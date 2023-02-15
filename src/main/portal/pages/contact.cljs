(ns portal.pages.contact)

(defn $contact-page []
  [:div
   [:h3 "Contact Us"]
   [:p
    "Trying to contact us? Write us, it's easy!"]
   [:p
    "Send an email to the address below and we'll be in touch as soon as we can"]
   [:p
    [:a
     {:href "mailto:hello@captains-haven.org"
      :target "_blank"}
     "hello@captains-haven.org"]]])