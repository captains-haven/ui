(ns portal.components.blueprints.list
  (:require
   
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   
   [reagent.core :as r]
   
   [portal.http :refer [fetch-resource]]
   [portal.components.loading :refer [$loading]]
   [portal.components.debug :refer [$debug]]
   [portal.components.blueprint :refer [$blueprint]]))

(defn $blueprints-list []
  (let [loading? (r/atom false)
        items (r/atom [])]
    (r/create-class
     {:component-did-mount
      (fn []
        (reset! loading? true)
        (go
          (let [found-items (<p! (fetch-resource "blueprints"))]
            (reset! items (reverse (sort-by :id (:data found-items))))
            (reset! loading? false))))
      :reagent-render
      (fn []
        [:div
         (if @loading?
           [:div
            [$loading]]
           [:div.blueprint-list
            (doall
             (map (fn [i]
                    ^{:key (:id i)}
                    [:div.blueprint-list-item
                     [$blueprint i false]])
                  @items))])
         [$debug @items]])})))