(ns shyvana.feed
  (:require [shyvana.activity :as activity]
            [shyvana.convert :as convert])
  (:import [io.getstream.core.options Limit Offset]))

(defn flat-feed
  "Creates reference to flat feed. Flat feed is the most basic type of feed.
  You don't have to declare feed before using it. Once you reference feed
  it becomes entity in your system. If you don't want to have this feed anymore
  you can remove its' activities. You don't remove feed. You just stop using
  refrence to it."
  [client {:keys [type name]}]
  (.flatFeed client type name))

(defn get-activities
  ([feed]
   (get-activities feed 25 0))

  ([feed limit]
   (get-activities feed limit 0))

  ([feed limit offset]
   (map convert/activity->map
        (.get (.getActivities feed (Limit. limit) (Offset. offset))))))

(defn get-newest-activity [feed]
  (first (get-activities feed)))

(defn get-enriched-activities
  ([feed]
   (get-enriched-activities feed 25 0))

  ([feed limit]
   (get-enriched-activities feed limit 0))

  ([feed limit offset]
   (map convert/enriched-activity->map
        (.get (.getEnrichedActivities feed (Limit. limit) (Offset. offset))))))

(defn get-newest-enriched-activity [feed]
  (first (get-enriched-activities feed)))

(defn remove-activity-by-id [feed id]
  (.join (.removeActivityByID feed id)))

(defn remove-activities-by-foreign-id
  "Removes ALL activities with given foreign id. Activities can share foreign ids"
  [feed foreign-id]
  (.join (.removeActivityByForeignID feed foreign-id)))

(defn clear
  "Clear all activites published on feed. If some activity from this feed was
  passed on to other feed, or shown on other feed through following relation
  it will also be cleared from other feed."
  [feed]
  (doseq [id (map :id (get-activities feed))]
    (remove-activity-by-id feed id)))
