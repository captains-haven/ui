(ns portal.components.menu
  (:require
   [portal.state :refer [app-state]]
   [portal.router :refer [go-to]]
   [portal.auth :refer [is-logged-in]]))

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
                 :path "/blueprints/sorted-by/latest/desc"}
                {:title "Maps"
                 :disabled true
                 :path "/maps"}
                {:title "Savegames"
                 :disabled true
                 :path "/savegames"}
                {:title "Media"
                 :disabled true
                 :path "/media"}]
               [;;{:title "About"
                ;; :path "/about"}
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