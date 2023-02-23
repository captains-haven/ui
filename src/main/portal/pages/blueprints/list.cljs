(ns portal.pages.blueprints.list
  (:require
   [reagent.core :as r]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer [<p!]]
   [portal.components.link-btn :refer [$link-btn]]
   [portal.auth :refer [is-logged-in]]
   [portal.http :refer [fetch-resource-with-sort-and-search]]
   [portal.router :refer [go-to]]
   [portal.components.error :refer [$error]]
   [portal.components.text-input :refer [$text-input]]
   [portal.components.forms.select :refer [$select]]
   [portal.components.blueprints.list :refer [$blueprints-list-new]]))

(def sort-by-options
  {"latest" "id"
   "views" "views"
   "rating" "average_rating_score"})

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

(defn handle-fetch-resources [loading? error items sort-by direction search-term]
  (reset! loading? true)
  (reset! error nil)
  (reset! items [])
  (go
    (let [found-items (<p! (fetch-resource-with-sort-and-search
                            "blueprints"
                            (get
                             sort-by-options
                             sort-by)
                            direction
                            (.decodeURIComponent
                             js/window
                             search-term)))]
      (if (:error found-items)
        (reset! error (:error found-items))
        (reset! items (transform-items (:data found-items))))
      (reset! loading? false))))

(defn $search-input [sort-by direction search-term]
  (let [type-timeout (r/atom nil)
        text-input (r/atom search-term)
        navigate (fn [new-search-term]
                   (if (= new-search-term "")
                     (go-to (str "/blueprints/sorted-by/" sort-by "/" direction))
                     (go-to (str "/blueprints/sorted-by/"
                                 sort-by
                                 "/"
                                 direction
                                 "/search/"
                                 (.encodeURIComponent
                                  js/window
                                  new-search-term)))))]
   (r/create-class
    {:component-did-update
     (fn [this [_ _ _ old-search-term]]
       (let [[_ _ _ new-search-term] (r/argv this)]
         (when (not= old-search-term new-search-term)
           (reset! text-input new-search-term))))
     :reagent-render
     (fn [sort-by direction search-term]
       [$text-input
        {:label "Search:"
         :value (.decodeURIComponent
                 js/window
                 @text-input)
         :onKeyUp (fn [ev]
                    (when (= (.-keyCode ev) 13)
                      (let [new-val (-> ev .-target .-value)]
                        (navigate new-val))))
         :onChange (fn [ev]
                     (let [new-val (-> ev .-target .-value)]
                       (reset! text-input new-val)
                       (when @type-timeout
                         (.clearTimeout js/window @type-timeout)
                         (reset! type-timeout nil))
                       (reset! type-timeout
                               (.setTimeout
                                js/window
                                (fn []
                                  (navigate (-> ev .-target .-value)))
                                1000))))
         :input-style {:margin-left 5
                       :width 215}
         :container-el :span
         :placeholder "Search for title, description or author"
         :label-el :span}])})))

(defn $blueprints-list-page [{:keys [sort-by direction search-term]
                              :or {sort-by "latest"
                                   direction "desc"
                                   search-term ""}}]
  (let [loading? (r/atom true)
        error (r/atom nil)
        items (r/atom [])]
    (r/create-class
     {:component-did-mount
      (fn []
        (handle-fetch-resources loading? error items sort-by direction search-term))
      :component-did-update
      (fn [this [_ old-args]]
        (let [[_ new-args] (r/argv this)]
          (when (or (not= (:sort-by old-args)
                          (:sort-by new-args))
                    (not= (:direction old-args)
                          (:direction new-args))
                    (not= (:search-term old-args)
                          (:search-term new-args)))
            (handle-fetch-resources
             loading?
             error
             items
             (or (:sort-by new-args) "latest")
             (or (:direction new-args) "desc")
             (or (:search-term new-args) "")))))
      :reagent-render
      (fn [{:keys [sort-by direction search-term]
            :or {sort-by "latest"
                 direction "desc"
                 search-term ""}}]
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
              (if (= search-term "")
                (go-to (str "/blueprints/sorted-by/" new-sort-by "/" direction))
                (go-to (str "/blueprints/sorted-by/" new-sort-by "/" direction "/search/" search-term))))
            [{:name "Latest"
              :value "latest"}
             {:name "Rating"
              :value "rating"}
             {:name "Views"
              :value "views"}]]]
          [:span
           {:style {:margin-right 15}}
           [$select
            "Direction:"
            direction
            (fn [new-direction]
              (if (= search-term "")
                (go-to (str "/blueprints/sorted-by/" sort-by "/" new-direction))
                (go-to (str "/blueprints/sorted-by/" sort-by "/" new-direction "/search/" search-term))))
            [{:name "Descending"
              :value "desc"}
             {:name "Ascending"
              :value "asc"}]]]
          [:span
           [$search-input
            sort-by
            direction
            search-term]]]
         (when @error
           [:div
            [$error
             "Error code "
             (:status @error)
             " - "
             (:message @error)
             [:br] [:br]
             "Please retry in a bit or contact "
             [:a
              {:href "mailto:hello@captains-haven.org"}
              "hello@captains-haven.org"]
             " if the issue persists."]])
         (when (and (not= search-term "")
                    (= (count @items) 0)
                    (not @loading?))
           [:div
            {:style {:margin-left 10
                     :margin-top 15
                     :font-size 20}}
            "No results found for that search term..."])
         [$blueprints-list-new @items @loading?]])})))