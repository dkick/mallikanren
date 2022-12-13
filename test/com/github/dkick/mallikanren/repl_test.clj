(ns com.github.dkick.mallikanren.repl-test
  (:require  [clojure.core.logic :as l]
             [clojure.test :as t]
             [com.github.dkick.mallikanren.core :as sut]))

(comment
  (let [q nil]
    (l/run 3 [q]
      (l/== q 13)
      (sut/col!? :int q)))
  (let [q nil]
    (l/run 3 [q]
      (l/== q "13")
      (sut/col!? :int q)))
  :comment)
