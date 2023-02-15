(ns portal.router
  (:require
   [portal.state :refer [app-state]]
   [bide.core :as bide]))

(def routes
  (bide/router [["/home" :home]
                ["/mods" :mods]
                ["/mods/:id" :mods]
                ["/blueprints" :blueprints]
                
                ["/blueprints/sorted-by/:sort-by/:direction" :blueprints]
                
                ["/blueprints/new" :blueprints-new]
                ["/blueprints/:slug" :blueprint]
                ["/blueprints/:slug/edit" :blueprint-edit]
                ["/game-versions" :game-versions]
                ["/maps" :maps]
                ["/savegames" :savegames]
                ["/media" :media]
                ["/about" :about]
                ["/changelog" :changelog]
                ["/privacy-policy" :privacy-policy]
                ["/terms-and-conditions" :terms-and-conditions]
                ["/signup" :signup]
                ["/login" :login]
                ["/news" :news]
                ["/news/:slug" :news]]))

(defn get-page-from-path [path]
  (let [match (bide/match routes path)]
    (get js/window.app_pages
         (first match)
         [:div
          [:h1 "Page not found"]])))

(defn get-args-from-path [path]
  (let [match (bide/match routes path)]
    (or
     (second match)
     {})))

(comment
  (bide/match routes "/home")
  (get-args-from-path "/home"))

(defn go-to [path]
  ;; (println "sending pushState" path)
  (js/window.history.pushState
   #js{}
   ""
   path)
  (swap! app-state assoc
         :path path
         :page-component (get-page-from-path path)
         :page-args (get-args-from-path path)))

(defn ev-go-to [path]
  (fn [ev]
    (.preventDefault ev)
    (go-to path)))

(defn update-from-client-location! []
  (swap! app-state assoc
         :path js/window.location.pathname
         :page-component (get-page-from-path js/window.location.pathname)
         :page-args (get-args-from-path js/window.location.pathname)))

(defn listen-for-popstate! []
  (.addEventListener
   js/window
   "popstate"
   (fn []
     (swap! app-state assoc
            :path js/window.location.pathname
            :page-component (get-page-from-path js/window.location.pathname)
            :page-args (get-args-from-path js/window.location.pathname)))))

(defn setup-router! []
  (update-from-client-location!)
  (listen-for-popstate!)
  (when (= js/window.location.pathname "/")
    (go-to "/home")))