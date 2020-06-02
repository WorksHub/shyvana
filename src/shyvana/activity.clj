(ns shyvana.activity
  (:require [clojure.spec.alpha :as s]
            [shyvana.convert :as convert])
  (:import [com.google.common.collect Lists]
           [io.getstream.client Feed]
           [io.getstream.core.models Activity FeedID]
           [io.getstream.core.utils Enrichment]))


(defn- feed->feed-id [^Feed feed]
  (.getID feed))

(defn- feeds->feeds-ids [feeds]
  (Lists/newArrayList (map feed->feed-id feeds)))

(defn- string->feed-id [^String s]
  (FeedID. s))

(defn- strings->feeds-ids [feeds]
  (Lists/newArrayList (map string->feed-id feeds)))


(defn- add-activity-fields
  "Reduce over fields hashmap and convert content of hashmap into
  Activity extra fields"
  [activity fields]
  (reduce-kv
    (fn [acc k v]
      (.extraField acc (str k) (convert/map->java v)))
    activity
    fields))

(defn- collection-reference
  "Create reference to object in collection, to bind object to activity"
  [{:keys [collection id]}]
  (Enrichment/createCollectionReference collection id))

(defn create [{:keys [actor verb object fields forward-to foreign-id date]
               :or   {fields     {}
                      foreign-id ""
                      date       (java.util.Date.)}}]
  (let [activity    (doto (Activity/builder)
                      (.actor actor)
                      (.verb verb)
                      (.time date)
                      (.object (collection-reference object))
                      (.foreignID foreign-id))
        with-fields (add-activity-fields activity fields)
        forwarded   (if forward-to
                      (.to with-fields (strings->feeds-ids forward-to))
                      with-fields)]
    (.build forwarded)))

(s/def :activity/actor string?)
(s/def :activity/verb string?)
;; :activity/object is a string that references Object from Collection in Stream DB
;; It has the form of: "SO:collection-name:object-id"
(s/def :activity/object string?)
;; :activity/fields is object containing extra fields to be saved with Activity.
;; If you want Activity to be linked with some Object, ie. Job Posting linked with Job
;; you should use Collections and Objects. You may use extra fields to save
;; additional data about the Activity itself.
(s/def :activity/fields map?)
(s/def :activity/forward-to (s/coll-of string?))
(s/def :activity/foreign-id string?)
(s/def :activity/date inst?)

(s/def ::activity
  (s/keys :req-un [:activity/actor
                   :activity/verb
                   :activity/object]
          :opt-un [:activity/fields
                   :activity/foreign-id
                   :activity/forward-to]))

(s/def ::java-activity (partial instance? Activity))
(s/def ::java-feed (partial instance? Feed))

(s/fdef create
  :args (s/cat :activity ::activity)
  :ret ::java-activity)

(defn post [activity ^Feed feed]
  (.join (.addActivity feed (create activity))))

(s/fdef post
  :args (s/cat :activity ::java-activity
               :feed ::java-feed)
  :ret ::java-activity)
