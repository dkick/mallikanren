(ns com.github.dkick.mallikanren.util.logic
  (:require
   [clojure.core.logic :as l]))

;;; This is just a place to put things that make clj-kondo confusedly
;;; unhappy.

(defn member1!? [x l]
  (l/member1o x l))
