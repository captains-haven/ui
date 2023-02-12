(ns portal.pages.blueprints.edit
  (:require
   [reagent.core :as r]
   
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   
   [portal.router :refer [go-to]]
   [portal.http :refer [update-resource
                        fetch-resource-by-slug]]
   
   [portal.form-utils :refer [handle-text-input-change-nested]]
   [portal.components.debug :refer [$debug]]
   [portal.components.file-upload :refer [$file-upload]]
   [portal.components.text-input :refer [$text-input]]
   [portal.components.blueprint :refer [$blueprint]]))

(defn $blueprints-edit-page [{:keys [slug]}]
  (let [is-loading (r/atom false)
        item (r/atom nil)
        update-blueprint
        (fn []
          (go
            (let [to-submit (-> @item
                                :attributes
                                (assoc :thumbnail (or (:new-thumbnail @item)
                                                      (-> @item :attributes :thumbnail :data :id))))
                  res (<p! (update-resource "blueprints" to-submit (:id @item)))]
              (go-to (str "/blueprints/" (-> res :data :attributes :slug))))))]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-item (<p! (fetch-resource-by-slug "blueprints" slug))]
            (reset! item (first (:data found-item))))))
      :reagent-render
      (fn [{:keys [slug]}]
        [:div
         {:style {:display "flex"
                  :justify-content "center"
                  :align-items "start"}}
         [:div
          {:style {:width 250}}
          [:h2 "Edit Blueprint"]
          [:p
           "Warning: Blueprint Data nor Title can be updated."]
          [$text-input {:label "Blueprint Data"
                        :disabled true ;; can never update blueprint_data 
                        :value (-> @item :attributes :blueprint_data)
                        :placeholder "B2416:H4sIAAAAAAAACnVWzW8bxxV...."}]
          [$text-input {:label "Title"
                        :disabled true ;; can never update title has we use it for urls 
                        :value (-> @item :attributes :title)
                        :placeholder "Blueprint Title"}]
          [$text-input {:label "Description"
                        :disabled @is-loading
                        :onChange (handle-text-input-change-nested item [:attributes :description])
                        :value (-> @item :attributes :description)
                        :multiline true
                        :placeholder "Description"}]
          [:label
           [:div "Thumbnail"]
           [$file-upload
            {:disabled @is-loading
             :onStart (fn []
                        (reset! is-loading true))
             :onDone (fn [res]
                       (swap! item assoc :new-thumbnail (-> res first :id))
                       (let [url (-> res first :formats :thumbnail :url)]
                         (swap! item assoc-in [:attributes :thumbnail :data :attributes :url] url))
                       (reset! is-loading false))}]]
          [:div
           {:style {:margin-top 15}}
           [:button.link-btn
            {:onClick update-blueprint}
            "Update"]]]
         [:div
          {:style {:margin-left 30}}
          [:h2 "Preview"]
          [$blueprint @item true]]
         [$debug @item]])})))