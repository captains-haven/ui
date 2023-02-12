(ns audition.core
  (:require
   [portal.time :refer [simple-datetime]]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [clojure.edn :refer [read-string]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [bide.core :as bide]
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as md.transform] 
   
   [portal.components.link]
   [portal.components.error]
   [portal.components.debug])
  ;; (:require-macros
  ;;  [audition.macros :refer [into-var]]
  ;;  )
  )

(def $debug portal.components.debug/$debug)

(def ns-list
  '[portal.components.link])

(defn only-components [m]
  (into
   {}
   (filter (fn [[n v]]
            ;;  (println n)
             (str/starts-with? (name n) "$"))
           m)))

(comment
  (require '[portal.components.link])
  (ns-interns 'portal.components.link)
  (only-components (ns-interns 'portal.components.link))
  
  (meta (second (first (only-components (ns-interns 'portal.components.link)))))
  
  )

(defn list-components []
  (reduce
   (fn [acc [k v]]
     (let [m (meta v)]
       (assoc acc (str
                   (:ns m)
                   "/"
                   (name k)) m))
     )
   {}
   (only-components
    (merge
     {}
     (ns-interns 'portal.components.link)
     (ns-interns 'portal.components.error)
     (ns-interns 'portal.components.debug)))))

(comment
  (list-components))

(def app-state
  (r/atom {}))

(def router
  (bide/router [["/home" ::home]
                ["/components/:key" ::components]]))

(declare go-to)

(defn $home-page []
  [:div
   "Home"
   (map
    (fn [[n v]]
      (let [url (str "/components/" (.encodeURIComponent
                                 js/window
                                 n))]
       [:div
        [:a
         {:href url
          :onClick (fn [ev]
                     (.preventDefault ev)
                     (go-to url))}
         n]]))
    (list-components))])

(defn $component-page [{:keys [key]}]
  (let [decoded-key (.decodeURIComponent js/window key)]
   [:div
    [:div "Component"]
    [:div (str key)]
    [:div (.decodeURIComponent js/window key)]
    ;; [:div [(into-var decoded-key)]]
    ]))

(def pages
  {::home $home-page
   ::components $component-page})

(defn get-page-from-path [path]
  (let [match (bide/match router path)]
    (get pages (first match)
         [:div
          [:h1 "Page not found"]])))

(defn get-args-from-path [path]
  (let [match (bide/match router path)]
    (second match)))

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

(defn listen-for-popstate []
  (println "setting up listener")
  (.addEventListener
   js/window
   "popstate"
   (fn []
     (println "popstate happened")
     (swap! app-state assoc
            :path js/window.location.pathname
            :page-component (get-page-from-path js/window.location.pathname)
            :page-args (get-args-from-path js/window.location.pathname)))))

(defn $app []
  [:div
   [:a
    {:href "#"}
    [:div.app-title
     [:img
      {:alt "Captain's Haven Logo"
       :src "/favicon-32x32.png"}]
     "Captain's Haven"]]
  ;;  [$menu]
   [:div.page-wrapper
    [:h1 "Audition"]
    [(:page-component @app-state)
     (:page-args @app-state)]]
  [$debug @app-state]
   ])

(defn render []
  (rdom/render [$app] (js/document.getElementById "root")))

(defn -main []
  (swap! app-state assoc
         :path js/window.location.pathname
         :page-component (get-page-from-path js/window.location.pathname)
         :page-args (get-args-from-path js/window.location.pathname))
  (listen-for-popstate)
  (when (= js/window.location.pathname "/")
    (go-to "/home"))
  (render))

(defn ^:dev/after-load start []
  (-main))

(.addEventListener
 js/window
 "DOMContentLoaded"
 (fn []
   (start)))