(ns shyvana.collections
  (:require [clojure.walk :as walk]
            [shyvana.convert :as convert])
  (:import [io.getstream.core.models CollectionData]))

(defn add-to-collection [client ^String collection-name data]
  (.join (.add (.collections client) collection-name data)))

(defn update-in-collection [client ^String collection-name data]
  (.join (.update (.collections client) collection-name data)))

(defn- set-collection-fields [collection fields]
  (reduce-kv
    (fn [acc k v]
      (.set acc (str k) (convert/map->java v)))
    collection
    fields))

(defn- collection-entry [id data]
  (-> (CollectionData. id)
      (set-collection-fields data)))

(defn get-by-id [client ^String collection-name ^String id]
  (let [entity (.join (.get (.collections client) collection-name id))]
    (-> (.getData entity)
        convert/java->map
        (assoc :id (.getID entity))
        walk/keywordize-keys)))

(defn add-by-id [client ^String collection-name ^String id data]
  (add-to-collection client collection-name (collection-entry id data)))

(defn remove-by-id [client ^String collection-name ^String id]
  (.join (.delete (.collections client) collection-name id)))

(defn upsert-to-collection [client collection-name data]
  (.join (.upsert (.collections client) collection-name data)))

(defn upsert-by-id [client ^String collection-name ^String id data]
  (upsert-to-collection client collection-name [(collection-entry id data)]))
