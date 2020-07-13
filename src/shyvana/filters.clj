(ns shyvana.filters
  (:import [io.getstream.core.options Limit Offset Filter]))


(defn- filter-ids [filter]
  (reduce-kv
    (fn [filter key val]
      (case key
        :id>  (.idGreaterThan filter val)
        :id<  (.idLessThan filter val)
        :id>= (.idGreaterThanEqual filter val)
        :id<= (.idLessThanEqual filter val)
        filter))

    (Filter.) filter))

(defn create-filter [{:keys [limit offset filter]}]
  (cond
    (and limit filter)
    [(Limit. limit) (filter-ids filter)]

    (and limit offset)
    [(Limit. limit) (Offset. offset)]

    limit
    [(Limit. limit)]

    offset
    [(Offset. offset)]))

