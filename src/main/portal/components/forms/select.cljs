(ns portal.components.forms.select
  (:require
   [audition.params]))

(defn $select
  {:audition {:args [:example-label :selected-option :function :options]}}
  [label value onChange items]
  [:label
   [:span
    {:style {:margin-right 5}}
    label]
   [:select
    {:value value
     :onChange (fn [ev]
                 (let [new-val (-> ev .-target .-value)]
                   (onChange new-val)))}
    (map
     (fn [{:keys [name value]}]
       ^{:key value}
       [:option {:value value} name])
     items)]])

(defmethod audition.params/default-args :example-label []
  "Select Label")

(defmethod audition.params/default-args :selected-option []
  "Option 2 Value")

(defmethod audition.params/default-args :options []
  [{:name "Option 1"
    :value "Option 1 Value"}
   {:name "Option 2"
    :value "Option 2 Value"}])