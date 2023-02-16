(ns portal.state
  (:require
   [portal.localstorage :refer [ls-get]]
   [reagent.core :as r]))

(def app-state
  (r/atom
   {:path "/"
    :user-token (ls-get "auth-token")
    :theme (or (ls-get "theme") "dark")
    :user (ls-get "user")
    :page-component [:div "404"]}))