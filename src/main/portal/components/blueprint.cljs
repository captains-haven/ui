(ns portal.components.blueprint
  (:require
   [portal.components.link :refer [$link]]
   [portal.time :refer [simple-datetime]]
   [portal.state :refer [app-state]]
   [portal.components.link-btn :refer [$link-btn]]
   
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as md.transform]))

(defn $blueprint-new [data full-view?]
  (let [attrs (:attributes data)
        full-href (str "/blueprints/" (:slug attrs))]
    [:div.blueprint
     {:class (when full-view? "full-blueprint")}
     [:div.blueprint-title
      {:class (when-not full-view? "ellipsis-overflow")
       :title (:title attrs)}
      [$link
       (:title attrs)
       full-href]]
     [:div
      "By " (-> attrs :author :data :attributes :username)]
     (when full-view?
       [:div
        "Description:"
        (when (:description attrs)
          (md.transform/->hiccup (md/parse (:description attrs))))])
     [$link
      [:div.blueprint-image
       [:img
        {:alt (str "Thumbnail for the Blueprint called " (:title attrs))
         :width 300
         :src (str "https://uploads.captains-haven.org" (-> attrs :thumbnail :data :attributes :url))}]]
      full-href]
     (when full-view?
       [:div
        "Blueprint Data:"
        [:pre (:blueprint_data attrs)]])
     [:div
      [:button.link-btn
       {:onClick (fn []
                   (.writeText js/navigator.clipboard
                               (:blueprint_data attrs))
                   (.alert js/window "Blueprint data has been copied to your clipboard!"))}
       "Copy Blueprint to Clipboard"]]
     (if full-view?
       [:div
        [:div
         "Published at: " (:publishedAt attrs)]
        [:div
         "Last Update: " (:updatedAt attrs)]]
       [:div
        {:style {:margin-top 10}}
        (simple-datetime (js/Date. (:updatedAt attrs)))])
     (when (and full-view?
                (= (-> attrs :author :data :id)
                   (-> @app-state :user :id)))
       [:div
        {:style {:margin-top 15
                 :padding-left 5}}
        [$link-btn
         "Edit"
         (str "/blueprints/"
              (:slug attrs)
              "/edit")]])]))

(defn $blueprint [data full-view?]
  (let [attrs (:attributes data)
        full-href (str "/blueprints/" (:slug attrs))]
    [:div.blueprint
     {:class (when full-view? "full-blueprint")}
     [:div.blueprint-title
      {:class (when-not full-view? "ellipsis-overflow")
       :title (:title attrs)}
      [$link
       (:title attrs)
       full-href]]
     [:div
      "By " (-> attrs :author :data :attributes :username)]
     (when full-view?
       [:div
        "Description:"
        (when (:description attrs)
          (md.transform/->hiccup (md/parse (:description attrs))))])
     [$link
      [:div.blueprint-image
       [:img
        {:alt (str "Thumbnail for the Blueprint called " (:title attrs))
         :width 300
         :src (str "https://uploads.captains-haven.org" (-> attrs :thumbnail :data :attributes :url))}]]
      full-href]
     (when full-view?
       [:div
        "Blueprint Data:"
        [:pre (:blueprint_data attrs)]])
     [:div
      [:button.link-btn
       {:onClick (fn []
                   (.writeText js/navigator.clipboard
                               (:blueprint_data attrs))
                   (.alert js/window "Blueprint data has been copied to your clipboard!"))}
       "Copy Blueprint to Clipboard"]]
     (if full-view?
       [:div
        [:div
         "Published at: " (:publishedAt attrs)]
        [:div
         "Last Update: " (:updatedAt attrs)]]
       [:div
        {:style {:margin-top 10}}
        (simple-datetime (js/Date. (:updatedAt attrs)))])
     (when (and full-view?
                (= (-> attrs :author :data :id)
                   (-> @app-state :user :id)))
       [:div
        {:style {:margin-top 15
                 :padding-left 5}}
        [$link-btn
         "Edit"
         (str "/blueprints/"
              (:slug attrs)
              "/edit")]])]))