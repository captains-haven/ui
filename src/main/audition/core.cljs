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

   [audition.params :refer [default-args]]

   [portal.components.link]
   [portal.components.error]
   [portal.components.debug]
   [portal.components.changelog]
   [portal.components.blueprint]
   [portal.components.blueprints.list]
   [portal.components.forms.select]
   [portal.components.news.item]
   [portal.components.news.list])
  ;; (:require-macros
  ;;  [audition.macros :refer [into-var]]
  ;;  )
  )

(def $debug portal.components.debug/$debug)

(def components-map
  {"portal.components.link/$link" portal.components.link/$link
   "portal.components.error/$error" portal.components.error/$error
   "portal.components.debug/$debug" portal.components.debug/$debug
   "portal.components.changelog/$changelog-item" portal.components.changelog/$changelog-item
   "portal.components.changelog/$changelog-list" portal.components.changelog/$changelog-list
   "portal.components.blueprint/$blueprint-new" portal.components.blueprint/$blueprint-new
   "portal.components.blueprints.list/$blueprints-list-new" portal.components.blueprints.list/$blueprints-list-new
   "portal.components.forms.select/$select" portal.components.forms.select/$select
   "portal.components.news.item/$news-item" portal.components.news.item/$news-item
   "portal.components.news.list/$news-list-item" portal.components.news.list/$news-list-item
   "portal.components.news.list/$news-list" portal.components.news.list/$news-list})

(defn only-components [m]
  (into
   {}
   (filter (fn [[n v]]
            ;;  (println n)
             (str/starts-with? (name n) "$"))
           m)))

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
     (ns-interns 'portal.components.debug)
     (ns-interns 'portal.components.changelog)
     (ns-interns 'portal.components.blueprint)
     (ns-interns 'portal.components.blueprints.list)
     (ns-interns 'portal.components.forms.select)
     (ns-interns 'portal.components.news.item)
     (ns-interns 'portal.components.news.list)))))

(defn sort-components [components]
  (into
   (sorted-map)
   (reverse
    (sort-by
     first
     components))))

(comment
  (keys (list-components))
  (keys (sort-components (list-components))))

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
    (sort-components (list-components)))])

(defn $string-editor [state index]
  [:input
   {:type "text"
    :onChange (fn [ev]
                (let [new-val (-> ev .-target .-value)]
                  (swap! state assoc index new-val)))
    :value (get @state index)}])

(defn $edn-editor [state index]
  [:textarea
   {:style {:width 500}
    :onChange (fn [ev]
                (let [new-val (-> ev .-target .-value)]
                  (swap! state assoc index (read-string new-val))))}
   (with-out-str (pprint (get @state index)))])

(defn $boolean-editor [state index]
  [:input
   {:type "checkbox"
    :onChange (fn [ev]
                (let [new-val (-> ev .-target .-checked)]
                  (swap! state assoc index new-val)))
    :value (get @state index)}])

(def args-editors
  {:string $string-editor
   :edn $edn-editor
   :boolean $boolean-editor})

(defn create-args [args] 
  (mapv
   (fn [k]
     (default-args k))
   args))

(defn $args [args state]
  [:div
   (doall
     (map-indexed
     (fn [index arg]
       (let [arg-type arg]
         [:div
          [:label
           [:div arg-type]
           [(get args-editors arg-type $edn-editor) state index]]]))
     args))])

(defn $component-page [{:keys [key]}]
  (let [decoded-key (.decodeURIComponent js/window key)
        component (get components-map decoded-key)
        _ (.log js/console component)
        _ (pprint component)
        _ (pprint (component "hello" "world"))
        audition-props (:audition (get (list-components) decoded-key))
        args (:args audition-props)
        _ (pprint args)
        component-args (create-args args)
        args-state (r/atom component-args)
        _ (println "args state")
        _ (pprint args-state)]
    (fn [{:keys [key]}]
      [:div
       [:div "Args:"]
       [:pre (with-out-str (pprint args))]
       [$args args args-state]
       [:div "Rendered:"]
       [:div
        {:style {:padding 10
                 :border "1px solid white"}}
        (apply component @args-state)]])))

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
    {:href "/home"
     :onClick
     (fn [ev]
       (.preventDefault ev)
       (go-to "/home"))}
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