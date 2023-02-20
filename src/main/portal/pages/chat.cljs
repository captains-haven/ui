(ns portal.pages.chat
  (:require
   [portal.components.link :refer [$external-link]]))

(def chat-link
  "https://discord.gg/captain-of-industry-803508556325584926")

(defn $chat-page []
  [:div
   {:style {:max-width 600
            :margin "0px auto"}}
   [:h1 "Chat - Discord"]
   [:div
    [:p "You can discuss with your fellow captains on the official Captain of Industry Discord"]]
   [:div
    [:p
     [$external-link
      chat-link
      chat-link]]]
   [:div
    [:p "Once inside the official Discord server, you can find our unofficial channel in #captains-haven"]]
   [:div
    [:p "We're all waiting for you :)"]]
   [:div
    [:small
     {:style {:font-size 12}}
     [:i "Please note that Captain's Haven is not affiliated with Captain of Industry or MaFi Games. We're an independent third party, we just happen to have a channel on the official Discord."]]]])