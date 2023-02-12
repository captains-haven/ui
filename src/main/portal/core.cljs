(ns portal.core
  (:require
   [portal.state :refer [app-state]]
   [portal.router :refer [setup-router!]]

   [portal.components.menu :refer [$menu]]
   [portal.components.link :refer [$link]]
   [portal.components.debug :refer [$debug]]
   
   [portal.components.error :refer [$error]]

   [portal.pages :as pages]

   [reagent.dom :as rdom]))

(defn $app []
  (let [$page-component (:page-component @app-state)
        page-args (:page-args @app-state)]
    [:div
     [$link
      [:div.app-title
       [:img
        {:alt "Captain's Haven Logo"
         :src "/favicon-32x32.png"}]
       "Captain's Haven"]
      "/home"]
     [$menu]
     [:div.page-wrapper
      [$page-component page-args]]
     [$debug @app-state]]))

(defn render []
  (rdom/render [$app] (js/document.getElementById "root")))

(defn -main []

  (pages/init!)
  (println "pages initialized")
  (setup-router!)
  (println "router setup")
  (render)
  (println "app rendered"))

(defn ^:dev/after-load start []
  (-main))

(.addEventListener
 js/window
 "DOMContentLoaded"
 (fn []
   (start)))