(ns com.github.dkick.mallikanren.core
  (:require
   [clojure.core.logic :as l]
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
