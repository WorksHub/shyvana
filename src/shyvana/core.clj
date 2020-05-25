(ns shyvana.core
  "Shyvana!
  https://vignette.wikia.nocookie.net/leagueoflegends/images/5/51/Shyvana_OriginalCentered.jpg/revision/latest?cb=20180414203547"
  (:require [shyvana.activity :as activity]
            [shyvana.convert :as convert])
  (:import [com.google.common.collect Lists]
           [io.getstream.client Client]
           [io.getstream.core.models FeedID]))

(defn build-client [key secret]
  (.build (Client/builder key secret)))

(defn flat-feed [client {:keys [type name]}]
  (.flatFeed client type name))

(defn remove-activity-by-id [feed id]
  (.join (.removeActivityByID feed id)))

(defn remove-activities-by-foreign-id
  "Removes ALL activities with given foreign id. Activities can share foreign ids"
  [feed foreign-id]
  (.join (.removeActivityByForeignID feed foreign-id)))

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

(defn clear-feed
  "Clear all activites published on feed. If some activity from this feed was
  passed on to other feed, or shown on other feed through following relation
  it will also be cleared from other feed."
  [feed]
  (doseq [id (map :activity/id (get-activities feed))]
    (remove-activity-by-id feed id)))

(defn feed-id [feed]
  (FeedID. (.toString (.getID feed))))

(defn feeds-ids [feeds]
  (Lists/newArrayList (map feed-id feeds)))

(defn -feeds-ids [feeds]
  (Lists/newArrayList (map #(FeedID. %) feeds)))
