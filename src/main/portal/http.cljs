(ns portal.http
  (:require
   [portal.state :refer [app-state]]
   [portal.constants :refer [default-token
                             api-root
                             dev-default-token]]
   [clojure.pprint :refer [pprint]]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer [<p!]]
   ["qs" :as qs]))

(defn user-or-default-token []
  (or (:user-token @app-state)
      default-token
      ;; dev-default-token
      ))

(defn fetch-resource [resource]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource "?populate=*&sort=id%3Adesc&pagination[pageSize]=100")
     #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn fetch-resource-with-sort [resource sort-by direction]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource "?populate=*&sort="sort-by"%3A"direction"&pagination[pageSize]=100")
     #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn querystring [edn-map]
  (.stringify
   qs
   (clj->js edn-map)
   #js{:encodeValuesOnly true}))

(defn fetch-resource-with-sort-and-search [resource sort-by direction search-term]
  (let [query {"populate" "*"
               "sort" (str sort-by ":" direction)
               "filters" {"$or" [{"title" {"$contains" search-term}}
                                 {"description" {"$contains" search-term}}
                                 {"author" {"username" {"$contains" search-term}}}]}
               "pagination[pageSize]" "100"}
        query-str (querystring query)]
   (.then
    (.then
     (js/window.fetch
      (str api-root resource "?" query-str)
      #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
     #(.json %))
    #(js->clj % :keywordize-keys true))))

(defn fetch-resource-with-qs [resource edn-map]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource "?" (querystring edn-map))
     #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn fetch-resource-by-slug [resource slug]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource "/find-by-slug/" slug "?populate=*")
     #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn fetch-resource-by-old-slug [resource slug]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource "?filters[slug][$eq]=" slug "&populate=*")
     #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn create-resource [resource resource-data]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource)
     #js{"method" "POST"
         "headers" #js{"Authorization" (str "bearer " (user-or-default-token))
                       "Content-Type" "application/json"}
         "body" (js/JSON.stringify (clj->js {:data resource-data}))})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn update-resource [resource resource-data resource-id]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource "/" resource-id)
     #js{"method" "PUT"
         "headers" #js{"Authorization" (str "bearer " (user-or-default-token))
                       "Content-Type" "application/json"}
         "body" (js/JSON.stringify (clj->js {:data resource-data}))})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn post-formdata [path form-data]
  (.then
   (.then
    (js/window.fetch
     (str "https://api.captains-haven.org/" path)
     #js{"method" "post"
         "headers" #js{"Authorization" (str "bearer " (user-or-default-token))}
         "body" form-data})
    #(.json %))
   #(js->clj % :keywordize-keys true)))