(ns portal.pages.about
  (:require
   [portal.components.link :refer [$link]]))

(defn $about-page []
  [:div
   [:h3 "About Captain's Haven"]
   [:p "Captain's Haven is a all-in-one portal for everything related to Captain of Industry."]
   [:p "We have everything from mods to screenshots under one roof."]
   [:p "Currently we only support sharing and browsing Blueprints, but plan is to support much more than that."]
   [:p "We are in no way related to Captain of Industry or MaFi Games, this is a third-party website developed by a independent developer, not a official website made by MaFi Games"]
   [:p "Legal mumbo jumbo:"]
   [:p
    [$link "Privacy Policy" "/privacy-policy"]
    " - "
    [$link "Terms & Conditions" "/terms-and-conditions"]]
   [:p "Wanna see how the infrastructure is holding up?"]
   [:p
    [:a
     {:href "https://status.captains-haven.org"
      :target "_blank"}
     "Service status page"]]
   [:p "Trying to contact us? Write us!"]
   [:p
    [:a
     {:href "mailto:hello@captains-haven.org"
      :target "_blank"}
     "hello@captains-haven.org"]]
   [:p "Want to see how this website was made or contribute some cool feature/bugfix?"]
   [:p
    [:a
     {:href "https://codeberg.org/captains-haven/ui"
      :target "_blank"}
     "https://codeberg.org/captains-haven/ui"]]
   [:p "We keep a list of all the changes happening to the platform over here"]
   [$link "Changelog" "/changelog"]
   [:p "Some version numbers and stuff:"]
   [:div
    [:pre
     "Revision: "
     [:a
      {:target "_blank"
       :href (str "https://codeberg.org/captains-haven/ui/commit/" js/globals.revision)}
      js/globals.revision]
     "\n"
     "App version: " js/globals.version "\n"
     "Last update: " js/globals.last_update "\n"
     "Node.js version: " js/globals.node_version "\n"
     "npm version: " js/globals.npm_version "\n"
     "shadow-cljs version: " js/globals.shadow_cljs_version "\n"]]])