(defproject toggl-timesheet "0.1.0-SNAPSHOT"
  :description "Extract your monthly timesheet from Toggl"
  :url "https://github.com/em-ak/toggl-timesheet"
  :license {:name "WTFPL"
            :url "https://spdx.org/licenses/WTFPL.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http  "3.10.0"]
                 [org.clojure/data.json  "0.2.6"]
                 [camel-snake-kebab  "0.4.0"]
                 [doric  "0.9.0"]])
