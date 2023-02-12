(ns portal.localstorage
  (:require
   [clojure.edn :refer [read-string]]))

(defn ls-set [k v]
  (.setItem js/window.localStorage
            k
            (prn-str v)))

(defn ls-get [k]
  (read-string
   (.getItem js/window.localStorage
             k)))