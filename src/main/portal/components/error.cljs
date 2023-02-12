(ns portal.components.error)

(defn $error
  {:audition {:args [:string]}}
  [& error-msgs]
  [:div.error-message
   error-msgs])