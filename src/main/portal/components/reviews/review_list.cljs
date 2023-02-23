(ns portal.components.reviews.review-list
  (:require
   [audition.params]
   [portal.components.reviews.review :refer [$review]]))

(defn $review-list
  {:audition {:args [:reviews]}}
  [reviews]
  [:div.review-list
   [:div
    {:style {:font-size 22
             :margin-left 5
             :margin-bottom 10}}
    "Reviews:"]
   (if (= (count reviews) 0)
     [:div
      {:style {:margin "10px 5px"}}
      "No reviews found, be a kind soul and leave the first one :)"]
     (interpose
      [:hr
       {:style {:margin "10px 0px"}}]
      (doall
       (map (fn [review]
              [$review review])
            reviews))))])

(defmethod audition.params/default-args :reviews []
  [{:createdAt "2023-02-14T13:23:22.566Z",
    :rating 1
    :author {:username "tommy"}}
   {:message "This is my review. I think it worked well in general. But sometimes, just **sometimes** I
             wish it worked differently but I'm not sure what we can do about that."
    :createdAt "2023-02-13T13:23:22.566Z",
    :rating 3
    :author {:username "verylongannoyingusername"}}
   {:createdAt "2023-02-12T13:23:22.566Z",
    :rating 5
    :author {:username "someoneelse"}}
   {:message "Cool"
    :createdAt "2023-02-11T13:23:22.566Z",
    :rating 5
    :author {:username "admin"}}])