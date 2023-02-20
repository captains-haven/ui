(ns portal.components.footer
  (:require
   [portal.components.link :refer [$link]]
   [portal.state :refer [app-state]]
   [portal.localstorage :refer [ls-get ls-set]]
   [clojure.string :as str]))

(defn c [t p]
  {:title t
   :path p})

(def footer-links
  [(c "About" "/about")
   (c "Contact" "/contact-us")
   (c "News" "/news")
   (c "Changelog" "/changelog")
   (c "Chat" "/chat")
   (c "Status" "https://status.captains-haven.org/")
   (c "Source Code" "https://codeberg.org/captains-haven/ui")])

(defn set-light-mode! []
  (swap! app-state assoc :theme "light")
  (ls-set "theme" "light"))

(defn set-dark-mode! []
  (swap! app-state assoc :theme "dark")
  (ls-set "theme" "dark"))

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
         footer-links))
   (let [theme (:theme @app-state)]
    [:div.footer-link
     [:a
      {:onClick
       (fn [ev]
         (.preventDefault ev)
         (if (= theme "dark")
           (set-light-mode!)
           (set-dark-mode!)))
       :href "#"}
      (if (= theme "dark")
        "Light Mode"
        "Dark Mode")]])])