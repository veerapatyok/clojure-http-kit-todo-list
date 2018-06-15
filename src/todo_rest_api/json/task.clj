(ns todo-rest-api.json.task
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

(defn validate-date
  [date]
  (validate-data (s/valid? date-format? date) "invalid date"))

(defn validate-task-name
  [task-name]
  (validate-data (s/valid? (complement nil?) task-name) "invalid task-name"))

(defn validate-status
  [status]
  (validate-data (s/valid? status? status) "invalid status"))

(defn validate-task-json
  [date task-name status]
  (filter #(not-empty %) [(validate-date date) (validate-task-name task-name) (validate-status status)]))