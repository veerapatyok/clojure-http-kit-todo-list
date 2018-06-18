(defproject todo-rest-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [http-kit "2.2.0"]
                 [ring/ring-core "1.7.0-RC1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [org.clojure/java.jdbc "0.7.6"]
                 [com.h2database/h2 "1.4.197"]
                 [clj-time "0.14.4"]]

  :aot  [todo-rest-api.core]
  :main todo-rest-api.core)
