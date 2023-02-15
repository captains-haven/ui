(ns portal.markdown
  (:require
   [nextjournal.markdown :as md]
   [nextjournal.markdown.transform :as md.transform]))

(defn markdown->hiccup [markdown-str]
  (md.transform/->hiccup (md/parse markdown-str)))

(comment
  (markdown->hiccup "Normal text")
  (markdown->hiccup "## H2 header"))