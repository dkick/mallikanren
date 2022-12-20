(ns com.github.dkick.mallikanren.flat.spock
  (:refer-clojure :rename {name clj-name})
  (:require
   [clojure.core.logic :as l]
   [encaje.core :refer [fx]]
   [malli.core :as m]
   [malli.generator :as mg]))

;;; Laregly based on malli.json-schema; sometimes copied without
;;; really understanding, sometimes copied with maybe understanding,
;;; sometimes copied with hopefully understanding. It is up to you to
;;; discover which happened, when, and where <laughter type="evil"/>

(defprotocol SpockSchema
  (-accept [this children options]
    "transforms schema to Spock Schema"))

(defn col!?
  ([?x x]
   (l/pred x #(m/validate ?x %)))
  ([?x x x']
   (l/conde
    [(col!? ?x x)
     (l/== x' x)]
    [(l/nilo x)
     (l/== x' (mg/generate ?x))])))

(defmulti accept (fn [name _schema _children _options] name)
  :default ::default)

(defmethod accept ::default [name schema children options]
  (do (fx println
        {:name name
         :schema schema
         :children children
         :options options})
      #_`(col!? ~@(rest schema) ~(first schema))
      `(col!? ~schema)))

(defmethod accept ::m/val [name schema children options]
  (do (fx println
        {:name name
         :schema schema
         :children children
         :options options})
      (first children)))

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

(defn transform
  ([?schema]
   (transform ?schema nil))
  ([?schema options]
   (let [definitions (atom {})
         options (fx merge options
                     {::m/walk-entry-vals true
                      ::definitions definitions
                      ::transfrom -transform})]
     (cond-> (-transform ?schema options)
       (seq @definitions) (assoc :definitions @definitions)))))
