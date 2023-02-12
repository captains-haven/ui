(ns portal.core
    (:require
     [clojure.pprint :refer [pprint]]
     [clojure.edn :refer [read-string]]
     [cljs.core.async :refer [go]]
     [cljs.core.async.interop :refer-macros [<p!]]
     [reagent.core :as r]
     [reagent.dom :as rdom]
     [bide.core :as bide]))

(def default-token "e5f14974109fe5d155f10e05767015b7cfc6ead4f09c0e1837eb25558c70b3e0a418dd619482868e48f5985d853edd469286c4c710c1eae419ec438ac107e5789551032cf0083ba93c8174f665b56e78bf0a41630e2607cb1878590f1775fdc64d82cff281f96b3603cbf9ee0c7ba4b74331e1d79eb009df89cbbe903acfca45")

(defn ls-set [k v]
  (.setItem js/window.localStorage
            k
            (prn-str v)))

(defn ls-get [k]
  (read-string
   (.getItem js/window.localStorage
             k)))

(def app-state
  (r/atom
   {:path "/"
    :user-token (ls-get "auth-token")
    :page-component [:div "404"]}))

(defn user-or-default-token []
  (or (:user-token @app-state)
      default-token))

(defn fetch-resource [resource]
    (.then
     (.then
      (js/window.fetch
       (str "https://api.captains-haven.org/" resource "?populate=*")
       #js{"headers" #js{"Authorization" (str "bearer " (user-or-default-token))}})
      #(.json %))
     #(js->clj % :keywordize-keys true)))

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
  [:a {:onClick (fn [ev]
                  (.preventDefault ev)
                  (go-to href))
       :href href} title])

(defn $debug [data]
  [:div [:pre (with-out-str (pprint data))]])

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
                   [:img
                    {:width 300
                     :height 300
                     :src (str "https://uploads.captains-haven.org" (-> attrs :thumbnail :data :attributes :url))}]
                   [:div
                    "Published at: " (:publishedAt attrs)]
                   [:div
                    "Last Update: " (:updatedAt attrs)]]))
               (:data @items)))
        [$debug @items]])})))

(defn $blueprints-list []
  (let [items (r/atom [])]
    (r/create-class
     {:component-did-mount
      (fn []
        (go
          (let [found-items (<p! (fetch-resource "blueprints"))]
            (reset! items found-items))))
      :reagent-render
      (fn []
        [:div
         (doall
          (map (fn [i]
                 (let [attrs (:attributes i)]
                   [:div.blueprint-list-item
                    [:h2
                     [$link
                      (:title attrs)
                      (str "/blueprints/" (:id i))]]
                    [:div
                     "By " (-> attrs :author :data :attributes :username)]
                    [:div
                     (:description attrs)]
                    [:img
                     {:width 300
                      :src (str "https://uploads.captains-haven.org" (-> attrs :thumbnail :data :attributes :url))}]
                    [:div
                     "Blueprint Data:"
                     [:pre (:blueprint_data attrs)]]
                    [:div
                     [:button
                      {:onClick (fn []
                                  ;;     navigator.clipboard.writeText (base64_encoded);
                                  ;; window.alert ("Sharable text has been copied to your clipboard")
                                  (.writeText js/navigator.clipboard
                                              (:blueprint_data attrs))
                                  (.alert js/window "Blueprint data has been copied to your clipboard!"))}
                      "Copy Blueprint to Clipboard"]]
                    [:div
                     "Published at: " (:publishedAt attrs)]
                    [:div
                     "Last Update: " (:updatedAt attrs)]]))
               (:data @items)))
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
   "Home"
   [:div "Wanna check out some cool mods? Check this out: "]
   [$link "mods" "/mods"]])

(defn $mod-page [{:keys [id]}]
  [:div
   "Home"
   [:div "Wanna check out some cool mods? Check this out: "]
   [$link "mods" "/mods"]])

(defn $blueprints-page [{:keys [id]}]
  [:div
   "Blueprints"
   [$link "Upload Blueprint" "/blueprints/new"]
   [$blueprints-list]])

(defn $text-input [{:keys [label type placeholder value onChange]
                    :or {label "Input Label"
                         type "text"
                         value nil
                         onChange (fn [])
                         placeholder "Input Placeholder"}}]
  [:div
   [:label
    [:div label]
    [:input
     {:type type
      :onChange onChange
      :value value
      :placeholder placeholder}]]])

(defn handle-text-input-change [s k]
   (fn [ev]
     (let [v (-> ev .-target .-value)]
       (swap! s assoc k v))))

(defn $btn [{:keys [label onClick]}]
  [:div
   [:button
    {:onClick onClick}
    label]])

(defn $blueprints-new-page []
  (let [s (r/atom {:title ""
                   :description ""
                   :blueprint_data ""
                ;;    :author {:connect [{:id (:id (ls-get "user"))}]}
                   :author (:id (ls-get "user"))
                   :res nil})
        submit-blueprint
        (fn []
          (go
            (println "creating")
            (let [res (<p! (create-resource "blueprints" @s))]
              (println "created")
              (swap! s assoc :res res))))]
    (r/create-class
    {:reagent-render
     (fn []
       [:div
        [:div "So you wanna add a new blueprint? Go right ahead:"]
        [$text-input {:label "Title"
                      :onChange (handle-text-input-change s :title)
                      :value (:title @s)
                      :placeholder "Blueprint Title"
                      }]
        [$text-input {:label "Description"
                      :onChange (handle-text-input-change s :description)
                      :value (:description @s)
                      :placeholder "Description"}]
        [$text-input {:label "Blueprint Data"
                      :onChange (handle-text-input-change s :blueprint_data)
                      :value (:blueprint_data @s)
                      :placeholder "B2416:H4sIAAAAAAAACnVWzW8bxxV...."}]
        [$btn {:label "Submit"
               :onClick submit-blueprint}]
        [$debug @s]])})))

(defn $signup-page []
  (let [s (r/atom {:username ""
                   :email ""
                   :res nil})
        handle-change (fn [k]
                        (fn [ev]
                          (let [v (-> ev .-target .-value .trim)]
                            (swap! s assoc k v))))
        submit-signup (fn []
                        (go
                          (let [res (<p! (trigger-login (:email @s)
                                                    (:username @s)))]
                            (swap! s assoc :res res))))]
   (r/create-class
    {:reagent-render
     (fn []
       [:div
        (when-let [err (-> @s :res :error)]
          [:div
           [:div (-> @s :res :error :name)]
           [:div (-> @s :res :error :message)]])
        [$text-input {:label "Username"
                      :value (:username @s)
                      :onChange (handle-change :username)
                      :type "text"
                      :placeholder "Username"}]
        [$text-input {:label "Email"
                      :value (:email @s)
                      :onChange (handle-change :email)
                      :type "email"
                      :placeholder "email@example.com"}]
        [$btn {:label "Signup"
               :onClick (fn []
                          (submit-signup))}]
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
              )))))
    :reagent-render
    (fn []
      [:div
      "Trying to login are we?: "])}))

(comment
  (pprint (ls-get "auth-token")))

(def router
  (bide/router [["/home" ::home]
                ["/mods" ::mods]
                ["/mods/:id" ::mods]
                ["/blueprints" ::blueprints]
                ["/blueprints/new" ::blueprints-new]
                ["/signup" ::signup]
                ["/login" ::login]]))

(def pages
  {::home [$home-page]
   ::mods [$mods-page]
   ::blueprints [$blueprints-page]
   ::blueprints-new [$blueprints-new-page]
   ::signup [$signup-page]
   ::login [$login-page]})

(defn get-page-from-path [path]
  (let [match (bide/match router path)]
    (get pages (first match)
         [:div
          [:h1 "Page not found"]])))

(defn go-to [path]
  (println "sending pushState" path)
  (js/window.history.pushState
    #js{}
    ""
    path)
  (swap! app-state assoc
         :path path
         :page-component (get-page-from-path path)))

(defn $menu []
  (let [items [{:title "Home"
                :path "/home"}
               {:title "Mods"
                :path "/mods"}
               {:title "Blueprints"
                :path "/blueprints"}
               {:title "Signup"
                :path "/signup"}]]
    [:div
     (doall
       (map (fn [item]
             [:div
              {:key (:path item)}
              [:a
               {:class (when (or
                              (.startsWith (:path item)
                                           (:path @app-state))
                              (= (:path item) (:path @app-state)))
                         "active")
                :onClick (fn [ev]
                           (.preventDefault ev)
                           (go-to (:path item)))
                :href (:path item)}
               (:title item)]])
           items))]))

(defn $app []
    [:div
     [$menu]
     [:div (:page-component @app-state)]
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
            :page-component (get-page-from-path js/window.location.pathname))
     )))

(defn render []
  (rdom/render [$app] (js/document.getElementById "root")))

(defn -main []
  (swap! app-state assoc
         :path js/window.location.pathname
         :page-component (get-page-from-path js/window.location.pathname))
  (listen-for-popstate)
  (when (= js/window.location.pathname "/")
    (go-to "/home"))
;;   (go
;;     (let [mods (<p! (fetch-resource "mods"))]
;;       (pprint mods)))
  (render))

(defn ^:dev/after-load start []
  (-main))

(.addEventListener
 js/window
 "DOMContentLoaded"
 (fn []
   (start)))