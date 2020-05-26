(ns shyvana.feed
  (:require [shyvana.activity :as activity]
            [shyvana.convert :as convert])
  (:import [com.google.common.collect Lists]
           [io.getstream.core.models FeedID]))

(defn flat-feed [client {:keys [type name]}]
  (.flatFeed client type name))

(defn post-activity
  "Create activity object and add it to given feed"
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

(defn feed->feed-id [feed]
  (FeedID. (.toString (.getID feed))))

(defn feeds->feeds-ids [feeds]
  (Lists/newArrayList (map feed->feed-id feeds)))

(defn string->feed-id [s]
  (FeedID. s))

(defn strings->feeds-ids [feeds]
  (Lists/newArrayList (map string->feed-id feeds)))
