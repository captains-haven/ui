(ns portal.translate
  (:require
   [clojure.edn :as edn]
   [cljs.core.async :refer [go]]
   [cljs.core.async.interop :refer [<p!]]
   [taoensso.tempura :as tempura :refer [tr]]))

(defonce dictionary-cache (atom {}))

(defn init-cache [locale]
  (go
    (let [res (<p! (.fetch
                    js/window
                    (str "/translations/" (name locale) ".edn")))
          text (<p! (.text res))
          parsed (edn/read-string text)]
      (swap! dictionary-cache assoc locale parsed))))

(defn translate [locale key]
  (tr {:dict @dictionary-cache} [locale :en] [key]))

(comment
  ;; First call init-cache with translations that should be available
  (init-cache :en)
  (init-cache :fr)

  ;; After init-cache call, you can use `translate` in order to translate a text snippet
  (translate :en :test)
  (translate :fr :test)
  (translate :fr :only-in-en))