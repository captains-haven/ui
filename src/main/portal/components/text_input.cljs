(ns portal.components.text-input)

(defn $text-input [{:keys [label type placeholder value onChange
                           onKeyUp disabled multiline label-el
                           container-el input-style]
                    :or {label "Input Label"
                         type "text"
                         input-style {}
                         label-el :div
                         container-el :div
                         value nil
                         disabled false
                         multiline false
                         onChange (fn [])
                         onKeyUp (fn [])
                         placeholder "Input Placeholder"}}]
  [container-el
   {:class (when disabled "disabled")}
   [:label
    [label-el label]
    (if multiline
      [:textarea
       {:style input-style
        :onChange onChange}
       (or value placeholder)]
      [:input
       {:style input-style
        :type type
        :onChange onChange
        :disabled disabled
        :onKeyUp onKeyUp
        :value value
        :placeholder placeholder}])]])