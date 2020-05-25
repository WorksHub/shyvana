(ns shyvana.follow
  "Examplary ns for discovering stream possibilites"
  (:require [shyvana.core :as api]))

(def pitch-feed (api/flat-feed api/client {:type "company" :name "pitchio"}))

(def scala-feed (api/flat-feed api/client {:type "tag" :name "scala"}))

(def clojure-feed (api/flat-feed api/client {:type "tag" :name "clojure"}))

(def functional-feed (api/flat-feed api/client {:type "tag" :name "functional"}))

(def joana
  (api/flat-feed api/client {:type "user" :name "joana"})

  )

(def job
  (api/create-activity
    {:actor      "WorksHub"
     :verb       "job"
     :object     (api/collection-reference {:collection "jobs" :id "remoteok"})
     :forward-to (api/-feeds-ids ["tag:clojurescript"])
     :fields     {:job/super-job true}}))

(def cljs-feed (api/flat-feed api/client {:type "tag" :name "clojurescript"}))

(api/get-activities (api/flat-feed api/client {:type "tag" :name "clojurescript"}))

(comment
  (api/add-activity pitch-feed job)

  (def article
    (api/create-activity
      {:actor  "david nolen"
       :verb   "article"
       :object "articles:345"
       :fields {}}))

  (api/add-activity clojure-feed article)

  (api/newest-enriched-activity pitch-feed)

  (api/follow-all joana pitch-feed)

  (api/follow-all joana clojure-feed)

  (api/follow-all joana functional-feed)

  (api/get-activities clojure-feed)

  (first (api/get-activities functional-feed))
  (:activity/extra (api/get-newest-enriched-activity functional-feed))

  (api/clear-feed pitch-feed)

  (api/clear-feed clojure-feed)

  (api/clear-feed joana)

  (api/get-activities joana)

  (api/get-activities pitch-feed)

  (api/get-activities scala-feed)

  (api/get-activities functional-feed)

  (api/get-activities clojure-feed))
