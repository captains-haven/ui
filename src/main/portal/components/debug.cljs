(ns portal.components.debug
  (:require
   [clojure.pprint :refer [pprint]]))

(def hide-debug? true)

(defn $debug
  {:audition {:args [:edn]}}
  [data]
  (when-not hide-debug?
    [:div.debug
     [:pre (with-out-str (pprint data))]]))