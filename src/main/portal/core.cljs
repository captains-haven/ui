(ns portal.core
    (:require
     [portal.time :refer [simple-datetime]]
     [clojure.pprint :refer [pprint]]
     [clojure.string :as str]
     [clojure.edn :refer [read-string]]
     [cljs.core.async :refer [go]]
     [cljs.core.async.interop :refer-macros [<p!]]
     [reagent.core :as r]
     [reagent.dom :as rdom]
     [bide.core :as bide]))

;; (defn pprint [s]
;;   (println (prn-str s)))

(def default-token "e5f14974109fe5d155f10e05767015b7cfc6ead4f09c0e1837eb25558c70b3e0a418dd619482868e48f5985d853edd469286c4c710c1eae419ec438ac107e5789551032cf0083ba93c8174f665b56e78bf0a41630e2607cb1878590f1775fdc64d82cff281f96b3603cbf9ee0c7ba4b74331e1d79eb009df89cbbe903acfca45")

(defn ls-set [k v]
  (.setItem js/window.localStorage
            k
            (prn-str v)))

(defn ls-get [k]
  (read-string
   (.getItem js/window.localStorage
             k)))

(def hide-debug? true)

(def app-state
  (r/atom
   {:path "/"
    :user-token (ls-get "auth-token")
    :user (ls-get "user")
    :page-component [:div "404"]}))

(defn user-or-default-token []
  (or (:user-token @app-state)
      default-token))

(defn is-logged-in []
  (boolean (:user @app-state)))

(defn fetch-resource [resource]
    (.then
     (.then
      (js/window.fetch
       (str "https://api.captains-haven.org/" resource "?populate=*")
       #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
      #(.json %))
     #(js->clj % :keywordize-keys true)))

(defn fetch-resource-by-slug [resource slug]
  (.then
   (.then
    (js/window.fetch
     (str "https://api.captains-haven.org/" resource "?populate=*&filters[Slug][$eq]=" slug)
     #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(comment
  (go
    (pprint (<p! (fetch-resource-by-slug "blueprints" "another-fertalizer-ii")))))

(defn create-resource [resource resource-data]
  (.then
   (.then
    (js/window.fetch
     (str "https://api.captains-haven.org/" resource)
     #js{"method" "POST"
         "headers" #js{"Authorization" (str "bearer " (user-or-default-token))
                       "Content-Type" "application/json"}
         "body" (js/JSON.stringify (clj->js {:data resource-data}))})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn trigger-login [email username]
  (.then
   (.then
    (js/window.fetch
     "https://admin.captains-haven.org/api/passwordless/send-link"
     #js{"method" "POST"
         "headers" #js{"Content-Type" "application/json"}
         "body" (js/JSON.stringify #js{"email" email
                                       "username" username})})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(comment
  (go
    (let [res (<p! (trigger-login "user2@victor.earth"
                                  "user2"))]
      (pprint res))))

(defn verify-login [token]
  (.then
   (.then
    (js/window.fetch
     (str
      "https://admin.captains-haven.org/api/passwordless/login?loginToken="
      token))
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(comment
  (go
    (let [res (<p! (verify-login "31Rp-ifm5X-itoPo5Aus"))]
      (pprint res))))

(declare go-to) ;; declared after router, but need to use in pages declared before router, so...

(defn $link [title href]
  [:a.link
   {:onClick (fn [ev]
                  (.preventDefault ev)
                  (go-to href))
    :href href}
   title])

(defn $link-btn [title href]
  [:a.link-btn
   {:onClick (fn [ev]
               (.preventDefault ev)
               (go-to href))
    :href href}
   title])

(defn $debug [data]
  (when-not hide-debug?
   [:div.debug
    [:pre (with-out-str (pprint data))]]))

(defn relative->absolute-url [relative]
  (str "https://uploads.captains-haven.org" relative))

(defn $mod-list []
  (let [items (r/atom [])]
   (r/create-class
    {:component-did-mount
     (fn []
       (go
         (let [found-items (<p! (fetch-resource "mods"))]
           (reset! items found-items))))
     :reagent-render
     (fn []
       [:div
        [:h1 "All Mods"]
        [:div "A list of all the mods available"]
        (doall
          (map (fn [i]
                 (let [attrs (:attributes i)]
                  [:div
                   [:h2
                    [$link
                     (:title attrs)
                     (str "/mods/" (:id i))]]
                   [:div
                    "By " (:author attrs)]
                   [:div
                    [:a
                     {:href (:source_code attrs)
                      :target "_blank"}
                     (:source_code attrs)]]
                   [:div.mod-image
                    [:img
                     {:width 300
                      :src (str "https://uploads.captains-haven.org" (-> attrs :thumbnail :data :attributes :url))}]]
                   [:div
                    "Published at: " (:publishedAt attrs)]
                   [:div
                    "Last Update: " (:updatedAt attrs)]]))
               (:data @items)))
        [$debug @items]])})))

(defn $list-blueprint-items [items]
  (fn []
   (let [sorted-items (into (sorted-map-by (fn [key1 key2] (compare [(key2 items) key2] [(key1 items) key1]))) items)]
     [:div
      {:style {:margin 5
               :width 300}}
      [:div
       {:style {:font-size "22px"}}
       "Used items:"]
      [:div
       {:style {:display "flex"
                :flex-direction "column"
                :max-width 400}}
       (doall
          (map (fn [[k v]]
                 [:div
                  {:style {:display "flex"
                           :flex-direction "row"
                           :justify-content "space-between"
                           :border-bottom "1px solid grey"
                           :margin-top 5
                           :padding-bottom 2
                           :margin-bottom 3}}
                  [:div.ellipsis-overflow
                   {:title k}
                   k]
                  [:div v]])
               sorted-items))]])))

(defn $blueprint-items [blueprint-data]
  (let [is-loading (r/atom false)
        items (r/atom {})
        fetch-items (fn [blueprint-data]
                      (reset! is-loading true)
                      (println "Did we have any blueprint data?")
                      (pprint blueprint-data)
                      (go
                        (let [res (<p! (js/window.fetch
                                        "https://parser.captains-haven.org/blueprint"
                                        #js{:method "POST"
                                            :body blueprint-data}))
                              parsed (<p! (.json res))
                              obj (js->clj parsed :keywordize-keys true)]
                          (reset! items (:items obj))
                          (reset! is-loading false))))]
    (r/create-class
     {:component-did-mount
      (fn [this] (fetch-items blueprint-data)) 
      :reagent-render
      (fn [blueprint-data]
        [:div.items-list
         (when-not (empty? @items)
           [$list-blueprint-items @items])
         ;;[$debug @items]
         ])})))

(defn $blueprint [data full-view?]
  (let [attrs (:attributes data)
        full-href (str "/blueprints/" (:slug attrs))]
   [:div.blueprint
    {:class (when full-view? "full-blueprint")}
    [:div.blueprint-title
     {:class (when-not full-view? "ellipsis-overflow")
      :title (:title attrs)}
     [$link
      (:title attrs)
      full-href]]
    [:div
     "By " (-> attrs :author :data :attributes :username)]
    (when full-view?
      [:div
       "Description:"
       [:br]
       [:p
        (:description attrs)]])
    [$link
     [:div.blueprint-image
      [:img
       {:width 300
        :src (str "https://uploads.captains-haven.org" (-> attrs :thumbnail :data :attributes :url))}]]
     full-href]
    (when full-view?
      [:div
       "Blueprint Data:"
       [:pre (:blueprint_data attrs)]])
    [:div
     [:button.link-btn
      {:onClick (fn []
                                  ;;     navigator.clipboard.writeText (base64_encoded);
                                  ;; window.alert ("Sharable text has been copied to your clipboard")
                  (.writeText js/navigator.clipboard
                              (:blueprint_data attrs))
                  (.alert js/window "Blueprint data has been copied to your clipboard!"))}
      "Copy Blueprint to Clipboard"]]
    (if full-view?
      [:div
       [:div
        "Published at: " (:publishedAt attrs)]
       [:div
        "Last Update: " (:updatedAt attrs)]]
      [:div
       {:style {:margin-top 10}}
       (simple-datetime (js/Date. (:updatedAt attrs)))])
    (when (and full-view?
               (= (-> attrs :author :data :id)
                  (-> @app-state :user :id)))
      [$link
       "Edit"
       (str "/blueprints/"
            (:id data)
            "/edit")])]))

(defn $loading []
  (let [iterations (r/atom 0)
        interval (r/atom nil)]
    (r/create-class
     {:component-will-unmount
      (fn []
        (js/window.clearInterval @interval)
        (reset! interval nil))
      :component-did-mount
      (fn []
        (reset! interval
                (js/window.setInterval
                  (fn []
                    (if (< @iterations 3)
                      (swap! iterations inc)
                      (reset! iterations 0)))
                  100)))
      :reagent-render
      (fn []
        [:div
         "Loading"
         (reduce (fn [acc _curr]
                   (str acc "."))
                 ""
                 (range @iterations))])})))

(defn $blueprints-list []
  (let [loading? (r/atom false)
        items (r/atom [])]
    (r/create-class
     {:component-did-mount
      (fn []
        (reset! loading? true)
        (go
          (let [found-items (<p! (fetch-resource "blueprints"))]
            (reset! items (reverse (sort-by :id (:data found-items))))
            (reset! loading? false))))
      :reagent-render
      (fn []
        [:div
         (if @loading?
           [:div
            [$loading]]
          [:div.blueprint-list
           (doall
            (map (fn [i]
                   ^{:key (:id i)}
                   [:div.blueprint-list-item
                    [$blueprint i false]])
                 @items))])
         [$debug @items]])})))

(defn $mods-page []
  [:div
   "Mods"
   [$mod-list]
   [:div "Didn't find what you were looking for?"]
   [:div "Try here:"]
   [$link "Home" "/"]])

(defn $home-page []
  [:div
   (if-let [username (-> @app-state :user :username)]
     [:h3 "Ahoy " username ", welcome back to Captain's Haven"]
     [:h3 "Ahoy captain, welcome to Captain's Haven"])
   [:div "Here we have everything for budding captains:"]
   [:ul
    [:li "Mods to change your game experience"]
    [:li "Blueprints to learn designs from others and share with the community"]
    [:li "Savegames to learn even bigger designs"]
    [:li "Timelapses and screenshots because sometimes we just need to stare at some photos and videos to feel good about ourselves"]]])

(defn $mod-page [{:keys [id]}]
  [:div
   "Home"
   [:div "Wanna check out some cool mods? Check this out: "]
   [$link "mods" "/mods"]])

(defn $blueprints-page [{:keys [id]}]
  [:div
   [:div
    {:style {:display "flex"
             :justify-content "space-between"
             :padding 10}}
    [:h2 "All Blueprints"]
    (when (is-logged-in)
     [$link-btn "Upload a new Blueprint" "/blueprints/new"])]
   [$blueprints-list]])

(defn $blueprint-page [{:keys [slug]}]
  (let [item (r/atom nil)
        blueprint-data (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-item (<p! (fetch-resource-by-slug "blueprints" slug))]
            (reset! item (first (:data found-item)))
            (reset! blueprint-data (-> found-item
                                       :data
                                       first
                                       :attributes
                                       :blueprint_data)))))
      :reagent-render
      (fn [{:keys [slug]}]
        [:div.blueprint-page
         [$blueprint @item true]
         (when @blueprint-data
          [$blueprint-items @blueprint-data])])})))

(defn $edit-blueprint-page [{:keys [id]}]
  (let [item (r/atom nil)]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-item (<p! (fetch-resource (str "blueprints/" id)))]
            (reset! item (:data found-item)))))
      :reagent-render
      (fn []
        [:div
         [:h2 "EDITING"]
         [$blueprint @item true]
         [$debug @item]])})))

(defn $text-input [{:keys [label type placeholder value onChange disabled]
                    :or {label "Input Label"
                         type "text"
                         value nil
                         disabled false
                         onChange (fn [])
                         placeholder "Input Placeholder"}}]
  [:div
   {:class (when disabled "disabled")}
   [:label
    [:div label]
    [:input
     {:type type
      :onChange onChange
      :disabled disabled
      :value value
      :placeholder placeholder}]]])

(defn handle-text-input-change [s k]
   (fn [ev]
     (let [v (-> ev .-target .-value)]
       (swap! s assoc k v))))

(defn $btn [{:keys [label onClick disabled]}]
  [:div
   [:button 
    {:disabled disabled
     :onClick (if disabled
                (fn [])
                onClick)}
    label]])

(defn post-formdata [path form-data]
  (.then
   (.then
    (js/window.fetch
     (str "https://api.captains-haven.org/" path)
     #js{"method" "post"
         "headers" #js{"Authorization" (str "bearer " (user-or-default-token))}
         "body" form-data})
    #(.json %))
   #(js->clj % :keywordize-keys true)))

(defn upload-file [input-field]
  (let [form-data (js/FormData.)
        file (-> input-field .-files first)]
    (pprint file)
    (.append form-data "files" file)
    (pprint form-data)
    (post-formdata "upload" form-data)))

(defn slugify
  [string]
  ((comp #(str/replace % #" " "-")
         str/lower-case
         str/trim)
   string))

(defn $blueprints-new-page []
  (let [is-loading (r/atom false)
        s (r/atom {:title ""
                   :description ""
                   :blueprint_data ""
                   :items_count 1
                   :items {}
                ;;    :author {:connect [{:id (:id (ls-get "user"))}]}
                   :author (:id (ls-get "user"))
                   :thumbnail nil
                   :res nil
                   :upload-res nil
                   :blueprint-error nil
                   :error nil})
        upload-res (r/atom nil)
        parse-blueprint-timeout (r/atom nil)
        parse-blueprint (fn [data]
                          (swap! s assoc
                                 :blueprint_data data
                                 :blueprint-error nil)
                          (when @parse-blueprint-timeout
                            (js/window.clearTimeout @parse-blueprint-timeout)
                            (reset! parse-blueprint-timeout nil))
                          (reset! parse-blueprint-timeout
                                  (js/window.setTimeout
                                    (fn []
                                      (reset! is-loading true)
                                      (go
                                        (let [res (<p! (js/window.fetch
                                                        "https://parser.captains-haven.org/blueprint"
                                                        #js{:method "POST"
                                                            :body data}))
                                              parsed (<p! (.json res))
                                              obj (js->clj parsed :keywordize-keys true)]
                                          (if (:error obj)
                                            (do
                                              (swap! s assoc
                                                     :blueprint-error (:error obj))
                                              (reset! is-loading false))
                                            (do
                                              (pprint obj)
                                              (swap! s assoc
                                                     :title (:name obj)
                                                     :description (:desc obj)
                                                     :items_count (:items_count obj)
                                                     :items (:items obj))
                                              (reset! is-loading false))))))
                                    100)))
        submit-blueprint
        (fn []
          (if (nil? (:thumbnail @s))
            (swap! s assoc :error {:name "Missing thumbnail!" :message "You need to upload a thumbnail to go with your blueprint"})
            (do
              (reset! is-loading true)
              (go
                (println "creating")
                (let [to-submit (assoc @s :slug (-> @s :title slugify))
                      res (<p! (create-resource "blueprints" to-submit))]
                  (if (:error res)
                    (do
                      (if (= (-> res :error :message) "This attribute must be unique")
                       (swap! s assoc :error {:name "Title not unique" :message "Title has to be unique, seems another blueprint already use the same title, please change it"})
                        (swap! s assoc :error (:error res)))
                      (reset! is-loading false))
                    (do
                      (pprint res)
                      (println "created")
                      (swap! s assoc :res res)
                      (reset! is-loading false)
                      (go-to (str "/blueprints/" (-> res :data :attributes :slug))))))))))]
    (r/create-class
    {:reagent-render
     (fn []
       [:div.new-blueprint
        [:div "So you wanna add a new blueprint? Go right ahead."]
        [$text-input {:label "Blueprint Data"
                      :disabled @is-loading
                      :onChange #(parse-blueprint (-> % .-target .-value))
                      :value (:blueprint_data @s)
                      :placeholder "B2416:H4sIAAAAAAAACnVWzW8bxxV...."}]
        (when (:blueprint-error @s)
          [:div (:blueprint-error @s)])
        [$text-input {:label "Title"
                      :disabled @is-loading
                      :onChange (handle-text-input-change s :title)
                      :value (:title @s)
                      :placeholder "Blueprint Title"
                      }]
        [$text-input {:label "Description"
                      :disabled @is-loading
                      :onChange (handle-text-input-change s :description)
                      :value (:description @s)
                      :placeholder "Description"}]
        [$text-input {:label "Items Count"
                      :type "number"
                      :disabled @is-loading
                      ;; :onChange (handle-text-input-change s :items_count)
                      :value (:items_count @s)
                      :placeholder "1"}]
        [:div
         [:label
          [:div "Thumbnail"]
          [:input
           {:type "file"
            :disabled @is-loading
            :name "file"
            :onChange (fn [ev]
                        (reset! is-loading true)
                        (swap! s assoc :error nil)
                        (pprint (-> ev .-target .-value))
                        (pprint (upload-file (-> ev .-target)))
                        (go
                          (let [res (<p! (upload-file (.-target ev)))]
                            (reset! upload-res res)
                            (swap! s assoc :thumbnail (-> res first :id))
                            (reset! is-loading false))))}]]]
        (when-let [image-url (-> @upload-res first :url)]
          [:img
           {:width 300
            :src (relative->absolute-url image-url)}])
        (when (:error @s)
          [:div
           (-> @s :error :name)
           " - "
           (-> @s :error :message)])
        [$btn {:label "Submit"
               :disabled @is-loading
               :onClick submit-blueprint}]
        (when @is-loading
          [$loading])
        (when-not (empty? (:items @s))
          [$list-blueprint-items (:items @s)])
        [$debug @s]])})))

(defn $signup-page []
  (let [accept? (r/atom false)
        s (r/atom {:username ""
                   :email ""
                   :res nil
                   :loading? false})
        is-loading (fn [] (:loading? @s))
        handle-change (fn [k]
                        (fn [ev]
                          (let [v (-> ev .-target .-value .trim)]
                            (swap! s assoc k v))))
        submit-signup (fn []
                        (swap! s assoc :loading? true)
                        (go
                          (let [res (<p! (trigger-login (:email @s)
                                                    (:username @s)))]
                            (swap! s assoc :res res :loading? false))))]
   (r/create-class
    {:reagent-render
     (fn []
       [:div.signup-page
        [:h2 "Signup"]
        (when-let [err (-> @s :res :error)]
          [:div
           [:div (-> @s :res :error :name)]
           [:div (-> @s :res :error :message)]])
        [$text-input {:label "Username"
                      :disabled (is-loading)
                      :value (:username @s)
                      :onChange (handle-change :username)
                      :type "text"
                      :placeholder "Username"}]
        [$text-input {:label "Email"
                      :disabled (is-loading)
                      :value (:email @s)
                      :onChange (handle-change :email)
                      :type "email"
                      :placeholder "email@example.com"}]
        [:label
         {:class (when (is-loading) "disabled")}
         [:input
          {:type "checkbox" 
           :onChange (fn [ev]
                       (let [v (-> ev .-target .-checked boolean)]
                         (reset! accept? v)))}]
         "Accept "
         [$link "Terms & Conditions" "/terms-and-conditions"]
         " and "
         [$link "Privacy Policy" "/privacy-policy"]
         "?"]
        [$btn {:label "Signup"
               :disabled (or (not @accept?) (is-loading))
               :onClick (fn []
                          (submit-signup))}]
        (when (is-loading)
          [$loading])
        (when (-> @s :res :sent)
          [:div
           "Email has been sent to " (-> @s :res :email) ". Please check your inbox to confirm."])
        [$debug @s]])})))

(defn $login-page []
  (r/create-class
   {:component-did-mount
    (fn []
      (let [query-param js/window.location.search
            splitted (.split query-param "=")
            login-token (second splitted)]
        (when login-token
          (pprint login-token)
          (go
            (let [res (<p! (verify-login login-token))]
              (pprint res)
              (ls-set "auth-token" (:jwt res))
              (ls-set "user" (:user res))
              (set! js/window.location.pathname "/")
              )))))
    :reagent-render
    (fn []
      [:div
      "Trying to login are we?: "])}))

(comment
  (pprint (ls-get "auth-token")))

(defn $not-yet-page []
  [:div "This page hasn't yet been built"])

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
     "hello@captains-haven.org"]]])

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

(defn $terms-and-conditions-page []
  [:div
   {:style {:max-width 600}}
   [:h3 "Terms & Conditions"]
   [:div [:p "The terms and conditions (TOS) described below apply to all use of Captain's Haven and all services available through Captain's Haven. By using Captain's Haven, you agree to be bound by these terms, so please read this Agreement carefully before using Captain's Haven."]]
   [:div [:p "Captain's Haven has the sole discretion to change or replace any part of this agreement You are responsible for periodically checking this agreement for any changes. Any new Service additions or enhancements are subject to these Terms of Service. You consent to any such changes by continuing to use Captain's Haven."]]
   [:div [:p "We reserve the right to terminate your account if you violate any of the terms described below."]]
   
   [:h4 "Account Terms"]
   [:div [:p "You are required to use your email in order to use Captain's Haven. You are solely responsible for maintaining the security of your Captain's Haven account and it’s connected email account. You must notify us of any security breach as soon as possible. Captain's Haven will not be liable for any loss or damage if you fail to comply with this security obligation."]]
   [:div [:p "Accounts registered by automated methods are not allowed. You must be a real person to use this service."]]
   [:div [:p "You are responsible for all activity that happens under your account."]]
   [:div [:p "One person or legal entity may not sign up for more than one free account."]]
   [:div [:p "You may not use the Captain's Haven for any illegal or unauthorized purpose. You must not violate any laws in your jurisdiction in your usage of Captain's Haven."]]
   
   [:h4 "Changes to Captain's Haven"]
   [:div [:p "Captain's Haven reserves the right to modify or discontinue, temporarily or permanently, Captain's Haven with or without notice."]] 
   [:div [:p "Captain's Haven will not be liable to you or anyone for any modification, suspension or discontinuance of Captain's Haven."]]
   [:h4 "Copyright and Content Ownership"]
   [:div [:p "You retain all intellectual property rights over all content in your account. Your profile and any material materials uploaded also remains yours."]]
   [:div [:p "Captain's Haven has the right in its sole discretion to refuse or remove any content that is available through Captain's Haven."]]
   
   [:h4 "General Conditions"]
   [:div [:p "Captain's Haven is provided on an \"as is\" and \"as available\" basis."]]
   [:div [:p "Captain's Haven uses third party vendors and hosting partners to provide the hardware, software, networking, storage, and related technology required to run Captain's Haven."]]
   [:div [:p "We may remove content and accounts containing content that we determine in our sole discretion to be unlawful, offensive, threatening, libelous, defamatory, pornographic, obscene or otherwise objectionable or violates any party's intellectual property or these Terms of Service."]]
   [:div [:p "You must not transmit any worms or viruses or any content of a destructive nature."]]
   [:div [:p "Captain's Haven does not promise that the service will be uninterrupted, timely, secure, or error-free. We make every effort to ensure that it will satisfy your requirements and expectations, but make no promises in this regard."]]
   [:div [:p "Captain's Haven cannot be held liable for system down time, crashes or data loss. We cannot be held liable for any predicated estimate of profits which a client would have gained if Captain's Haven was functioning."]]
   [:div [:p "The failure of Captain's Haven to exercise or enforce any right or provision of the Terms of Service shall not constitute a waiver of such right or provision. This Terms of Service supersedes any prior agreements or prior versions of Terms of Service between you and Captain's Haven. You agree that these Terms of Service and your use of Captain's Haven are governed under Spanish and European law."]]
   [:div [:p "If you choose to provide Captain's Haven with your information, you consent to the transfer and storage of that information on our servers located in Germany, as well as your email being shared with SMTP2GO."]]
   [:div [:p "If you have any questions regarding this Terms of Service, you can reach us at support@captains-haven.org"]]
   
   [:div [:p "Last Update: 2023-02-06"]]])

(def router
  (bide/router [["/home" ::home]
                ["/mods" ::mods]
                ["/mods/:id" ::mods]
                ["/blueprints" ::blueprints]
                ["/blueprints/new" ::blueprints-new]
                ["/blueprints/:slug" ::blueprint]
                ["/blueprints/:slug/edit" ::blueprint-edit]
                ["/game-versions" ::game-versions]
                ["/maps" ::maps]
                ["/savegames" ::savegames]
                ["/media" ::media]
                ["/about" ::about]
                ["/privacy-policy" ::privacy-policy]
                ["/terms-and-conditions" ::terms-and-conditions]
                ["/signup" ::signup]
                ["/login" ::login]]))

(def pages
  {::home $home-page
   ::about $about-page
   
   ::privacy-policy $privacy-policy-page
   ::terms-and-conditions $terms-and-conditions-page
   
   ::blueprints $blueprints-page
   ::blueprints-new $blueprints-new-page
   ::blueprint $blueprint-page
   ::blueprint-edit $edit-blueprint-page
   
   ::mods $mods-page
   
   ::versions $not-yet-page
   ::maps $not-yet-page
   ::savegames $not-yet-page
   ::media $not-yet-page
   
   ::signup $signup-page
   ::login $login-page})

(defn get-page-from-path [path]
  (let [match (bide/match router path)]
    (get pages (first match)
         [:div
          [:h1 "Page not found"]])))

(defn get-args-from-path [path]
  (let [match (bide/match router path)]
    (second match)))

(defn go-to [path]
  ;; (println "sending pushState" path)
  (js/window.history.pushState
    #js{}
    ""
    path)
  (swap! app-state assoc
         :path path
         :page-component (get-page-from-path path)
         :page-args (get-args-from-path path)))

(defn $menu-item [item]
  [:div
   {:key (:path item)
    :class (when (:disabled item)
             "disabled")}
   [:a.menu-link
    {:class (when (or
                   (.startsWith (:path item)
                                (:path @app-state))
                   (= (:path item) (:path @app-state)))
              "active")
     :onClick (fn [ev]
                (.preventDefault ev)
                (if (:action item)
                  ((:action item))
                  (go-to (:path item))))
     :href (:path item)}
    (if (fn? (:title item))
      ((:title item))
      (:title item))]])

(defn $menu []
  (let [items [[{:title "Home"
                 :path "/home"}
                {:title "Mods"
                 :disabled true
                 :path "/mods"}
                {:title "Blueprints"
                 :path "/blueprints"}
                {:title "Maps"
                 :disabled true
                 :path "/maps"}
                {:title "Savegames"
                 :disabled true
                 :path "/savegames"}
                {:title "Media"
                 :disabled true
                 :path "/media"}]
               [{:title "About"
                 :path "/about"}
                ;; {:title "Login"
                ;;  :path "/login"}
                {:title (fn []
                          (if (is-logged-in)
                            "Logout"
                           "Signup / Login"))
                 :action (fn []
                           (if (is-logged-in)
                             (do (.clear js/localStorage)
                                 (set! js/window.location.pathname "/"))
                             (go-to "/signup")))
                 :path "/signup"}]]]
    [:div.menu
     [:div.menu-left
      (doall
       (map (fn [item]
              ^{:key (:path item)}
              [$menu-item item])
            (first items)))]
     [:div.menu-right
      (doall
       (map (fn [item]
              ^{:key (:path item)}
              [$menu-item item])
            (second items)))]]))

(defn $app []
    [:div
     [$link 
      [:div.app-title
       [:img
        {:src "/favicon-32x32.png"}]
       "Captain's Haven"]
      "/home"]
     [$menu]
     [:div.page-wrapper
      [(:page-component @app-state)
       (:page-args @app-state)]]
     [$debug @app-state]])

(defn listen-for-popstate []
  (println "setting up listener")
  (.addEventListener
   js/window
   "popstate"
   (fn []
     (println "popstate happened")
     (swap! app-state assoc
            :path js/window.location.pathname
            :page-component (get-page-from-path js/window.location.pathname)
            :page-args (get-args-from-path js/window.location.pathname))
     )))

(defn render []
  (rdom/render [$app] (js/document.getElementById "root")))

(defn -main []
  (swap! app-state assoc
         :path js/window.location.pathname
         :page-component (get-page-from-path js/window.location.pathname)
         :page-args (get-args-from-path js/window.location.pathname))
  (listen-for-popstate)
  (when (= js/window.location.pathname "/")
    (go-to "/home"))
  (render))

(defn ^:dev/after-load start []
  (-main))

(.addEventListener
 js/window
 "DOMContentLoaded"
 (fn []
   (start)))
