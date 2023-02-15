(ns portal.pages
  (:require
   [portal.pages.home :refer [$home-page]]
   [portal.pages.changelog :refer [$changelog-page]]
   [portal.pages.about :refer [$about-page]]
   [portal.pages.privacy-policy :refer [$privacy-policy-page]]
   [portal.pages.terms-and-conditions :refer [$terms-and-conditions-page]]
   [portal.pages.not-yet :refer [$not-yet-page]]
   [portal.pages.auth.login :refer [$login-page]]
   [portal.pages.auth.signup :refer [$signup-page]]
   
   [portal.pages.mods.list :refer [$mods-list-page]]
   
   [portal.pages.blueprints.list :refer [$blueprints-list-page]]
   [portal.pages.blueprints.new :refer [$blueprints-new-page]]
   [portal.pages.blueprints.view :refer [$blueprints-view-page]]
   [portal.pages.blueprints.edit :refer [$blueprints-edit-page]]
   
   [portal.pages.news :refer [$news-page]]))

(def pages
  {:home $home-page
   :about $about-page
   :changelog $changelog-page

   :privacy-policy $privacy-policy-page
   :terms-and-conditions $terms-and-conditions-page

   :blueprints $blueprints-list-page
   :blueprints-new $blueprints-new-page
   :blueprint $blueprints-view-page
   :blueprint-edit $blueprints-edit-page

   :mods $mods-list-page

   :versions $not-yet-page
   :maps $not-yet-page
   :savegames $not-yet-page
   :media $not-yet-page

   :signup $signup-page
   :login $login-page
   
   :news $news-page})

(defn init! []
  (set! js/window.app_pages pages))