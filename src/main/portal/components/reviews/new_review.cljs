(ns portal.components.reviews.new-review
  (:require
   [reagent.core :as r]
   [clojure.pprint :refer [pprint]]
   [audition.params]
   [portal.components.forms.select :refer [$select]]
   [portal.components.text-input :refer [$text-input]]
   [portal.components.link-btn :refer [$link-btn]]))

(defn -$new-review [{:keys [message on-submit author_id blueprint_id]
                     :or {on-submit (fn [])}}] 
  (let [s (r/atom {:message message
                   :rating 5
                   :author {:connect [{:id author_id}]}
                   :blueprint {:connect [{:id blueprint_id}]}})]
    (r/create-class
    {:reagent-render
     (fn []
       [:div.new-review
        [:div
         {:style {:margin-bottom 15}}
         [:strong "Leave a review for your fellow captains"]]
        [:div
         {:style {:margin-bottom 15}}
         [$select
          "Rating (1/5):"
          (:rating @s)
          (fn [new-val]
            (swap! s assoc :rating (js/parseInt new-val)))
          (map (fn [i] {:name (str (inc i)) :value (inc i)})
               (range 5))]]
        [:div
         {:style {:margin-bottom 15}}
         [$text-input
          {:label "Comment: (optional)"
           :value message
           :placeholder nil
           :multiline true
           :input-style {:width "100%"
                         :height "5em"}}]
         [:div
          {:style {:font-size 12
                   :margin-top 5}}
          "Please be direct and honest but kind and respectful with your feedback as well"]]
        [:button.link-btn
         {:onClick (fn []
                     (on-submit @s))}
         "Submit Review"]])})))

(defn $new-review
  {:audition {:args [:new-review-opts]}}
  [args] 
  [-$new-review args])

(defmethod audition.params/default-args :new-review-opts []
  {:message "This is my review"
   :author_id 1
   :blueprint_id 2
   :on-submit (fn [to-submit]
                (pprint to-submit))})