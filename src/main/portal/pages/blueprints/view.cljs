(ns portal.pages.blueprints.view
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [clojure.pprint :refer [pprint]]
   [reagent.core :as r]
   [portal.http :refer [fetch-resource-by-slug create-resource fetch-resource-with-qs]]
   [portal.auth :refer [is-logged-in current-user-id current-username]]
   [portal.components.error :refer [$error]]
   [portal.components.loading :refer [$loading]]
   [portal.metatags :refer [add-or-edit-metadata!]] 
   [portal.components.blueprint :refer [$blueprint-new]]
   [portal.components.blueprints.items-list :refer [$blueprint-items]]
   [portal.components.reviews.new-review :refer [$new-review]]
   [portal.components.reviews.review-list :refer [$review-list]]))

(defn set-metadata! [{:keys [title author description thumbnail]}]
  (add-or-edit-metadata!
   :title
   (str title " Blueprint - Captain's Haven"))
  (add-or-edit-metadata!
   :og_title
   (str "Blueprint: " title " by " (:username author)))
  (add-or-edit-metadata!
   :og_image
   (str
    "https://uploads.captains-haven.org"
    (-> thumbnail :url)))
  (if (and description (not= description ""))
    (do
      (add-or-edit-metadata!
       :description
       (str title " blueprint for Captain of Industry"))
      (add-or-edit-metadata!
       :og_description
       description))
    (add-or-edit-metadata! :og_description "User blueprint for Captain of Industry")))

(defn flatten-item [item]
  (merge {:id (:id item)}
         (:attributes item)))

(defn transform-items [items]
  (reduce (fn [acc item]
            (let [flat-item (flatten-item item)]
              (conj acc (merge flat-item
                               {:author (flatten-item (-> flat-item :author :data))
                                :thumbnail (flatten-item (-> flat-item :thumbnail :data))}))))
          []
          items))

(defn fetch-reviews [blueprint_id]
  (.then
   (fetch-resource-with-qs
    "blueprint-ratings"
    {:filters {:blueprint {:id blueprint_id}}
     :populate "*"
     :sort "createdAt:desc"})
   #(transform-items (:data %)))
  )

(comment
  (go
    (pprint (<p! (fetch-reviews 157)))))

(defn $blueprints-view-page [{:keys [slug]}]
  (let [loading? (r/atom true)
        submitting-review? (r/atom false)
        item (r/atom nil)
        reviews (r/atom [])
        blueprint-data (r/atom nil)
        fetch-blueprint (fn []
                         (go
                           (let [found-item (<p! (fetch-resource-by-slug "blueprints" slug))
                                 found-ratings (<p! (fetch-reviews (:id found-item)))]
                             (set-metadata! found-item)
                             (reset! item found-item)
                             (reset! blueprint-data (:blueprint_data found-item))
                             (reset! reviews found-ratings)
                             (reset! loading? false))))
        submit-review (fn [data]
                        (reset! submitting-review? true)
                        (go
                          (let [res (<p! (create-resource "blueprint-ratings" data))]
                            (reset! submitting-review? false)
                            (if (:error res)
                              (.alert js/window (with-out-str (pprint (:error res))))
                              (fetch-blueprint)))))]
    (r/create-class
     {:component-did-mount
      (fn []
        (fetch-blueprint))
      :component-will-unmount
      (fn []
        (add-or-edit-metadata!
         :title
         (str "Captain's Haven - Mods, Blueprints and Maps for Captain of Industry")))
      :reagent-render
      (fn [{:keys [_slug]}]
        [:div.blueprint-page
         (if @loading?
           [:div
            {:style {:margin "10px auto"
                     :width 100
                     :text-align "left"}}
            [$loading]]
           [:div
            (when (:is_archived @item)
              [:div
               {:style {:margin "30px auto"
                        :width 670}}
               [$error
                [:div
                 "This blueprint have been archived. This means the author have indicated you shouldn't use this anymore. "
                 (when (and (:replacement_blueprint_url @item)
                            (not= (:replacement_blueprint_url @item) ""))
                   [:a
                    {:style {:color "rgb(18, 37, 187)"
                             :text-decoration "underline"}
                     :href (:replacement_blueprint_url @item)}
                    "Replacement blueprint"])]]])
            [:div.blueprint-page-layout
             [$blueprint-new @item true]
             (when @blueprint-data
               [$blueprint-items @blueprint-data])
             [:div.blueprint-reviews
              {:style {:max-width 450
                       :flex-shrink 1
                       :min-width 200
                       }}
              (when (and (is-logged-in)
                         (not (some (fn [author_id]
                                      (= author_id (current-user-id)))
                                    (map #(-> % :author :id)
                                         @reviews)))
                         (not (=
                               (-> @item :author :username)
                               (current-username))))
                [:div
                 {:style {:margin-bottom 20}}
                 [$new-review
                  {:loading? @submitting-review?
                   :on-submit submit-review
                   :author_id (current-user-id)
                   :blueprint_id (:id @item)}]])
              [$review-list @reviews]]]])])})))