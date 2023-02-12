(ns portal.pages.home
  (:require
   [portal.state :refer [app-state]]
   [portal.components.link :refer [$link]]))

(defn $home-page []
  [:div
   (if-let [username (-> @app-state :user :username)]
     [:h3 "Ahoy " username ", welcome back to Captain's Haven"]
     [:h3 "Ahoy captain, welcome to Captain's Haven"])
   [:div "Here we have everything for budding captains playing Captain of Industry:"]
   [:ul
    [:li "Mods to change your game experience"]
    [:li "Blueprints to learn designs from others and share with the community"]
    [:li "Savegames to learn even bigger designs"]
    [:li "Timelapses and screenshots because sometimes we just need to stare at some photos and videos to feel good about ourselves"]]
   [:div
    "Want us to support some other thing from Captain of Industry? Reach out to us and maybe we can add support for it!"]
   [$link
    "hello@captains-haven.org"
    "mailto:hello@captains-haven.org"]])