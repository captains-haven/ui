(ns portal.components.changelog
  (:require
   [audition.params]))



(defn $changelog-item
  {:audition {:args [:git-item]}}
  [{:keys [subject body author commitHash]}]
  [:div
   {:style {:margin-bottom 15}}
   [:div
    {:style {:font-weight "bolder"}}
    subject]
   [:div body]
   [:div "By: " (:name author)]
   [:div "At: " (:date author)]
   [:div
    [:a
     {:target "_blank"
      :href (str "https://codeberg.org/captains-haven/ui/commit/" commitHash)}
     commitHash]]])

(defn $changelog-list
  {:audition {:args [:git-items]}}
  [items]
  [:div
   (map
    (fn [item]
      ^{:key (:commitHash item)}
      [$changelog-item item])
    items)])

(defmethod audition.params/default-args :git-item []
  {:encoding "",
   :tree "bb6cabf845f9e632c4d5666506dda974ae82a7f6",
   :treeAbbreviated "bb6cabf",
   :parent "",
   :commitHash "ca34d74f8729c42a6aa3f89eefd578420c696471",
   :parentAbbreviated "",
   :committer
   {:name "Captain_Of_Coit",
    :email "admin@captains-haven.org",
    :date "Sun, 12 Feb 2023 13:33:32 +0100",
    :dateISO8601 "2023-02-12T13:33:32+01:00"},
   :refs "",
   :signature {:key "", :signer "", :verificationFlag "N"},
   :author
   {:name "Captain_Of_Coit",
    :email "admin@captains-haven.org",
    :date "Sun, 12 Feb 2023 13:33:32 +0100",
    :dateISO8601 "2023-02-12T13:33:32+01:00"},
   :notes "",
   :subjectSanitized "init",
   :body "",
   :commitHashAbbreviated "ca34d74",
   :subject "init"})

(defmethod audition.params/default-args :git-items []
  [{:encoding "",
    :tree "bba91738cc691acb30dec5f07172114f258cdcfc",
    :treeAbbreviated "bba9173",
    :parent "90d2311247bfae894ed0c9a8ba8efcfdcaa69b62",
    :commitHash "94a1f106e5de967aac8ca7751bd297989d22b36b",
    :parentAbbreviated "90d2311",
    :committer
    {:name "Captain_Of_Coit",
     :email "admin@captains-haven.org",
     :date "Sun, 12 Feb 2023 14:14:40 +0100",
     :dateISO8601 "2023-02-12T14:14:40+01:00"},
    :refs "HEAD -> master, codeberg/master",
    :signature {:key "", :signer "", :verificationFlag "N"},
    :author
    {:name "Captain_Of_Coit",
     :email "admin@captains-haven.org",
     :date "Sun, 12 Feb 2023 14:14:40 +0100",
     :dateISO8601 "2023-02-12T14:14:40+01:00"},
    :notes "",
    :subjectSanitized "sort-bigger-page-size",
    :body "",
    :commitHashAbbreviated "94a1f10",
    :subject "sort + bigger page size"}
   {:encoding "",
    :tree "3ff1bdff292f5466bff37bffc268a1e302aa8ae0",
    :treeAbbreviated "3ff1bdf",
    :parent "cdb4b4435f5adf84bf8ac7cb2926057714d3c4d6",
    :commitHash "90d2311247bfae894ed0c9a8ba8efcfdcaa69b62",
    :parentAbbreviated "cdb4b44",
    :committer
    {:name "Captain_Of_Coit",
     :email "admin@captains-haven.org",
     :date "Sun, 12 Feb 2023 14:02:41 +0100",
     :dateISO8601 "2023-02-12T14:02:41+01:00"},
    :refs "",
    :signature {:key "", :signer "", :verificationFlag "N"},
    :author
    {:name "Captain_Of_Coit",
     :email "admin@captains-haven.org",
     :date "Sun, 12 Feb 2023 14:02:41 +0100",
     :dateISO8601 "2023-02-12T14:02:41+01:00"},
    :notes "",
    :subjectSanitized "Link-to-source-code",
    :body "",
    :commitHashAbbreviated "90d2311",
    :subject "Link to source code"}])