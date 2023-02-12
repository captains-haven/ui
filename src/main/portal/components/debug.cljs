(ns portal.components.debug
  (:require
   [clojure.pprint :refer [pprint]]))

(def hide-debug? false)

(defn $debug [data]
  (when-not hide-debug?
    [:div.debug
     [:pre (with-out-str (pprint data))]]))