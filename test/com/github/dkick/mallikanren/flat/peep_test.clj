(ns com.github.dkick.mallikanren.flat.peep-test
  (:require
   [clojure.core.logic :as l]
   [clojure.test :as t]
   [com.github.dkick.mallikanren.flat.spock :as spock]
   [com.github.dkick.mallikanren.util.logic :as lu]
   [encaje.core :refer [-- fx]]
   [malli.util :as mu]))

(def Name
  [:map
   [:first :string]
   [:middle [:maybe :string]]
   [:last :string]])

(def name!?
  (->> Name spock/transform))

(def Person
  [:map
   [:id :uuid]
   [:name Name]])

(def person!?
  (->> Person spock/transform))

;; Using properties for foreign keys makes it easy to annotate the
;; schema in a way that doesn't affect the Malli schema as a thing
;; in/of itself but can provide the necessary info for the
;; spock/transfrom ... but the referring type is redundant
(def Parents
  [:map
   [:child {:foreign-key [Person :id]} :uuid]
   [:father {:foreign-key [Person :id]} :uuid]
   [:mother {:foreign-key [Person :id]} :uuid]])

(def parents!?
  (->> Parents spock/transform))

(def Children
  [:map
   [:parent {:foreign-key [Person :id]} :uuid]
   [:child {:foreign-key [Person :id]} :uuid]])

(def children!?
  (->> Children spock/transform))

(def Table
  [:or Children Name Parents Person])

(def table!?
  (->> Table spock/transform))

(defn fml [f m l]
  [[:first f] [:middle m] [:last l]])

(t/deftest name!?-test
  (t/testing "lvar members"
    (let [q nil
          [x-name & x-rest]
          (-- (l/run 3 [q]
                (name!? q)
                (l/conde
                 [(lu/member1!? [:first "Damien"] q)]
                 [(lu/member1!? [:first 13] q)])
                (name!? q)))]
      (t/is (nil? x-rest))
      ;; ToDo: This would be a perfect place for core.match
      (let [[[k0 v0] [k1 v1] [k2 v2]] x-name]
        (t/is (= k0 :first))
        (t/is (= v0 "Damien"))
        (t/is (= k1 :middle))
        (t/is (= (name v1) "_0"))
        (t/is (= k2 :last))
        (t/is (= (name v2) "_1")))))
  (t/testing "literal members"
    (let [q nil
          [x & y]
          (-- (l/run 100 [q]
                (fx name!?
                    {:first "Damien", :middle "Robert", :last "Kick"} q)
                (name!? q)))]
      (t/is (nil? y))
      (t/is (= x (fml "Damien" "Robert" "Kick")))))
  (t/testing "literal nonmembers"
    (let [q nil
          [& x]
          (-- (l/run 1 [q]
                (let [q' {:nickname "D'amy"}]
                  (name!? q' q))
                (name!? q)))]
      (t/is (nil? x))))
  (t/testing "validate invalidate"
    (let [q nil
          [x y & z]
          (-- (l/run 3 [q]
                (l/conde
                 [(l/== q (fml "Damien" "Robert" "Kick"))]
                 [(l/== q (fml "Damien" nil "Kick"))]
                 [(l/== q [[:first "Robert"] [:last "Downey"]])]
                 [(l/== q [[:nickname "D'amy"]])])
                (name!? q)))]
      (t/is (nil? z))
      (t/is (= x (fml "Damien" "Robert" "Kick")))
      (t/is (= y (fml "Damien" nil "Kick"))))))

(comment
  (let [q nil]
    (l/run 10 [q]
      (let [q' {:last "Kick"}]
        (name!? q' q))))
  ;; => ([[:first "d882EJ7a"]
  ;;      [:middle "dgM5h02XCLIxe94Sc3NEfc8M4rv5aK"]
  ;;      [:last "Kick"]]
  ;;     [[:first "d882EJ7a"] [:middle nil] [:last "Kick"]])
  (mu/get Person :id)
  ;; => :uuid
  (let [q nil]
    (l/run 10 [q]
      (person!? nil q)))
  ;; => ([[:id #uuid "d8834f42-3a97-4335-8526-1d68b3c79d10"]
  ;;      [:name
  ;;       [[:first "1Sl70R9af3BD"] [:middle ""] [:last "7Z6EtmWV908e"]]]]
  ;;     [[:id #uuid "d8834f42-3a97-4335-8526-1d68b3c79d10"]
  ;;      [:name
  ;;       [[:first "1Sl70R9af3BD"]
  ;;        [:middle nil]
  ;;        [:last "v9k6M38V8L89vW71R0TGl081o"]]]])
  (let [q nil]
    (l/run 10 [q]
      (l/or* (->> [name!? person!? parents!? children!?]
                  (map #(% q))))))
  ;; => ([[:first _0] [:middle _1] [:last _2]]
  ;;     [[:id _0] [:name _1]]
  ;;     [[:parent _0] [:child _1]]
  ;;     [[:child _0] [:father _1] [:mother _2]]
  ;;     [[:id _0] [:name [[:first _1] [:middle _2] [:last _3]]]])
  (let [q nil]
    (l/run 3 [q]
      (spock/val!? :uuid nil q)))
  ;; => (#uuid "0fe43875-fe62-4fb6-8ceb-13a40e1c4454")
  (let [q nil]
    (l/run 10 [q]
      (table!? {:child #uuid "0fe43875-fe62-4fb6-8ceb-13a40e1c4454"} q)))
  ;; => ([[:parent #uuid "5c065b19-728a-43f3-85ef-ce9c24d69c37"]
  ;;      [:child #uuid "0fe43875-fe62-4fb6-8ceb-13a40e1c4454"]]
  ;;     [[:child #uuid "0fe43875-fe62-4fb6-8ceb-13a40e1c4454"]
  ;;      [:father #uuid "db6dc11c-ba1c-4ec1-abcb-b69221fa4a26"]
  ;;      [:mother #uuid "ded1e1ca-b71a-4154-9a0e-aa93b34971d3"]])
  :comment)
