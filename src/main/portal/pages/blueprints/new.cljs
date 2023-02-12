(ns portal.pages.blueprints.new
  (:require
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer-macros [<p!]]
   
   [reagent.core :as r]
   ["transliteration" :refer [transliterate slugify]]
   
   [portal.localstorage :refer [ls-get]]
   [portal.http :refer [create-resource]]
   [portal.router :refer [go-to]]
   [portal.form-utils :refer [handle-text-input-change]]
   
   [portal.components.text-input :refer [$text-input]]
   [portal.components.file-upload :refer [$file-upload]]
   [portal.components.error :refer [$error]]
   [portal.components.btn :refer [$btn]]
   [portal.components.loading :refer [$loading]]
   [portal.components.blueprints.items-list :refer [$blueprint-items-list]]
   ))

(defn slug-str [s]
  (slugify (transliterate s)))

(defn relative->absolute-url [relative]
  (str "https://uploads.captains-haven.org" relative))

(defn $blueprints-new-page []
  (let [is-loading (r/atom false)
        s (r/atom {:title nil
                   :description nil
                   :blueprint_data nil
                   :items_count 1
                   :items {}
                ;;    :author {:connect [{:id (:id (ls-get "user"))}]}
                   :author (:id (ls-get "user"))
                   :thumbnail nil
                   :res nil
                   :upload-res nil
                   :blueprint-error nil
                   :error nil})
        upload-res (r/atom nil)
        parse-blueprint-timeout (r/atom nil)
        parse-blueprint (fn [data]
                          (swap! s assoc
                                 :blueprint_data data
                                 :blueprint-error nil)
                          (when @parse-blueprint-timeout
                            (js/window.clearTimeout @parse-blueprint-timeout)
                            (reset! parse-blueprint-timeout nil))
                          (reset! parse-blueprint-timeout
                                  (js/window.setTimeout
                                   (fn []
                                     (reset! is-loading true)
                                     (go
                                       (let [res (<p! (js/window.fetch
                                                       "https://parser.captains-haven.org/blueprint"
                                                       #js{:method "POST"
                                                           :body data}))
                                             parsed (<p! (.json res))
                                             obj (js->clj parsed :keywordize-keys true)]
                                         (if (:error obj)
                                           (do
                                             (swap! s assoc
                                                    :blueprint-error (:error obj))
                                             (reset! is-loading false))
                                           (do
                                             (swap! s assoc
                                                    :title (:name obj)
                                                    :description (:desc obj)
                                                    :items_count (:items_count obj)
                                                    :items (:items obj))
                                             (reset! is-loading false))))))
                                   100)))
        submit-blueprint
        (fn []
          (if (nil? (:thumbnail @s))
            (swap! s assoc :error {:name "Missing thumbnail!" :message "You need to upload a thumbnail to go with your blueprint"})
            (do
              (reset! is-loading true)
              (go
                (println "creating")
                (let [to-submit (assoc @s :slug (-> @s :title slug-str))
                      res (<p! (create-resource "blueprints" to-submit))]
                  (if (:error res)
                    (do
                      (if (= (-> res :error :message) "This attribute must be unique")
                        (swap! s assoc :error {:name "Title not unique" :message "Title has to be unique, seems another blueprint already use the same title, please change it"})
                        (swap! s assoc :error (:error res)))
                      (reset! is-loading false))
                    (do
                      (println "created")
                      (swap! s assoc :res res)
                      (reset! is-loading false)
                      (go-to (str "/blueprints/" (-> res :data :attributes :slug))))))))))]
    (r/create-class
     {:reagent-render
      (fn []
        [:div.new-blueprint
         [:div "So you wanna add a new blueprint? Go right ahead."]
         [$text-input {:label "Blueprint Data"
                       :disabled @is-loading
                       :onChange #(parse-blueprint (-> % .-target .-value))
                       :value (:blueprint_data @s)
                       :placeholder "B2416:H4sIAAAAAAAACnVWzW8bxxV...."}]
         (when (:blueprint-error @s)
           [$error (:blueprint-error @s)])
         [$text-input {:label "Title"
                       :disabled @is-loading
                       :onChange (handle-text-input-change s :title)
                       :value (:title @s)
                       :placeholder "Blueprint Title"}]
         [$text-input {:label "Description"
                       :disabled @is-loading
                       :onChange (handle-text-input-change s :description)
                       :multiline true
                       :value (:description @s)
                       :placeholder "Description"}]
         [:div
          {:style {:opacity 0.7}}
          "(Markdown supported for the description)"]
         [$text-input {:label "Items Count"
                       :type "number"
                       :disabled @is-loading
                      ;; :onChange (handle-text-input-change s :items_count)
                       :value (:items_count @s)
                       :placeholder "1"}]
         [:div
          [:label
           [:div "Thumbnail"]
           [$file-upload
            {:disabled @is-loading
             :onStart (fn []
                        (reset! is-loading true)
                        (swap! s assoc :error nil))
             :onDone (fn [res]
                       (reset! upload-res res)
                       (swap! s assoc :thumbnail (-> res first :id))
                       (reset! is-loading false))}]]]
         (when-let [image-url (-> @upload-res first :url)]
           [:img
            {:width 300
             :src (relative->absolute-url image-url)}])
         (when (:error @s)
           [$error
            (-> @s :error :name)
            " - "
            (-> @s :error :message)])
         [$btn {:label "Submit"
                :disabled @is-loading
                :onClick submit-blueprint}]
         (when @is-loading
           [$loading])
         (when-not (empty? (:items @s))
           [$blueprint-items-list (:items @s)])])})))