(ns portal.components.blueprints.items-list
  (:require
   [reagent.core :as r]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer [<p!]]))

(defn $blueprint-items-list [items]
  (fn []
    (let [sorted-items (into (sorted-map-by (fn [key1 key2] (compare [(key2 items) key2] [(key1 items) key1]))) items)]
      [:div
       {:style {:margin 5
                :width 300}}
       [:div
        {:style {:font-size "22px"}}
        "Used items:"]
       [:div
        {:style {:display "flex"
                 :flex-direction "column"
                 :max-width 400}}
        (doall
         (map (fn [[k v]]
                [:div
                 {:style {:display "flex"
                          :flex-direction "row"
                          :justify-content "space-between"
                          :border-bottom "1px solid grey"
                          :margin-top 5
                          :padding-bottom 2
                          :margin-bottom 3}}
                 [:div.ellipsis-overflow
                  {:title k}
                  k]
                 [:div v]])
              sorted-items))]])))

(defn $blueprint-items [blueprint-data]
  (let [is-loading (r/atom false)
        items (r/atom {})
        fetch-items (fn [blueprint-data]
                      (reset! is-loading true)
                      (go
                        (let [res (<p! (js/window.fetch
                                        "https://parser.captains-haven.org/blueprint"
                                        #js{:method "POST"
                                            :body blueprint-data}))
                              parsed (<p! (.json res))
                              obj (js->clj parsed :keywordize-keys true)]
                          (reset! items (:items obj))
                          (reset! is-loading false))))]
    (r/create-class
     {:component-did-mount
      (fn [this] (fetch-items blueprint-data))
      :reagent-render
      (fn [blueprint-data]
        [:div.items-list
         (when-not (empty? @items)
           [$blueprint-items-list @items])])})))