(ns portal.auth
  (:require
   [portal.state :refer [app-state]]))

(defn is-logged-in []
  (boolean (:user @app-state)))

(defn current-user-id []
  (-> @app-state :user :id))

(defn current-username []
  (-> @app-state :user :username))

(defn trigger-login [email username]
  (.then
   (.then
    (js/window.fetch
     "https://admin.captains-haven.org/api/passwordless/send-link"
     #js{"method" "POST"
         "headers" #js{"Content-Type" "application/json"}
         "body" (js/JSON.stringify #js{"email" email
                                       "username" username})})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn verify-login [token]
  (.then
   (.then
    (js/window.fetch
     (str
      "https://admin.captains-haven.org/api/passwordless/login?loginToken="
      token))
    #(.json %))
   #(js->clj % :keywordize-keys true)))