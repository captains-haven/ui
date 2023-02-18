(ns portal.components.blueprint
  (:require
   [portal.components.link :refer [$link]]
   [portal.time :refer [simple-datetime]]
   [portal.state :refer [app-state]]
   [portal.components.link-btn :refer [$link-btn]]
   [portal.components.zoomable-image :refer [$zoomable-image]]
   
   [portal.markdown :refer [markdown->hiccup]]
   
   [audition.params]))

;; Adjusted for the new structure we get from findBySlug API
(defn $blueprint-new
  {:audition {:args [:blueprint :boolean]}}
  [data full-view?]
  (let [full-href (str "/blueprints/" (:slug data))]
    [:div.blueprint
     {:class (when full-view? "full-blueprint")}
     [:div.blueprint-title
      {:class (when-not full-view? "ellipsis-overflow")
       :title (:title data)}
      [$link
       (:title data)
       full-href]]
     [:div
      "By "
      [$link
       (-> data :author :username)
       (str "/blueprints/sorted-by/views/desc/search/"
            (-> data :author :username))]]
     (when full-view?
       [:div
        "Description:"
        (when (:description data)
          (markdown->hiccup (:description data)))])
     (if full-view?
       [$zoomable-image
        (str "https://uploads.captains-haven.org" (-> data :thumbnail :formats :thumbnail :url))
        (str "https://uploads.captains-haven.org" (-> data :thumbnail :url))
        {:alt (str "Thumbnail for the Blueprint called " (:title data))
         :width 300}]
      [$link
       [:div.blueprint-image
        [:img
         {:alt (str "Thumbnail for the Blueprint called " (:title data))
          :width 300
          :src (str "https://uploads.captains-haven.org" (-> data :thumbnail :formats :thumbnail :url))}]]
       full-href])
     (when full-view?
       [:div
        "Blueprint Data:"
        [:pre (:blueprint_data data)]])
     [:div
      [:button.link-btn
       {:onClick (fn []
                   (.writeText js/navigator.clipboard
                               (:blueprint_data data))
                   (.alert js/window "Blueprint data has been copied to your clipboard!"))}
       "Copy Blueprint to Clipboard"]]
     (if full-view?
       [:div
        [:div
         "Published at: " (:publishedAt data)]
        [:div
         "Last Update: " (:updatedAt data)]]
       [:div
        {:style {:margin-top 10}}
        (simple-datetime (js/Date. (:updatedAt data)))])
     [:div
      "Views: " (or (:views data) 1)]
     (when (and full-view?
                (= (-> data :author :username)
                   (-> @app-state :user :username)))
       [:div
        {:style {:margin-top 15
                 :padding-left 5}}
        [$link-btn
         "Edit"
         (str "/blueprints/"
              (:slug data)
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
          (markdown->hiccup (:description attrs)))])
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
     [:div
      "Views: " (or (:views attrs) 1)]
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

(defmethod audition.params/default-args :blueprint []
  {:description
   "Based on the blueprint from rogersluke8383.\n\nCombined 2 blueprints and added an Evaporation Pond (Heated) to the blueprint.",
   :publishedAt "2023-02-12T13:23:22.563Z",
   :slug "basic-coal-burner-power-plant-and-salt-by-peterpeijs",
   :updatedAt "2023-02-12T23:15:08.877Z",
   :createdBy nil,
   :locale "en",
   :compatible_game_version nil,
   :localizations [],
   :createdAt "2023-02-12T13:23:22.566Z",
   :screenshots [],
   :items_count 85,
   :title "Basic Coal Burner Power plant and Salt by PeterPeijs",
   :author
   {:username "PeterPeijs"},
   :thumbnail
   {:formats
    {:thumbnail
     {:path nil,
      :ext ".jpg",
      :hash "thumbnail_20230212141103_1_f21c385854",
      :name "thumbnail_20230212141103_1.jpg",
      :width 245,
      :mime "image/jpeg",
      :size 8.93,
      :url "/uploads/thumbnail_20230212141103_1_f21c385854.jpg",
      :height 138},
     :small
     {:path nil,
      :ext ".jpg",
      :hash "small_20230212141103_1_f21c385854",
      :name "small_20230212141103_1.jpg",
      :width 500,
      :mime "image/jpeg",
      :size 34.88,
      :url "/uploads/small_20230212141103_1_f21c385854.jpg",
      :height 281},
     :medium
     {:path nil,
      :ext ".jpg",
      :hash "medium_20230212141103_1_f21c385854",
      :name "medium_20230212141103_1.jpg",
      :width 750,
      :mime "image/jpeg",
      :size 78.18,
      :url "/uploads/medium_20230212141103_1_f21c385854.jpg",
      :height 422},
     :large
     {:path nil,
      :ext ".jpg",
      :hash "large_20230212141103_1_f21c385854",
      :name "large_20230212141103_1.jpg",
      :width 1000,
      :mime "image/jpeg",
      :size 137.97,
      :url "/uploads/large_20230212141103_1_f21c385854.jpg",
      :height 563}},
    :provider_metadata nil,
    :folderPath "/1",
    :caption nil,
    :ext ".jpg",
    :updatedAt "2023-02-12T13:21:33.620Z",
    :hash "20230212141103_1_f21c385854",
    :name "20230212141103_1.jpg",
    :width 1920,
    :createdAt "2023-02-12T13:21:33.620Z",
    :previewUrl nil,
    :mime "image/jpeg",
    :size 524.91,
    :alternativeText nil,
    :id 128,
    :url "/uploads/20230212141103_1_f21c385854.jpg",
    :provider "local",
    :height 1080},
   :updatedBy nil,
   :id 68,
   :blueprint_data
   "B2828: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",
   :views 42})