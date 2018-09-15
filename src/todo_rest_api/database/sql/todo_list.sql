-- :name create-task-table
-- :command :execute
-- :result :raw
create table task (
  id         varchar(100),
  name       varchar(100),
  status     varchar(100),
  task_date  date
)

-- :name insert-task :! :n
insert into task (id, name, status, task_date)
values (:id, :name, :status, :task_date)

-- :name all-task :?
select * from task

-- :name task-by-id :? :1
select * from task where id = :id