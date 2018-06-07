(ns todo-rest-api.service
  (:gen-class)
  (:use [todo-rest-api.h2]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn timestamp [] (. (java.time.Instant/now) toEpochMilli))

(defn get-tasks-by-id
  [id]
  (-> (query-by-h2-setting (str "select * from task where id=" "'" id "'"))
      (first)))

(defn insert-task-by-req
  [req]
  (let [id (uuid)
        date (timestamp)
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
        date (timestamp)
        task-name (get-in req [:body :name])
        status (get-in req [:body :status])]
    (execute-by-h2-setting (str "update task set name=" "'" task-name "'" "," "status='" status "'" "where id=" "'" id "'"))
    {:id     id
     :name   task-name
     :status status
     :date   date}))