(ns toggl-timesheet.core
  (:require [clojure.string :as cstr]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [camel-snake-kebab.core :as csk]
            [doric.core :refer [table]]))

(def toggl
  {:user-url    "https://www.toggl.com/api/v8/me"
   :summary-url "https://www.toggl.com/reports/api/v2/summary"})

(defn- find-workspace-id
  [{:keys [workspaces workspace-name]}]
  (->> workspaces
       (filter #(= (:name %) workspace-name))
       first
       :id))

(defn- get-user
  [{:keys [api-token] :as user}]
  (as->
    (-> (:user-url toggl)
        (http/get {:accept       :json
                   :basic-auth   [api-token "api_token"]})
        :body
        (json/read-str :key-fn csk/->kebab-case-keyword)
        :data)
    data

    (select-keys data [:id
                       :email
                       :fullname
                       :api-token
                       :workspaces])
    (merge data user)
    (assoc data :workspace-id (find-workspace-id data))
    (dissoc data :workspaces)))

(defn- get-summary
  [user from to]
  (-> (:summary-url toggl)
      (http/get {:accept       :json
                 :basic-auth   [(:api-token user) "api_token"]
                 :query-params {"workspace_id" (:workspace-id user)
                                "user_agent"   (:email user)
                                "user_ids"     (:id user)
                                "order_field"  "duration"
                                "grouping"     "users"
                                "since"        from
                                "until"        to}})
      :body
      (json/read-str :key-fn csk/->kebab-case-keyword)
      :data
      first
      (assoc :since from)
      (assoc :until to)
      (assoc :user user)))

(defn- sort-entries
  [{:keys [items] :as summary}]
  (sort-by :time #(> %1 %2) items))

(defn- ->hours
  [ms]
  (float (/ ms (* 1000 60 60))))

(defn- total-hours
  [{:keys [time] :as summary}]
  (->hours time))

(defn- billable-amount
  [{:keys [user] :as summary}]
  (* (total-hours summary) (:hourly-rate user)))

(defn- timesheet-table
  [summary]
  (table
    [{:name  :time
      :title "Hours"
      :align :right
      :format #(format "%.2f" (->hours %))}
     {:name  :title
      :title "Task"
      :format :time-entry
      :align :left}]
    (sort-entries summary)))

(defn- present-timesheet
  [{:keys [user since until] :as summary}]
  (str
    (:fullname user)
    " clocked a total of "
    (format "%.2f" (total-hours summary))
    " hours in the "
    (:workspace-name user)
    " workspace from "
    since
    " to "
    until
    ".\nThis amounts to "
    (format "%.2f€" (billable-amount summary))
    " at the rate of "
    (:hourly-rate user)
    "€ per hour.\n\n"
    (timesheet-table summary)
    "\n"))

;; TODO: store personnal info into .env
(def user-env
  "Find your API token in your user settings on Toggl"
  {:api-token      "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
   :workspace-name "Automation"
   :hourly-rate    60
   :currency       "€"})

;; TODO: deduce from and to dates as "last month" from the current date
(defn print-report
  [from to]
  (let [user    (get-user user-env)
        summary (get-summary user from to)]
    (present-timesheet summary)))

;; TODO: distribute the report via a web service
;; TODO: plug a Telegram/RocketChat bot to the service

(comment
  (print-report "2019-01-01" "2019-01-31"))
