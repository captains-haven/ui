(ns portal.components.link-btn
  (:require
   [portal.router :refer [ev-go-to]]
   ))

(defn $link-btn [title href]
  [:a.link-btn
   {:onClick (ev-go-to href)
    :href href}
   title])