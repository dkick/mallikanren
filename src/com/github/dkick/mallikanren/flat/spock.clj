(ns com.github.dkick.mallikanren.flat.spock
  (:refer-clojure :rename {name clj-name})
  (:require
   [clojure.core.logic :as l]
   [encaje.core :refer [fx]]
   [malli.core :as m]
   [malli.generator :as mg]))

(defn col!?
  ([?x x]
   (l/pred x #(m/validate ?x %)))
  ([?x x x']
   (l/conde
    [(col!? ?x x)
     (l/== x' x)]
    [(l/nilo x)
     (l/== x' (mg/generate ?x))])))

;;; Laregly based on malli.json-schema; sometimes copied without
;;; really understanding, sometimes copied with maybe understanding,
;;; sometimes copied with hopefully understanding. It is up to you to
;;; discover which happened, when, and where <laughter type="evil"/>

(defprotocol SpockSchema
  (-accept [this children options]
    "transforms schema to Spock Schema"))

(defmulti accept (fn [name _schema _children _options] name)
  :default ::default)

(defmethod accept ::default [_name schema _children _options]
  (fn col-schema!?
    ([x] (col!? schema x))
    ([x x'] (col!? schema x x'))))

(defmethod accept ::m/val [_name _schema children _options]
  (first children))

(defmethod accept :map [_name _schema children _options]
  (let [kvs (->> children (map (fn [[k _p ?schema]] [k ?schema])))
        ks (->> kvs (map (fn [[k _?schema]] k)))
        ?schemas (->> kvs (map (fn [[_k ?schema]] ?schema)))]
    (fn row-schema!?
      ([m]
       (let [x-lvars (map #(->> % symbol l/lvar) ks)]
         (l/all
          (l/== m (->> (map vector ks x-lvars) (into [])))
          (l/and* (map (fn [x-lvar ?schema] (?schema x-lvar))
                       x-lvars ?schemas)))))
      ([m m'] (vector m m')))))

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
