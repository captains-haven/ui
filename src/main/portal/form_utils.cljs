(ns portal.form-utils)

(defn handle-text-input-change [s k]
  (fn [ev]
    (let [v (-> ev .-target .-value)]
      (swap! s assoc k v))))

(defn handle-text-input-change-nested [s ks]
  (fn [ev]
    (let [v (-> ev .-target .-value)]
      (swap! s assoc-in ks v))))