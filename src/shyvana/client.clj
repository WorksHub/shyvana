(ns shyvana.client
  (:import [io.getstream.client Client]))

(defn connect [^String key ^String secret]
  (.build (Client/builder key secret)))
