(ns shyvana.feed
  (:require [shyvana.convert :as convert]))

(defn get-activities [feed]
  (map convert/activity->edn
       (.get (.getActivities feed))))

(defn get-newest-activity [feed]
  (first (get-activities feed)))

(defn get-enriched-activities [feed]
  (map convert/enriched-activity->edn
       (.get (.getEnrichedActivities feed))))

(defn get-newest-enriched-activity [feed]
  (first (get-enriched-activities feed)))

(defn remove-activity-by-id [feed id]
  (.join (.removeActivityByID feed id)))

(defn remove-activities-by-foreign-id
  "Removes ALL activities with given foreign id. Activities can share foreign ids"
  [feed foreign-id]
  (.join (.removeActivityByForeignID feed foreign-id)))

(defn clear-feed
  "Clear all activites published on feed. If some activity from this feed was
  passed on to other feed, or shown on other feed through following relation
  it will also be cleared from other feed."
  [feed]
  (doseq [id (map :activity/id (get-activities feed))]
    (remove-activity-by-id feed id)))
