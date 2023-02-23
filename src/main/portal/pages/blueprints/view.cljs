(ns portal.pages.blueprints.view
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [clojure.pprint :refer [pprint]]
   [reagent.core :as r]
   [portal.http :refer [fetch-resource-by-slug create-resource fetch-resource-with-qs]]
   [portal.auth :refer [is-logged-in current-user-id]]
   [portal.components.error :refer [$error]]
   [portal.components.loading :refer [$loading]]
   [portal.metatags :refer [add-or-edit-metadata!]] 
   [portal.components.blueprint :refer [$blueprint-new]]
   [portal.components.blueprints.items-list :refer [$blueprint-items]]
   [portal.components.reviews.new-review :refer [$new-review]]
   [portal.components.reviews.review-list :refer [$review-list]]))

(defn set-metadata! [{:keys [title author description thumbnail]}]
  (add-or-edit-metadata!
   :og_title
   (str "Blueprint: " title " by " (:username author)))
  (add-or-edit-metadata!
   :og_image
   (str
    "https://uploads.captains-haven.org"
    (-> thumbnail :url)))
  (if (and description (not= description ""))
    (add-or-edit-metadata!
     :og_description
     description)
    (add-or-edit-metadata! :og_description "User blueprint for Captain of Industry")))

(defn submit-review [data]
  (go
    (let [res (<p! (create-resource "blueprint-ratings" data))]
      (pprint res))))

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
        item (r/atom nil)
        reviews (r/atom [])
        blueprint-data (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-item (<p! (fetch-resource-by-slug "blueprints" slug))
                found-ratings (<p! (fetch-reviews (:id found-item)))]
            (set-metadata! found-item)
            (reset! item found-item)
            (reset! blueprint-data (:blueprint_data found-item))
            (reset! reviews found-ratings)
            (reset! loading? false))))
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
             [:div.blueprint-reviews
              {:style {:max-width 450
                       :flex-shrink 1
                       :min-width 200
                       }}
              (when (is-logged-in)
                [:div
                 {:style {:margin-bottom 20}}
                 [$new-review
                  {:on-submit submit-review
                   :author_id (current-user-id)
                   :blueprint_id (:id @item)}]])
              [$review-list @reviews]]
             (when @blueprint-data
               [$blueprint-items @blueprint-data])]])])})))