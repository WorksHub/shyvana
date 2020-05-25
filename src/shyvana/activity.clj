(ns shyvana.activity
  (:require [clojure.spec.alpha :as s]
            [shyvana.convert :as convert])
  (:import [io.getstream.core.models Activity]
           [io.getstream.core.utils Enrichment]))

(defn- add-activity-fields [activity fields]
  (reduce-kv
    (fn [acc k v]
      (.extraField acc (str k) (convert/edn->java v)))
    activity
    fields))

(defn- collection-reference
  "Create reference to object in collection, to bind object to activity"
  [{:keys [collection id]}]
  (Enrichment/createCollectionReference collection id))

(defn create-activity [{:keys [actor verb object fields forward-to foreign-id date]
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
                      (.to with-fields forward-to)
                      with-fields)]
    (.build forwarded)))

(s/def :activity/actor string?)
(s/def :activity/verb #{"tweet" "post-job"})
(s/def :activity/object string?)
(s/def :activity/fields map?)

(s/def ::activity
  (s/keys :req [:activity/actor
                :activity/verb
                :activity/object]
          :opt [:activity/fields]))

(s/fdef create-activity
  :args (s/cat :activity ::activity)
  :ret Activity)

(defn add-activity [feed activity]
  (.join (.addActivity feed activity)))
