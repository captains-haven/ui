(ns portal.pages.auth.login
  (:require
   [portal.localstorage :refer [ls-set]]
   [portal.auth :refer [verify-login trigger-login]]

   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [portal.components.loading :refer [$loading]]
   [portal.components.btn :refer [$btn]]
   [portal.components.link :refer [$link]]
   [portal.components.text-input :refer [$text-input]]

   [reagent.core :as r]))

(defn trigger-login! []
  (let [query-param js/window.location.search
        splitted (.split query-param "=")
        login-token (second splitted)]
    (when login-token
      (go
        (let [res (<p! (verify-login login-token))]
          (ls-set "auth-token" (:jwt res))
          (ls-set "user" (:user res))
          (set! js/window.location "/")
          ;; (set! js/window.location.pathname "/")
          )))))

(defn $login-page []
  (let [s (r/atom {:username ""
                   :email ""
                   :res nil
                   :loading? false})
        is-loading (fn [] (:loading? @s))
        handle-change (fn [k]
                        (fn [ev]
                          (let [v (-> ev .-target .-value .trim)]
                            (swap! s assoc k v))))
        submit-signup (fn []
                        (swap! s assoc :loading? true)
                        (go
                          (let [res (<p! (trigger-login (:email @s)
                                                        (:username @s)))]
                            (swap! s assoc :res res :loading? false))))]
    (r/create-class
     {:component-did-mount
      (fn []
        (go (trigger-login!)))
      :reagent-render
      (fn []
        [:div.signup-page
         [:div
          {:style {:max-width 600}}
          [:h2 "Login"]
          (if (is-loading)
            [$loading]
            (if (-> @s :res :sent)
              [:div
               "Login link have been sent to your email inbox. Please click on the link to log in."]
              [:div
               (when-let [err (-> @s :res :error)]
                 (if (= (:message err) "wrong.email")
                   [:div "That user does not exist..."]
                  [:div
                   [:div (:name err)]
                   [:div (:message err)]]))
               [$text-input {:label "Username"
                             :disabled (is-loading)
                             :value (:username @s)
                             :onChange (handle-change :username)
                             :type "text"
                             :placeholder "Username"}]
               [$btn {:label "Login"
                      :disabled (is-loading)
                      :onClick (fn []
                                 (println "Submitting")
                                 (submit-signup))}]]))]])})))