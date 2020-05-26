(ns shyvana.client
  (:import [io.getstream.client Client]))

(defn build-client [key secret]
  (.build (Client/builder key secret)))
