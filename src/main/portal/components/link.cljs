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

(defn $external-link
  {:audition {:args [:string :string]}}
  [title href]
  [:a.link
   {:target "_blank"
    :href href}
   title])