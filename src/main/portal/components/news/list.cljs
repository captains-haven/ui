(ns portal.components.news.list
  (:require
   [audition.params]
   [portal.markdown :refer [markdown->hiccup]]
   [portal.time :refer [js-date->simple-date]]
   [portal.components.link :refer [$link]]))

(defn $news-list-item
  {:audition {:args [:news-item]}}
  [{:keys [title slug text publishedAt]}]
  [:div.news-list-item
   [$link
    [:h3
     title
     ]
    (str "/news/" slug)]
   [:div
    (second (markdown->hiccup (or text "")))]
   [:div
    (js-date->simple-date (js/Date. publishedAt)) " - "
    [$link "Read the full article" (str "/news/" slug)]]])

(defn $news-list
  {:audition {:args [:news-items]}}
  [items]
  [:div.news-list
   (interpose
    [:hr]
    (map (fn [item]
           ^{:key (:slug item)}
           [$news-list-item item])
         items))])

(defmethod audition.params/default-args :news-item []
  {:title "Update - Blueprints now has \"views\"!",
   :slug "update-blueprint-views",
   :text
   "Good news everybody!\n\nFirst, we have a page where we can publish news related to Captains Haven and more.\n\nSecondly, Blueprints now has a new attribute called \"views\" which counts each page visit of a single blueprint, so you'll be able to sort by \"views\" to find the most popular ones, and also see how popular your own blueprints are.\n\n#### Yesterdays downtime\n\nRegarding yesterdays downtime, you can read more about the cause and fix here in case you're curious: https://status.captains-haven.org/incident/175179\n\nHere is a short summary:\n\n> Service went down, no data was lost, but service 100% unavailable. After 7 hours, issue was resolved and everything back online now. Sorry for the inconvenience, we're going to do our best to prevent something like this happening in the future again.",
   :createdAt "2023-02-15T17:28:09.337Z",
   :updatedAt "2023-02-15T17:29:16.588Z",
   :publishedAt "2023-02-15T17:28:10.559Z"})

(defmethod audition.params/default-args :news-items []
  [{:title "Update - Blueprints now has \"views\"!",
    :slug "update-blueprint-views",
    :text
    "Good news everybody!\n\nFirst, we have a page where we can publish news related to Captains Haven and more.\n\nSecondly, Blueprints now has a new attribute called \"views\" which counts each page visit of a single blueprint, so you'll be able to sort by \"views\" to find the most popular ones, and also see how popular your own blueprints are.\n\n#### Yesterdays downtime\n\nRegarding yesterdays downtime, you can read more about the cause and fix here in case you're curious: https://status.captains-haven.org/incident/175179\n\nHere is a short summary:\n\n> Service went down, no data was lost, but service 100% unavailable. After 7 hours, issue was resolved and everything back online now. Sorry for the inconvenience, we're going to do our best to prevent something like this happening in the future again.",
    :createdAt "2023-02-15T17:28:09.337Z",
    :updatedAt "2023-02-15T17:29:16.588Z",
    :publishedAt "2023-02-15T17:28:10.559Z"}
   {:title "Update - Another but fake news item",
    :slug "another-slug",
    :text
    "Good news everybody! This is the first line of the article with some longer stuff\n\nFirst, we have a page where we can publish news related to Captains Haven and more.\n\nSecondly, Blueprints now has a new attribute called \"views\" which counts each page visit of a single blueprint, so you'll be able to sort by \"views\" to find the most popular ones, and also see how popular your own blueprints are.\n\n#### Yesterdays downtime\n\nRegarding yesterdays downtime, you can read more about the cause and fix here in case you're curious: https://status.captains-haven.org/incident/175179\n\nHere is a short summary:\n\n> Service went down, no data was lost, but service 100% unavailable. After 7 hours, issue was resolved and everything back online now. Sorry for the inconvenience, we're going to do our best to prevent something like this happening in the future again.",
    :createdAt "2023-02-14T17:28:09.337Z",
    :updatedAt "2023-02-14T17:29:16.588Z",
    :publishedAt "2023-02-14T17:28:10.559Z"}])