(ns portal.pages.auth.signup
  (:require
   [portal.auth :refer [trigger-login]]
   [portal.components.text-input :refer [$text-input]]
   [portal.components.btn :refer [$btn]]
   [portal.components.link :refer [$link]]
   [portal.components.loading :refer [$loading]] 

   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]

   [reagent.core :as r]))

(defn $signup-page []
  (let [accept? (r/atom false)
        s (r/atom {:username ""
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
     {:reagent-render
      (fn []
        [:div.signup-page
         [:div
          {:style {:max-width 400}}
          [:h2 "Signup"]
          (when-let [err (-> @s :res :error)]
            [:div
             [:div (:name err)]
             [:div (:message err)]])
          [$text-input {:label "Username"
                        :disabled (is-loading)
                        :value (:username @s)
                        :onChange (handle-change :username)
                        :type "text"
                        :placeholder "Username"}]
          [$text-input {:label "Email"
                        :disabled (is-loading)
                        :value (:email @s)
                        :onChange (handle-change :email)
                        :type "email"
                        :placeholder "email@example.com"}]
          [:label
           {:class (when (is-loading) "disabled")}
           [:input
            {:type "checkbox"
             :style {:margin-right 5}
             :onChange (fn [ev]
                         (let [v (-> ev .-target .-checked boolean)]
                           (reset! accept? v)))}]
           "Accept "
           [$link "Terms & Conditions" "/terms-and-conditions"]
           " and "
           [$link "Privacy Policy" "/privacy-policy"]
           "?"]
          [$btn {:label "Signup"
                 :disabled (or (not @accept?) (is-loading))
                 :onClick (fn []
                            (submit-signup))}]
          (when (is-loading)
            [$loading])
          (when (-> @s :res :sent)
            [:div
             "Email has been sent to " (-> @s :res :email) ". Please check your inbox to confirm."])]])})))