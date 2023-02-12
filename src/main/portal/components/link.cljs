(ns portal.components.link
  (:require
   [portal.router :refer [ev-go-to]]))

(defn $link
  {:audition {:args [:string :string]}}
  [title href]
  [:a.link
   {:onClick (ev-go-to href)
    :href href}
   title])