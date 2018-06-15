(ns todo-rest-api.service
  (:gen-class)
  (:use [todo-rest-api.database.h2]
        [ring.util.response :only [response not-found bad-request]]
        [todo-rest-api.json.task :only [validate-task-json uuid]]))

(defn get-tasks-by-id
  [id]
  (let [res (query-by-h2-setting (str "select * from task where id=" "'" id "'"))]
    (if (empty? res)
      (not-found {:errors ["not found"]})

      (response {:data (first res)}))))

(defn insert-task-by-req
  [req]
  (let [id (uuid)
        date (get-in req [:body :date])
        task-name (get-in req [:body :name])
        status (get-in req [:body :status])
        errors (validate-task-json date task-name status)]

    (cond
      (empty? errors) (second [(execute-by-h2-setting
                                 (str "insert into task(id, name, status, task_date) values(" "'" id "'" "," "'" task-name "'" "," "'" status "'" "," "'" date "'" ")"))
                               (response {:data {:id        id
                                                 :name      task-name
                                                 :status    status
                                                 :task_date date}})])
      :else (bad-request {:errors errors}))))

(defn delete-task-by-id
  [id]
  (if (> (first (execute-by-h2-setting (str "delete from task where id=" "'" id "'"))) 0)
    (second [(execute-by-h2-setting (str "delete from task where id=" "'" id "'"))
             (response {:data {:id id}})])

    (not-found {:errors ["not found"]})))

(defn update-task-by-id
  [req]
  (let [id (get-in req [:params :id])
        date (get-in req [:body :date])
        task-name (get-in req [:body :name])
        status (get-in req [:body :status])
        errors (validate-task-json date task-name status)]

    (cond
      (empty? errors) (if (> (first (execute-by-h2-setting
                                      (str "update task set name=" "'" task-name "'" "," "status=" "'" status "'" "," "task_date=" "'" date "'" "where id=" "'" id "'"))) 0)
                        (response {:data {:id        id
                                          :name      task-name
                                          :status    status
                                          :task_date date}})

                        (not-found {:errors ["not found"]}))

      :else (bad-request {:errors errors}))))