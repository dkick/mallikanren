(ns com.github.dkick.mallikanren.flat.spock
  (:refer-clojure :rename {name clj-name})
  (:require
   [clojure.core.logic :as l]
   [clojure.pprint :refer [pprint]]
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

(defn val!?
  ([?x x]
   (l/conde
    [(l/pred ?x fn?)
     (l/pred x ?x)]
    ;; ToDo: Add a way to try and convert to a schema; i.e. m/schema
    ;; but catch exceptions?
    [(l/pred ?x m/schema?)
     (l/pred x #(m/validate ?x %))]))
  ([?x x x']
   (l/conde
    [(val!? ?x x)
     (l/== x' x)]
    [(l/nilo x)
     (l/== x' (mg/generate ?x))])))

(defn var!? [x!? x]
  (l/conde
   [(l/lvaro x)]
   [(x!? x)]))

(defmethod accept ::default [_name schema _children _options]
  (fn val-schema!?
    ([x] (val!? schema x))
    ([x x'] (val!? schema x x'))))

(defmethod accept ::m/val [_name _schema children _options]
  (first children))

#_
(defn -->frgn-ks [children]
  (->> children
       (map (fn [[k p _]] [k p]))
       (filter (fn [[_ p]] (:foreign-key p)))
       (into {})))

(defn -apply* [& args]
  (apply (first args) (rest args)))

(defmethod accept :map [_name _schema children _options]
  (let [ks      (->> children (map (fn [[k _ _]] k)))
        schemas (->> children (map (fn [[_ _ v]] v)))
        #_#_frgn-ks (->> children -->frgn-ks)
        ->lvars (fn [] (->> ks (map #(->> % symbol l/lvar))))
        ->alist (fn [lvars] (->> (map vector ks lvars) (into [])))]
    #_(println frgn-ks)
    (fn map-schema!?
      ([m]
       (let [lvars (->lvars)]
         (l/all
          (l/== m (->alist lvars))
          (l/and* (map var!? schemas lvars)))))
      ([m m']
       (let [mvals (->> ks (map #(% m)))
             lvars (->lvars)]
         (l/all
          (l/== m' (->alist lvars))
          (l/and* (map -apply* schemas mvals lvars))))))))

(defmethod accept :or [name schema children options]
  (pprint {:name name, :schema schema, :children children
           :options options})
  schema)

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
