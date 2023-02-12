(ns portal.components.link)

(defn go-to [url]
  (println "Going to"))

(defn $link
  {:audition {:args [:string :string]}}
  [title href]
  [:a.link
   {:onClick (fn [ev]
               (.preventDefault ev)
              ;;  (go-to href)
               )
    :href href}
   title])