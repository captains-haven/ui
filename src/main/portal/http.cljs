(ns portal.http
  (:require
   [portal.state :refer [app-state]]))

(def default-token "45e3a5205f8de0683a79335091dc7f7e1897eaa54caa1c12518607deffa53c8079c5a981c7b4b88a237ddb5912f7da3d2683729f1c3109318178ac6755fd34cac99686068595012713b336fb220cf1029af0fa6bdce87f76a4e76da8e6b77120b636278be67478c1a855354904938dd5ce9aa484cb5ad68b30fdf79214d14546")

(def api-root
  ;; "https://captains-haven.org/"
  "http://localhost:1337/api/")

(defn user-or-default-token []
  (or (:user-token @app-state)
      default-token))

(defn fetch-resource [resource]
  (.then
   (.then
    (js/window.fetch
     (str api-root resource "?populate=*&sort=id%3Adesc&pagination[pageSize]=100")
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