(ns portal.pages.blueprints.list
  (:require
   [reagent.core :as r]
   [clojure.pprint :refer [pprint]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer [<p!]]
   [portal.components.link-btn :refer [$link-btn]]
   [portal.auth :refer [is-logged-in]]
   [portal.http :refer [fetch-resource-with-sort]]
   [portal.router :refer [go-to]]
   [portal.components.error :refer [$error]]
   [portal.components.forms.select :refer [$select]]
   [portal.components.blueprints.list :refer [$blueprints-list $blueprints-list-new]]))

(def sort-by-options
  {"latest" "id"
   "views" "views"})

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

(defn handle-fetch-resources [loading? error items sort-by direction]
  (reset! loading? true)
  (reset! error nil)
  (reset! items [])
  (go
    (let [found-items (<p! (fetch-resource-with-sort "blueprints" (get
                                                                   sort-by-options
                                                                   sort-by) direction))]
      (if (:error found-items)
        (reset! error (:error found-items))
        (reset! items (transform-items (:data found-items))))
      (reset! loading? false))))

(defn $blueprints-list-page [{:keys [sort-by direction]
                              :or {sort-by "latest"
                                   direction "desc"}}]
  (let [loading? (r/atom true)
        error (r/atom nil)
        items (r/atom [])]
   (r/create-class
    {:component-did-mount
     (fn []
       (handle-fetch-resources loading? error items sort-by direction))
     :component-did-update
     (fn [this [_ old-args]]
       (let [[_ new-args] (r/argv this)]
         (when (or (not= (:sort-by old-args)
                         (:sort-by new-args))
                   (not= (:direction old-args)
                         (:direction new-args)))
           (handle-fetch-resources
            loading?
            error
            items
            (or (:sort-by new-args) "latest")
            (or (:direction new-args) "desc")))))
     :reagent-render
     (fn [{:keys [sort-by direction]
           :or {sort-by "latest"
                direction "desc"}}]
       [:div
        [:div
         {:style {:display "flex"
                  :justify-content "space-between"
                  :padding 10}}
         [:h2 "All Blueprints"]
         (when (is-logged-in)
           [$link-btn "Upload a new Blueprint" "/blueprints/new"])]
        [:div
         {:style {:padding 10}}
         [:span
          {:style {:margin-right 15}}
          [$select
           "Sort By:"
           sort-by
           (fn [new-sort-by]
             (go-to (str "/blueprints/sorted-by/" new-sort-by "/" direction)))
           [{:name "Latest"
             :value "latest"}
            {:name "Views"
             :value "views"}]]]
         [:span
          [$select
           "Direction:"
           direction
           (fn [new-direction]
             (go-to (str "/blueprints/sorted-by/" sort-by "/" new-direction)))
           [{:name "Descending"
             :value "desc"}
            {:name "Ascending"
             :value "asc"}]]]]
        (when @error
          [:div
           [$error
            "Error code "
            (:status @error)
            " - "
            (:message @error)
            [:br][:br]
            "Please retry in a bit or contact "
            [:a
             {:href "mailto:hello@captains-haven.org"}
             "hello@captains-haven.org"]
            " if the issue persists."]])
        [$blueprints-list-new @items @loading?]])})))