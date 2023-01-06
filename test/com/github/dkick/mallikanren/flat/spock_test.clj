(ns com.github.dkick.mallikanren.flat.spock-test
  (:require [com.github.dkick.mallikanren.flat.spock :as sut]
            [clojure.core.logic :as l]
            [clojure.test :as t]
            [malli.core :as m]))

(t/deftest basic-col!?-test
  (t/testing "int is int"
    (let [q nil
          z (l/run 3 [q]
              (l/== q 13)
              (sut/val!? (m/schema :int) q))]
      (t/is (= z [13]))))
  (t/testing "string is not int"
    (let [q nil
          z (l/run 3 [q]
              (l/== q "13")
              (sut/val!? (m/schema :int) q))]
      (t/is (= z ())))))
