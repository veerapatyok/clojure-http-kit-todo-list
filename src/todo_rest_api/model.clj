(ns todo-rest-api.model)

(defrecord Create-Task [:task-name :status])

(defrecord Update-Task [:id :task-name :status :date])