(ns portal.components.reviews.review
  (:require
   [audition.params]
   [portal.time :refer [js-date->simple-date]]
   [portal.markdown :refer [markdown->hiccup]]))

(defn $review
  {:audition {:args [:review]}}
  [{:keys [rating message author createdAt]}]
  [:div.review
   [:div.review-meta
    [:div.review-meta-field
     [:strong "Score: "]
     rating
     "/5"]
    [:div.review-meta-field
     [:strong "Date: "]
     (js-date->simple-date (js/Date. createdAt))]
    [:div.review-meta-field
     {:title (-> author :username)}
     [:strong "By: "]
     (-> author :username)]]
   (when message
    [:div.review-message
     {:style {:margin "10px 0px"
              :font-family "monospace"}}
    ;;  [:strong "Comment: "]
     (markdown->hiccup message)])])

(defmethod audition.params/default-args :review []
  {:message "This is my review. I think it worked well in general. But sometimes, just **sometimes** I
             wish it worked differently but I'm not sure what we can do about that."
   :createdAt "2023-02-12T13:23:22.566Z",
   :rating 3
   :author {:username "verylongannoyingusername"}})