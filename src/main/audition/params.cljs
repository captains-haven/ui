(ns audition.params
  (:require
   [clojure.pprint :refer [pprint]]))

(defmulti default-args
  (fn [k]
    k))

(defmethod default-args :edn [_]
  {:key "value" :arbitrary-data "yessir"})

(defmethod default-args :string [_]
  "Example string")

(defmethod default-args :boolean [_]
  false)

(defmethod default-args :function [_]
  (fn [ev]
    (pprint ev)))

(defmethod default-args :number [_]
  5)

(defmethod default-args :default [args]
  (throw (js/Error. (str "Don't know the following argument type: " (with-out-str (pprint args))))))