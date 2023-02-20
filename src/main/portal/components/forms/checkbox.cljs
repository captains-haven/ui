(ns portal.components.forms.checkbox)

(defn $checkbox
  [label value onChange]
  [:label
   [:span
    {:style {:margin-right 5}}
    label]
   [:input
    {:checked value
     :type "checkbox"
     :onChange (fn [ev]
                 (let [new-val (-> ev .-target .-checked boolean)]
                   (onChange new-val)))}]])