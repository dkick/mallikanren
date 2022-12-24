(ns com.github.dkick.mallikanren.flat.spock
  (:refer-clojure :rename {name clj-name})
  (:require
   [clojure.core.logic :as l]
   [malli.core :as m]
   [malli.generator :as mg]))

;;; Laregly based on malli.json-schema; sometimes copied without
;;; really understanding, sometimes copied with maybe understanding,
;;; sometimes copied with hopefully understanding. It is up to you to
;;; discover which happened, when, and where <laughter type="evil"/>

(defprotocol SpockSchema
  (-accept [this children options]
    "transforms schema to Spock Schema"))

(defmulti accept (fn [name _schema _children _options] name)
  :default ::default)

(defn cell!?
  ([?x x]
   (l/conde
    [(l/pred ?x fn?)
     (l/pred x ?x)]
    [(l/pred ?x m/schema?)
     (l/pred x #(m/validate ?x %))]))
  ([?x x x']
   (l/conde
    [(cell!? ?x x)
     (l/== x' x)]
    [(l/nilo x)
     (l/== x' (mg/generate ?x))])))

(defn var!? [x!? x]
  (l/conde
   [(l/lvaro x)]
   [(x!? x)]))

(defmethod accept ::default [_name schema _children _options]
  (fn cell-schema!?
    ([x] (cell!? schema x))
    ([x x'] (cell!? schema x x'))))

(defmethod accept ::m/val [_name _schema children _options]
  (first children))

(defn -apply* [& args]
  (apply (first args) (rest args)))

(defmethod accept :map [_name _schema children _options]
  (let [kvs (->> children (map (fn [[k _ v]] [k v])))
        ks      (->> kvs (map (fn [[k _]] k)))
        schemas (->> kvs (map (fn [[_ v]] v)))
        ->lvars (fn [] (->> ks (map #(->> % symbol l/lvar))))
        ->row   (fn [lvars] (->> (map vector ks lvars) (into [])))]
    (fn row-schema!?
      ([m]
       (let [lvars (->lvars)]
         (l/all
          (l/== m (->row lvars))
          (l/and* (map var!? schemas lvars)))))
      ([m m']
       (let [m-vals (->> ks (map #(% m)))
             lvars (->lvars)]
         (l/all
          (l/== m' (->row lvars))
          (l/and* (map -apply* schemas m-vals lvars))))))))

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
         options (merge options {::m/walk-entry-vals true
                                 ::definitions definitions
                                 ::transfrom -transform})]
     (cond-> (-transform ?schema options)
       (seq @definitions) (assoc :definitions @definitions)))))
