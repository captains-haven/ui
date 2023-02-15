(ns portal.components.zoomable-image
  (:require
   [reagent.core :as r]))

;; (defn $zoomable-image []
;;   [:div "hello"])

(defn $-zoomable-image
  {:audition {:args [:thumbnail-url :full-size-url :edn]}}
  [thumbnail full-size thumbnail-extras]
  (let [zoomed? (r/atom false)]
   (r/create-class
    {:reagent-render
     (fn [thumbnail full-size thumbnail-extras]
        (if @zoomed?
          [:div.fullscreen-background
           {:onClick #(reset! zoomed? false)}
           [:div.fullscreen-modal
            [:img
             {:onClick #(reset! zoomed? false)
              :src full-size}]]]
          [:img
           (merge {:src thumbnail
                   :onClick (fn []
                              (reset! zoomed? true))}
                  (or thumbnail-extras {}))]))})))

(defn $zoomable-image
  {:audition {:args [:thumbnail-url :full-size-url :edn]}}
  [thumbnail full-size thumbnail-extras]
  [:div
   [$-zoomable-image thumbnail full-size thumbnail-extras]])

(defmethod audition.params/default-args :thumbnail-url []
  "https://uploads.captains-haven.org/uploads/thumbnail_Atommuehllager_91ea077abb.png")

(defmethod audition.params/default-args :full-size-url []
  "https://uploads.captains-haven.org/uploads/Atommuehllager_91ea077abb.png")