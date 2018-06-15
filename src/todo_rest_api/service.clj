(ns todo-rest-api.service
  (:gen-class)
  (:use [todo-rest-api.database.h2]
        [ring.util.response :only [response not-found bad-request]])
  (:require [clojure.spec.alpha :as s]
            [clj-time [format :as f]]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(def all-status
  ["success", "failure"])

(defn status?
  [status]
  (s/valid? (fn [x] (some #(= % x) all-status)) status))

(defn date-format?
  [date]
  (let [date? #(try (f/parse (f/formatter "YYYY-MM-dd") %) true (catch Exception e false))]
    (s/valid? date? date)))

(defn validate-data
  [valid? error]
  (if valid? "" error))

(defn validate-task-json
  [date task-name status]
  (filter #(not-empty %)
          [(validate-data (s/valid? date-format? date) "invalid date")
           (validate-data (s/valid? (complement nil?) task-name) "invalid task-name")
           (validate-data (s/valid? status? status) "invalid status")]))

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