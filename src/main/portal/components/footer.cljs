(ns portal.components.footer
  (:require
   [portal.components.link :refer [$link]]
   [portal.state :refer [app-state]]
   [clojure.string :as str]))

(defn c [t p]
  {:title t
   :path p})

(def footer-links
  [(c "About" "/about")
   (c "Contact" "/contact-us")
   (c "News" "/news")
   (c "Changelog" "/changelog")
   (c "Status" "https://status.captains-haven.org/")
   (c "Source Code" "https://codeberg.org/captains-haven/ui")])

(defn $footer []
  [:div.footer
   (doall
     (map (fn [{:keys [title path]}]
           [:div.footer-link
            {:class (when-not (= (:path @app-state) "/")
                      (when (or
                            (.startsWith path
                                         (:path @app-state))
                            (= path (:path @app-state)))
                       "active"))}
            (if (str/starts-with? path "/")
              [$link title path]
              [:a
               {:href path
                :class "external"
                :target "_blank"}
               title])])
         footer-links))])