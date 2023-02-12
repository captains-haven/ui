(ns portal.pages.blueprints.view
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]] 
   [reagent.core :as r]
   [portal.http :refer [fetch-resource-by-slug]]
   [portal.components.blueprint :refer [$blueprint]]
   [portal.components.blueprints.items-list :refer [$blueprint-items]]))

(defn $blueprints-view-page [{:keys [slug]}]
  (let [item (r/atom nil)
        blueprint-data (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-item (<p! (fetch-resource-by-slug "blueprints" slug))]
            (reset! item {:attributes found-item})
            (reset! blueprint-data (:blueprint_data found-item)))))
      :reagent-render
      (fn [{:keys [slug]}]
        [:div.blueprint-page
         [$blueprint @item true]
         (when @blueprint-data
           [$blueprint-items @blueprint-data])])})))