(ns shyvana.collections
  (:require [clojure.walk :as walk]
            [shyvana.convert :as convert])
  (:import [io.getstream.core.models CollectionData]))

(defn add-to-collection [client collection data]
  (.join (.add (.collections client) collection data)))

(defn update-in-collection [client collection data]
  (.join (.update (.collections client) collection data)))

(defn- set-collection-fields [collection fields]
  (reduce-kv
    (fn [acc k v]
      (.set acc (str k) (convert/edn->java v)))
    collection
    fields))

(defn- collection-entry [id data]
  (-> (CollectionData. id)
      (set-collection-fields data)))

(defn get-by-id [client collection id]
  (let [entity (.join (.get (.collections client) collection id))]
    (-> (.getData entity)
        convert/java->edn
        (assoc :id (.getID entity))
        walk/keywordize-keys)))

(defn add-by-id [client collection id data]
  (add-to-collection client collection (collection-entry id data)))

(defn remove-by-id [client collection id]
  (.join (.delete (.collections client) collection id)))

(defn upsert-to-collection [client collection data]
  (.join (.upsert (.collections client) collection data)))

(defn upsert-by-id [client collection id data]
  (upsert-to-collection client collection [(collection-entry id data)]))
