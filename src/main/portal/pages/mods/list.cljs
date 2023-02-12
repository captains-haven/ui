(ns portal.pages.mods.list
  (:require
   [reagent.core :as r]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   
   [portal.http :refer [fetch-resource]]
   [portal.components.link :refer [$link]]
   [portal.components.debug :refer [$debug]]))

(defn $mod-list []
  (let [items (r/atom [])]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-items (<p! (fetch-resource "mods"))]
            (reset! items found-items))))
      :reagent-render
      (fn []
        [:div
         [:h1 "All Mods"]
         [:div "A list of all the mods available"]
         (doall
          (map (fn [i]
                 (let [attrs (:attributes i)]
                   [:div
                    [:h2
                     [$link
                      (:title attrs)
                      (str "/mods/" (:id i))]]
                    [:div
                     "By " (:author attrs)]
                    [:div
                     [:a
                      {:href (:source_code attrs)
                       :target "_blank"}
                      (:source_code attrs)]]
                    [:div.mod-image
                     [:img
                      {:width 300
                       :src (str "https://uploads.captains-haven.org" (-> attrs :thumbnail :data :attributes :url))}]]
                    [:div
                     "Published at: " (:publishedAt attrs)]
                    [:div
                     "Last Update: " (:updatedAt attrs)]]))
               (:data @items)))
         [$debug @items]])})))

(defn $mods-list-page []
  [:div
   "Mods"
;;    [$mod-list]
   [:div "Didn't find what you were looking for?"]
   [:div "Try here:"]
;;    [$link "Home" "/"]
   ])