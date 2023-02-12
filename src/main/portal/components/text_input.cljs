(ns portal.components.text-input)

(defn $text-input [{:keys [label type placeholder value onChange disabled multiline]
                    :or {label "Input Label"
                         type "text"
                         value nil
                         disabled false
                         multiline false
                         onChange (fn [])
                         placeholder "Input Placeholder"}}]
  [:div
   {:class (when disabled "disabled")}
   [:label
    [:div label]
    (if multiline
      [:textarea
       {:onChange onChange}
       (or value placeholder)]
      [:input
       {:type type
        :onChange onChange
        :disabled disabled
        :value value
        :placeholder placeholder}])]])