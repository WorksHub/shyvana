(ns shyvana.feed
  (:require [shyvana.activity :as activity]
            [shyvana.convert :as convert]))

(defn flat-feed
  "Creates reference to flat feed. Flat feed is the most basic type of feed.
  You don't have to 'declare' feed before using it. Once you reference feed
  it becomes entity in your system. If you don't want to have this feed anymore
  you can remove its' activities. You don't 'remove' feed."
  [client {:keys [type name]}]
  (.flatFeed client type name))

(defn post-activity
  "Create activity object and post it to given feed"
  [feed activity]
  (.getID (activity/add-activity feed (activity/create-activity activity))))

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
