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
   [portal.components.blueprint :refer [$blueprint-new]]
   [portal.components.forms.checkbox :refer [$checkbox]]))

(defn $blueprints-edit-page [{:keys [slug]}]
  (let [is-loading (r/atom false)
        item (r/atom nil)
        update-blueprint
        (fn []
          (go
            (let [to-submit (-> @item
                                (assoc :thumbnail (or (:new-thumbnail @item)
                                                      (-> @item :thumbnail :id))))
                  res (<p! (update-resource "blueprints" to-submit (:id @item)))]
              (go-to (str "/blueprints/" (-> res :data :attributes :slug))))))]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-item (<p! (fetch-resource-by-slug "blueprints" slug))]
            (reset! item found-item))))
      :reagent-render
      (fn [{:keys [_slug]}]
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
                        :value (:blueprint_data @item)
                        :placeholder "B2416:H4sIAAAAAAAACnVWzW8bxxV...."}]
          [$text-input {:label "Title"
                        :disabled true ;; can never update title has we use it for urls 
                        :value (:title @item)
                        :placeholder "Blueprint Title"}]
          [:div
           {:style {:margin-top 10
                    :margin-bottom 10}}
           [$text-input {:label "Description"
                         :disabled @is-loading
                         :onChange (handle-text-input-change-nested item [:description])
                         :value (:description @item)
                         :multiline true
                         :placeholder "Description"}]]
          [:label
           {:style {:margin-top 10
                    :margin-bottom 10}}
           [:div "Thumbnail"]
           [$file-upload
            {:disabled @is-loading
             :onStart (fn []
                        (reset! is-loading true))
             :onDone (fn [res]
                       (swap! item assoc :new-thumbnail (-> res first :id))
                       (let [full-url (-> res first :url)
                             thumbnail-url (-> res first :formats :thumbnail :url)]
                         (swap! item assoc :thumbnail {:url full-url
                                                       :formats {:thumbnail {:url thumbnail-url}}}))
                       (reset! is-loading false))}]]
          [:div
           {:style {:margin-top 10
                    :margin-bottom 10}}
           [$checkbox
            "Archive?"
            (:is_archived @item)
            (fn [checked?]
              (swap! item assoc :is_archived checked?))]
           [:div
            {:style {:margin-top 5
                     :font-size 14}}
            [:small
             "Archiving a blueprint means it won't be visible on the main blueprints page or in the search, but people can still see the blueprint if they navigate to the complete url"]]
           (when (:is_archived @item)
             [:div
              {:style {:margin-top 10
                       :margin-bottom 10}}
              [$text-input {:label "Replacement blueprint"
                            :value (:replacement_blueprint_url @item)
                            :placeholder "URL to new blueprint"
                            :onChange (fn [ev]
                                        (let [new-val (-> ev .-target .-value)]
                                          (swap! item assoc :replacement_blueprint_url new-val)))}]])]
          [:div
           {:style {:margin-top 15}}
           [:button.link-btn
            {:onClick update-blueprint}
            "Update"]]]
         [:div
          {:style {:margin-left 30}}
          [:h2 "Preview"]
          [$blueprint-new @item true]
          [$debug @item]]])})))