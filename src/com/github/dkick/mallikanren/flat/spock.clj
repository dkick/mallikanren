(ns com.github.dkick.mallikanren.flat.spock
  (:refer-clojure :rename {name clj-name})
  (:require
   [clojure.core.logic :as l]
   [clojure.set :as set]
   [malli.core :as m]
   [malli.generator :as mg]))

(defn -schema? [x]
  (try
    (->> x m/schema m/schema?)
    (catch Exception e
      (case (->> e ex-data :type)
        ::m/invalid-schema false
        (throw e)))))

(defn val!?
  ([?x x]
   (l/conde
    [(l/pred ?x fn?)
     (l/pred x ?x)]
    [(l/pred ?x -schema?)
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

(defn -subset!? [args ks]
  (if (set/subset? args ks)
    l/succeed
    l/fail))

;; ToDo: Add an option for handling this schema for handling an
;; individual map vs a sequence of maps (representing a DB)
(defmethod accept :map [_name schema children options]
  (let [ks      (->> children (map (fn [[k _ _]] k)))
        schemas (->> children (map (fn [[_ _ v]] v)))
        #_#_frgn-ks (->> children -->frgn-ks)
        ->lvars (fn [] (->> ks (map #(->> % symbol l/lvar))))
        ->alist (fn [lvars] (->> (map vector ks lvars) (into [])))]
    #_(println frgn-ks)
    (if (:seq options)
      ;; ToDo: Is the seq xs a valid colection of maps?
      (fn seq-schema!?
        ([_xs] schema)
        ([_m _xs] schema))
      ;; Is the alist [m] or [_ m'] a valid map?
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
            (-subset!? (->> m keys set) (->> ks set))
            (l/== m' (->alist lvars))
            (l/and* (map -apply* schemas mvals lvars)))))))))

(defmethod accept :or [_name _schema children _options]
  (fn or-schema!?
    ([m] (l/or* (map #(% m) children)))
    ([m m'] (l/or* (map #(% m m') children)))))

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
     (-transform ?schema options)
     ;; The code from json-schema (below) does not work for us in this
     ;; case, as we're not returning some kind of a map into which we
     ;; can assoc extra :definitions. Given that fact, is it even
     ;; worth keeping definitions? Even in the json-schema case, I
     ;; couldn't find any use of the :definitions option. We'll keep
     ;; it around for now, just to look at; to ponder ...
     #_(cond-> (-transform ?schema options)
         (seq @definitions) (assoc :definitions @definitions)))))
