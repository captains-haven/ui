(ns portal.pages.privacy-policy)

(defn $privacy-policy-page []
  [:div
   {:style {:max-width 600}}
   [:h3 "Privacy Policy"]
   [:div [:p "We don’t collect any personal data besides the email you provide us with for doing the user login with. Nothing you do on Captain's Haven is linked with any other personally identifiable information such as your location, IP or other device statistics. The email is only being used for authentication purposes. You’ll never receive any other emails from us unless you’ve requested it yourself. We’re also not selling any data whatsoever, and only share your email with SMTP2GO when absolutely neccessary (to send login emails)."]]
   [:div [:p "Any information that we store, like number of HTTP requests that each website gets, has its personally identifiable information stripped from it, meaning we can’t see your IP address or any other data not related to the HTTP request itself."]]
   [:div [:p "We don’t run or add any tracking cookies or other techniques for client-side tracking, meaning your personal data never leaves your browser."]]
   [:div [:p "If you do contact us via email for support, we do have temporary storage of your email and it’s contents, as we try to help you with your questions. Once the issue has been solved, we delete all messages we’ve sent and received, meaning less hassle for us to deal with storage of emails, and more security for you as there is no data to be stolen."]]
   [:div [:p "If you have any questions, feedback or concerns regarding our Privacy Policy, you can reach out to compliance@captains-haven.org"]]
   [:div [:p "Last Update: 2023-02-06"]]])