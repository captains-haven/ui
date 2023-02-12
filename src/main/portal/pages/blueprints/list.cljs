(ns portal.pages.blueprints.list
  (:require
   [portal.components.link-btn :refer [$link-btn]]
   [portal.auth :refer [is-logged-in]] 
   [portal.components.blueprints.list :refer [$blueprints-list]]))

(defn $blueprints-list-page [{:keys [_id]}]
  [:div
   [:div
    {:style {:display "flex"
             :justify-content "space-between"
             :padding 10}}
    [:h2 "All Blueprints"]
    (when (is-logged-in)
      [$link-btn "Upload a new Blueprint" "/blueprints/new"])]
   [$blueprints-list]])