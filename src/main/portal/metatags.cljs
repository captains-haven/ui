(ns portal.metatags)

(def possible-metatags
  {:og_title "meta[property=\"og:title\"]"
   :og_site_name "meta[property=\"og:site_name\"]"
   :og_description "meta[property=\"og:description\"]"
   :og_image "meta[property=\"og:image\"]"})

(def possible-properties
  {:og_title "og:title"
   :og_site_name "og:site_name"
   :og_description "og:description"
   :og_image "og:image"})

(defn metatag-exists? [selector]
  (boolean
   (.querySelector
    js/document
    selector)))

(defn edit-metatag! [selector new-value]
  (let [el (.querySelector js/document selector)]
    (.setAttribute el "content" new-value)))

(defn create-metatag! [property content]
  (let [el (.createElement
            js/document
            "meta")
        head (.querySelector js/document "head")]
    (.setAttribute el "property" property)
    (.setAttribute el "content" content)
    (.appendChild head el)))

(defn add-or-edit-metadata! [k val]
  (if (metatag-exists? (k possible-metatags))
    (edit-metatag! (k possible-metatags) val)
    (create-metatag! (k possible-properties) val)))

(comment
  (metatag-exists? "title")
  (metatag-exists? (:og_title possible-metatags))
  (create-metatag! (:og_title possible-properties) "Yes")
  (edit-metatag! (:og_title possible-metatags) "no")

  (add-or-edit-metadata! :og_site_name "hello")
  )