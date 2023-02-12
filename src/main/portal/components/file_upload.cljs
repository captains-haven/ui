(ns portal.components.file-upload
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   [portal.http :refer [post-formdata]]))


(defn upload-file [input-field]
  (let [form-data (js/FormData.)
        file (-> input-field .-files first)] 
    (.append form-data "files" file) 
    (post-formdata "upload" form-data)))

(defn $file-upload [{:keys [disabled onStart onDone]
                     :or {disabled false
                          onStart (fn [])
                          onDone (fn [])}}]
  [:input
   {:type "file"
    :disabled disabled
    :name "file"
    :onChange (fn [ev]
                (onStart)
                (go
                  (let [res (<p! (upload-file (.-target ev)))]
                    (onDone res))))}])