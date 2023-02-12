(ns portal.components.btn)

(defn $btn [{:keys [label onClick disabled]}]
  [:div
   [:button
    {:disabled disabled
     :onClick (if disabled
                (fn [])
                onClick)}
    label]])