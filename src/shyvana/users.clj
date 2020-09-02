(ns shyvana.users
  (:require [shyvana.collections :as collections])
  (:import [io.getstream.core.models Data]))


(defn user-entry [data]
  (-> (Data.)
      (collections/set-collection-fields data)))

(defn get-or-create-user [client ^String id data]
  (.join (.getOrCreate (.user client id)
                       (user-entry data))))

(defn create-user [client ^String id data]
  (.join (.create (.user client id)
                  (user-entry data))))

(defn update-user [client ^String id data]
  (.join (.update (.user client id)
                  (user-entry data))))

(defn get-user [client ^String id]
  (-> (.user client id)
      (.get)
      (.join)))
