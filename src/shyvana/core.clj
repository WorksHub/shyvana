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

(defn post-activity
  "Create activity object and add it to given feed"
  [feed activity]
  (.getID (activity/add-activity feed (activity/create-activity activity))))

(defn feed->feed-id [feed]
  (FeedID. (.toString (.getID feed))))

(defn feeds->feeds-ids [feeds]
  (Lists/newArrayList (map feed->feed-id feeds)))

(defn string->feed-id [s]
  (FeedID. s))

(defn strings->feeds-ids [feeds]
  (Lists/newArrayList (map string->feed-id feeds)))
