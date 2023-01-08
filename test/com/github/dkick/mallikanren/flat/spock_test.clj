(ns com.github.dkick.mallikanren.flat.spock-test
  (:require
   [clojure.core.logic :as l]
   [clojure.test :as t]
   [com.github.dkick.mallikanren.flat.spock :as spock]))

(t/deftest basic-col!?-test
  (t/testing "int is int"
    (let [q nil
          z (l/run 3 [q]
              (l/== q 13)
              (spock/val!? :int q))]
      (t/is (= z [13]))))
  (t/testing "string is not int"
    (let [q nil
          z (l/run 3 [q]
              (l/== q "13")
              (spock/val!? :int q))]
      (t/is (= z ())))))

(comment
  (let [q nil]
    (l/run 10 [q]
      (spock/-subset!? #{:x} #{:x :y})
      (l/== q true)))
  :comment)
