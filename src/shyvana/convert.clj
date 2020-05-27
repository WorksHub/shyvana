(ns shyvana.convert
  (:require [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [com.google.common.collect Lists Maps]))

;; TODO: rethink using clojure.walk/keywordize-keys
;; Problem with keywordize-keys is that it doesn't care
;; how the original data was looking when it was uploaded to DB.
;; It just takes all keys in all maps and transforms them to keywords, so…
;; I'd rather keep current implementation since it attemptes to recreate
;; the data in the form it was uploaded to DB
(defn keyword-or-value
  "Takes value, possibly string, and if the first char is :
  transforms string into keyword. Otherwise returns value.
  Used to recreate keywords saved in DB as strings."
  [value]
  (if (and (string? value) (str/starts-with? value ":"))
    (keyword (subs value 1))
    value))

(defprotocol ConvertibleToEDN
  (->edn [o]))

(extend-protocol ConvertibleToEDN
  java.util.Map
  (->edn [o] (let [entries (.entrySet o)]
               (reduce (fn [m [^String k v]]
                         (assoc m (keyword-or-value k) (->edn v)))
                       {} entries)))

  java.util.List
  (->edn [o] (vec (map ->edn o)))

  java.lang.String
  (->edn [o] (keyword-or-value o))

  java.lang.Object
  (->edn [o] o)

  nil
  (->edn [_] nil))

(defn java->edn
  [m]
  (->edn m))

(defn map->java [map]
  (let [j-map (Maps/newHashMap)]

    (doseq [[key val] map]
      (.put j-map (str key) val))
    j-map))

(defn real-collection? [datum]
  (and
    (coll? datum)
    (not= (type datum) clojure.lang.MapEntry)))

(defn edn->java [data]
  (walk/postwalk
    (fn [datum]
      (cond
        (keyword? datum)         (str datum)
        (map? datum)             (map->java datum)
        (real-collection? datum) (Lists/newArrayList datum)
        :else                    datum))
    data))

(defn activity->edn [activity]
  {:id         (.getID activity)
   :verb       (.getVerb activity)
   :date       (.getTime activity)
   :extra      (java->edn (.getExtra activity))
   :score      (.getScore activity)
   :to         (mapv #(.toString %) (.getTo activity))
   :actor      (.toString (.getActor activity))
   :foreign-id (.getForeignID activity)
   :object-id  (.getObject activity)})

(defn enriched-activity->edn [activity]
  {:id         (.getID activity)
   :verb       (.getVerb activity)
   :date       (.getTime activity)
   :extra      (java->edn (.getExtra activity))
   :score      (.getScore activity)
   :to         (mapv #(.toString %) (.getTo activity))
   :actor      {:id   (.getID (.getActor activity))
                :data (java->edn (.getData (.getActor activity)))}
   :foreign-id (.getForeignID activity)
   :object     (walk/keywordize-keys
                 (java->edn (.getData (.getObject activity))))})
