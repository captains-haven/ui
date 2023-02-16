(ns portal.pages.blueprints.view
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]] 
   [reagent.core :as r]
   [portal.http :refer [fetch-resource-by-slug]]
   [portal.metatags :refer [add-or-edit-metadata!]]
   [portal.components.blueprint :refer [$blueprint-new]]
   [portal.components.blueprints.items-list :refer [$blueprint-items]]))

(defn set-metadata! [{:keys [title author description thumbnail]}]
  (add-or-edit-metadata!
   :og_title
   (str "Captain's Haven - Blueprint: " title " by " (:username author)))
  (add-or-edit-metadata!
   :og_image
   (str
    "https://uploads.captains-haven.org"
    (-> thumbnail :formats :thumbnail :url)))
  (if (and description (not= description ""))
    (add-or-edit-metadata!
     :og_description
     (str "Description: " description))
    (add-or-edit-metadata! :og_description "User blueprint for Captain of Industry")))

(defn $blueprints-view-page [{:keys [slug]}]
  (let [item (r/atom nil)
        blueprint-data (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-item (<p! (fetch-resource-by-slug "blueprints" slug))]
            (set-metadata! found-item)
            (reset! item found-item)
            (reset! blueprint-data (:blueprint_data found-item)))))
      :reagent-render
      (fn [{:keys [_slug]}]
        [:div.blueprint-page
         [$blueprint-new @item true]
         (when @blueprint-data
           [$blueprint-items @blueprint-data])])})))