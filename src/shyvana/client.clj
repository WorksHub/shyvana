(ns shyvana.client
  (:import [io.getstream.client Client]))

(defn connect [key secret]
  (.build (Client/builder key secret)))
