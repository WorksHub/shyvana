(ns shyvana.convert
  (:require [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [com.google.common.collect Lists Maps]
           [io.getstream.core.models Activity EnrichedActivity]))

(defprotocol ConvertibleToMap
  (->map [o]))

(extend-protocol ConvertibleToMap
  java.util.Map
  (->map [o] (let [entries (.entrySet o)]
               (reduce (fn [m [k v]]
                         (assoc m (keyword k) (->map v)))
                       {} entries)))

  java.util.List
  (->map [o] (vec (map ->map o)))

  java.lang.String
  (->map [o] o)

  java.lang.Object
  (->map [o] o)

  nil
  (->map [_] nil))

(defn java->map
  [m]
  (->map m))

(defn -map->java [map]
  (let [j-map (Maps/newHashMap)]

    (doseq [[key val] map]
      (.put j-map (name key) val))
    j-map))

(defn real-collection?
  "Clojure postwalk iterates over a map as collection of MapEntries. MapEntry
  is a collection, but we don't want to transform this collection into Java
  collection. For this reason, when we walk through data, we check for collection
  not being MapEntry, to avoid destroying maps."
  [datum]
  (and
    (coll? datum)
    (not= (type datum) clojure.lang.MapEntry)))

(defn map->java [^clojure.lang.PersistentArrayMap data]
  (walk/postwalk
    (fn [datum]
      (cond
        (keyword? datum)         (name datum)
        (map? datum)             (-map->java datum)
        (real-collection? datum) (Lists/newArrayList datum)
        :else                    datum))
    data))

(defn activity->map [^Activity activity]
  {:id         (.getID activity)
   :verb       (.getVerb activity)
   :date       (.getTime activity)
   :extra      (java->map (.getExtra activity))
   :score      (.getScore activity)
   :to         (mapv #(.toString %) (.getTo activity))
   :actor      (.toString (.getActor activity))
   :foreign-id (.getForeignID activity)
   :object-id  (.getObject activity)})

(defn enriched-activity->map [^EnrichedActivity activity]
  {:id         (.getID activity)
   :verb       (.getVerb activity)
   :date       (.getTime activity)
   :extra      (java->map (.getExtra activity))
   :score      (.getScore activity)
   :to         (mapv #(.toString %) (.getTo activity))
   :actor      {:id   (.getID (.getActor activity))
                :data (java->map (.getData (.getActor activity)))}
   :foreign-id (.getForeignID activity)
   :object     (-> (.getData (.getObject activity))
                   (java->map)
                   (walk/keywordize-keys)
                   (clojure.set/rename-keys {:_id :id}))})
