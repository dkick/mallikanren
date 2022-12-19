(ns com.github.dkick.mallikanren.flat.spock
  (:refer-clojure :rename {name clj-name})
  (:require
   [malli.core :as m]))

;;; Laregly based on malli.json-schema; sometimes copied without
;;; really understanding, sometimes copied with maybe understanding,
;;; sometimes copied with hopefully understanding. It is up to you to
;;; discover which happened, when, and where <laughter type="evil"/>

(defprotocol SpockSchema
  (-accept [this children options]
    "transforms schema to Spock Schema"))

(defmulti accept (fn [name _schema _children _options] name)
  :default ::default)

(defmethod accept ::default [name schema children options]
  (doto schema
    (println {:name name
              :schema schema
              :children children
              :options options})))

(defn -spock-schema-walker [schema _path children options]
  (let [p (merge (m/type-properties schema) (m/properties schema))]
    (or (get p :spock-schema)
        (if (satisfies? SpockSchema schema)
          (-accept schema children options)
          (accept (m/type schema) schema children options)))))

(defn -transform
  ([?schema] (-transform ?schema nil))
  ([?schema options]
   (m/walk ?schema -spock-schema-walker options)))
