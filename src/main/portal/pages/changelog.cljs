(ns portal.pages.changelog
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [reagent.core :as r]
   
   [portal.components.changelog :refer [$changelog-list]]))

(defn fetch-changelog []
  (.then
   (.then
    (.fetch
     js/window
     "/changelog.json")
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn $changelog-page []
  (let [items (r/atom nil)]
   (r/create-class
    {:component-did-mount
     (fn []
       (go
         (reset! items (<p! (fetch-changelog)))))
     :reagent-render
     (fn []
       [:div
        [:h3
         {:style {:margin-bottom 30}}
         "Latest changes"]
        (if @items
          [$changelog-list @items]
          "Loading...")
        (when @items
         [:div "And before all of this, there was nothing..."])])})))