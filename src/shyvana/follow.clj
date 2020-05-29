(ns shyvana.follow
  "Examplary ns for discovering stream possibilites"
  (:import [io.getstream.core KeepHistory]))

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
