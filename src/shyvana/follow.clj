(ns shyvana.follow
  (:import [io.getstream.core KeepHistory]
           [io.getstream.core.models FeedID]
           [io.getstream.core.options Limit Offset Filter]))

(defn follow
  "Follow feed for all new activities"
  [feed follower]
  (.join (.follow feed follower 0)))

(defn follow-all
  "Follow feed and copy all activites already existing on followed feed into target"
  [feed follower]
  (.join (.follow feed follower)))

(defn unfollow
  "Unfollow feed but keep activities from followed feed on followers' feed"
  [feed follower]
  (.join (.unfollow feed follower KeepHistory/YES)))

(defn unfollow-all
  "Unfollow feed and clear old activities from followers' feed"
  [feed follower]
  (.join (.unfollow feed follower)))


(defn- get-followed*
  "Interface for simple Java method call"
  ([a]
   (.getFollowed a))
  ([a b]
   (.getFollowed a b))
  ([a b c]
   (.getFollowed a b c))
  ([a b c d]
   (.getFollowed a b c d)))

(defn- filter-followed [limit offset]
  (filter some?
          [(when limit (Limit. limit))
           (when offset (Offset. offset))]))

(defn get-followed
  "Checks whether feeds identified by their IDs are followed by one, particular feed
  (get-followed flat-feed-ref [(FeedID. \"tag:clojure\") (FeedID. \"tag:scala\")])"
  ([feed ids]
   (get-followed feed ids {}))

  ([feed ids {:keys [limit offset]}]
   (let [ids (map #(FeedID. %) ids)]
     (map (fn [relation]
            {:target (.getTarget relation)
             :source (.getSource relation)})
          (.get (apply get-followed*
                       (concat [feed] (filter-followed limit offset) [ids])))))))
