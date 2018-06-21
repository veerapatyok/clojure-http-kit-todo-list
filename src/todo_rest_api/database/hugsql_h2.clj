(ns todo-rest-api.database.hugsql-h2
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns "todo_rest_api/database/sql/todo_list.sql")