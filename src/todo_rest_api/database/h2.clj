(ns todo-rest-api.database.h2
  (:gen-class)
  (:use [clojure.java.jdbc :as j]))

(def h2-setting
  {
   :classname   "org.h2.Driver"
   :subprotocol "h2:mem"
   :subname     "demo;DB_CLOSE_DELAY=-1"
   :user        ""
   :password    ""})

(defn create-tasks-table
  []
  (j/db-do-commands h2-setting
                    (j/create-table-ddl :task
                                        [[:id "varchar(100)"]
                                         [:name "varchar(100)"]
                                         [:status "varchar(100)"]
                                         [:task_date "date"]])))

(defn query-by-h2-setting
  [query]
  (j/query h2-setting query))

(defn execute-by-h2-setting
  [query]
  (j/execute! h2-setting query))