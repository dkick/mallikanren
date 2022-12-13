(ns com.github.dkick.mallikanren.core
  (:require
   [clojure.core.logic :as l]
   [malli.core :as m]
   [malli.generator :as mg]))

;;; A note on naming conventions: The Malli source code uses the
;;; convention of a leading '?' for schema names. For example ...
;;;
;;; (defn -reference? [?schema]
;;;   (or (string? ?schema) (qualified-keyword? ?schema)))
;;;
;;; ... and so I am keeping with this convention.

(defn col!?
  ([?x x]
   (l/pred x #(m/validate ?x %)))
  ([?x x x']
   (l/conde
    [(col!? ?x x)
     (l/== x' x)]
    [(l/nilo x)
     (l/== x' (mg/generate ?x))])))
