(ns portal.pages.auth.login
  (:require
   [portal.localstorage :refer [ls-set]]
   [portal.auth :refer [verify-login]]
   
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]] 
   [clojure.pprint :refer [pprint]]
   
   [reagent.core :as r]))

(defn $login-page []
  (r/create-class
   {:component-did-mount
    (fn []
      (let [query-param js/window.location.search
            splitted (.split query-param "=")
            login-token (second splitted)]
        (when login-token
          (pprint login-token)
          (go
            (let [res (<p! (verify-login login-token))]
              (pprint res)
              (ls-set "auth-token" (:jwt res))
              (ls-set "user" (:user res))
              (set! js/window.location.pathname "/"))))))
    :reagent-render
    (fn []
      [:div
       "Trying to login are we?: "])}))