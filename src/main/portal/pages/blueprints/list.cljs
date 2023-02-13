(ns portal.pages.blueprints.list
  (:require
   [portal.components.link-btn :refer [$link-btn]]
   [portal.auth :refer [is-logged-in]] 
   [portal.components.forms.select :refer [$select]]
   [portal.components.blueprints.list :refer [$blueprints-list $blueprints-list-new]]))

(def select-options
  [{:name "Latest"
    :value "latest"}
   {:name "Views"
    :value "views"}])

(defn $blueprints-list-page []
  [:div
   [:div
    {:style {:display "flex"
             :justify-content "space-between"
             :padding 10}} 
    [:h2 "All Blueprints"]
    (when (is-logged-in)
      [$link-btn "Upload a new Blueprint" "/blueprints/new"])] 
   [:div
    [$select "Sort By:" "latest" (fn []) select-options]]
   [$blueprints-list]])