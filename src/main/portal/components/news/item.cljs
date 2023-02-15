(ns portal.components.news.item
  (:require
   [audition.params]
   [portal.markdown :refer [markdown->hiccup]]
   [portal.time :refer [simple-datetime]]))

(defn $news-item
  {:audition {:args [:news-item]}}
  [{:keys [title slug text publishedAt]}]
  [:div.news-item
   [:h1 title]
   (println text)
   [:div
    (markdown->hiccup (or text ""))]
   [:hr]
   [:div
    "Published at " (simple-datetime (js/Date. publishedAt))]
   [:br]
   [:div
    "URL: "
    (let [url (str "https://captains-haven.org/news/" slug)]
     [:a
      {:href url}
      url])]
   [:br]
   [:div
    "Contact us: "
    [:a
     {:href "mailto:hello@captains-haven.org"}
     "hello@captains-haven.org"]]])

(defmethod audition.params/default-args :news-item []
  {:title "Update - Blueprints now has \"views\"!",
   :slug "update-blueprint-views",
   :text
   "Good news everybody!\n\nFirst, we have a page where we can publish news related to Captains Haven and more.\n\nSecondly, Blueprints now has a new attribute called \"views\" which counts each page visit of a single blueprint, so you'll be able to sort by \"views\" to find the most popular ones, and also see how popular your own blueprints are.\n\n#### Yesterdays downtime\n\nRegarding yesterdays downtime, you can read more about the cause and fix here in case you're curious: https://status.captains-haven.org/incident/175179\n\nHere is a short summary:\n\n> Service went down, no data was lost, but service 100% unavailable. After 7 hours, issue was resolved and everything back online now. Sorry for the inconvenience, we're going to do our best to prevent something like this happening in the future again.",
   :createdAt "2023-02-15T17:28:09.337Z",
   :updatedAt "2023-02-15T17:29:16.588Z",
   :publishedAt "2023-02-15T17:28:10.559Z"})