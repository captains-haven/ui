(ns portal.components.error)

(defn $error [& error-msgs]
  [:div.error-message
   error-msgs])