(ns shyvana.follow
  "Examplary ns for discovering stream possibilites"
  (:import [io.getstream.core KeepHistory]))

(defn follow
  "Follow feed for all new activities"
  [who whom]
  (.join (.follow who whom 0)))

(defn follow-all
  "Follow feed and copy all activites already existing on followed feed into target"
  [who whom]
  (.join (.follow who whom)))

(defn unfollow
  "Unfollow feed but keep activities from followed feed on followers' feed"
  [who whom]
  (.join (.unfollow who whom KeepHistory/YES)))

(defn unfollow-all
  "Unfollow feed and clear old activities from followers' feed"
  [who whom]
  (.join (.unfollow who whom)))
