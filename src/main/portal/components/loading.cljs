(ns portal.components.loading
  (:require
   [reagent.core :as r]))

(defn $loading []
  (let [iterations (r/atom 0)
        interval (r/atom nil)]
    (r/create-class
     {:component-will-unmount
      (fn []
        (js/window.clearInterval @interval)
        (reset! interval nil))
      :component-did-mount
      (fn []
        (reset! interval
                (js/window.setInterval
                 (fn []
                   (if (< @iterations 3)
                     (swap! iterations inc)
                     (reset! iterations 0)))
                 100)))
      :reagent-render
      (fn []
        [:div
         "Loading"
         (reduce (fn [acc _curr]
                   (str acc "."))
                 ""
                 (range @iterations))])})))