(ns todo-rest-api.core
  (:gen-class)
  (:use [compojure.core :only [defroutes GET POST PUT DELETE]]
        [ring.middleware.json :as middleware]
        [ring.util.response :only [response not-found]]
        [todo-rest-api.service]
        [todo-rest-api.database.h2 :only [create-tasks-table h2-setting]]
        [todo-rest-api.database.hugsql-h2 :as task-sql]
        org.httpkit.server))

(defroutes all-routes
           (GET "/ping" [] (response {:data "ok"}))
           (POST "/tasks" req (insert-task-by-req req))
           (DELETE "/tasks/:id" [id] (delete-task-by-id id))
           (PUT "/tasks/:id" req (update-task-by-id req))
           (GET "/tasks/:id" [id] (get-tasks-by-id id)))

(def app
  (-> all-routes
      (middleware/wrap-json-body {:keywords? true :bigdecimals? true})
      (middleware/wrap-json-response)))

(defn -main [& args]
  (task-sql/create-task-table h2-setting)
  (run-server app {:port 8080}))
