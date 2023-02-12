(ns audition.params)

(defmulti default-args
  (fn [k]
    k))

(defmethod default-args :edn [_]
  {:key "value" :arbitrary-data "yessir"})

(defmethod default-args :string [_]
  "Example string")

(defmethod default-args :default [_]
  (throw (js/Error. "Don't know that argument type")))