(ns todo-rest-api.core
  (:gen-class)
  (:use [compojure.core :only [defroutes GET POST PUT DELETE]]
        [ring.middleware.json :as middleware]
        [ring.util.response :only [response not-found]]
        [clojure.java.jdbc :as j]
        org.httpkit.server))

(def h2-setting
  {
   :classname   "org.h2.Driver"
   :subprotocol "h2:mem"
   :subname     "demo;DB_CLOSE_DELAY=-1"
   :user        ""
   :password    ""
   })

(defn create-tasks-table
  []
  (j/db-do-commands h2-setting
                    (j/create-table-ddl :task
                                        [[:id "varchar(100)"]
                                         [:name "varchar(100)"]
                                         [:status "varchar(100)"]
                                         [:date "int(15)"]])))

(defn query-by-h2-setting
  [query]
  (j/query h2-setting query))

(defn execute-by-h2-setting
  [query]
  (j/execute! h2-setting query))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn get-tasks-by-id
  [id]
  (-> (query-by-h2-setting (str "select * from task where id=" "'" id "'"))
      (first)))

(defn insert-task-by-req
  [req]
  (let [id (uuid)
        date 10
        task-name (get-in req [:body :name])
        status (get-in req [:body :status])]
    (execute-by-h2-setting (str "insert into task(id, name, status, date) values(" "'" id "'" "," "'" task-name "'" "," "'" status "'" "," date ")"))
    {:id     id
     :name   task-name
     :status status
     :date   date}))

(defn delete-task-by-id
  [id]
  (execute-by-h2-setting (str "delete from task where id=" "'" id "'"))
  {:id id})

(defn update-task-by-id
  [req]
  (let [id (get-in req [:params :id])
        date 10
        task-name (get-in req [:body :name])
        status (get-in req [:body :status])]
    (execute-by-h2-setting (str "update task set name=" "'" task-name "'" "," "status='" status "'" "where id=" "'" id "'"))
    {:id     id
     :name   task-name
     :status status
     :date   date}))

(defroutes all-routes
           (GET "/ping" [] (response {:data "ok"}))
           (POST "/tasks" req (response {:data (insert-task-by-req req)}))
           (DELETE "/tasks/:id" [id] (response {:data (delete-task-by-id id)}))
           (PUT "/tasks/:id" req (response {:data (update-task-by-id req)}))
           (GET "/tasks/:id" [id] (response {:data (get-tasks-by-id id)})))

(def app
  (-> all-routes
      (middleware/wrap-json-body {:keywords? true :bigdecimals? true})
      (middleware/wrap-json-response)))

(create-tasks-table)
(run-server app {:port 8080})
