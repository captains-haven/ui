(ns portal.pages.news
  (:require
   [reagent.core :as r]
   [portal.http :refer [fetch-resource fetch-resource-by-old-slug]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer [<p!]]
   
   [portal.components.loading :refer [$loading]]
   [portal.components.link :refer [$link]]
   [portal.components.news.list :refer [$news-list]]
   [portal.components.news.item :refer [$news-item]]))

(defn flatten-item [item]
  (merge {:id (:id item)}
         (:attributes item)))

(defn fetch-item [s slug]
  (go
   (swap! s
          assoc
          :items []
          :loading? false
          :item (flatten-item (first (:data (<p! (fetch-resource-by-old-slug "news-items" slug))))))))

(defn fetch-items [s]
  (go
    (swap! s
           assoc
           :items (mapv flatten-item (:data (<p! (fetch-resource "news-items"))))
           :loading? false
           :item {})))

(defn $news-page [{:keys [slug]}]
  (let [state (r/atom {:loading? true
                       :items []
                       :item {}})]
   (r/create-class
    {:component-did-mount
     (fn []
       (if slug
         (fetch-item state slug)
         (fetch-items state)))
     :component-did-update
     (fn [this [_ old-args]]
       (let [[_ new-args] (r/argv this)]
         (when (not= (:slug old-args) (:slug new-args))
           (swap! state assoc :loading? true)
           (if (:slug new-args)
             (fetch-item state (:slug new-args))
             (fetch-items state)))))
     :reagent-render
     (fn [{:keys [slug]}]
       [:div.news-page
        (if (:loading? @state)
          [:div [$loading]]
          (if slug
            [:div
             [$link "Go back to all news" "/news"]
             [$news-item (:item @state)]]
            [:div
             [:h3
              {:style {:margin-bottom 50}}
              "Latest Captain's Haven News"]
             [$news-list (:items @state)]]))])})))