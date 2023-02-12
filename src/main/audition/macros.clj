(ns audition.macros
  (:require [clojure.string :as str]))

(defmacro into-var [s]
  (let [sym (apply symbol (str/split s #"/"))]
    `(-> ~sym (var))))