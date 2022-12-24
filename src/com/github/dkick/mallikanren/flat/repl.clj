(ns com.github.dkick.mallikanren.flat.repl
  (:require
   [clojure.core.logic :as l]
   [com.github.dkick.mallikanren.flat.spock :as spock]))

(def Name
  [:map
   [:first :string]
   [:middle [:maybe :string]]
   [:last :string]])

(def name!? (spock/transform Name))

(def Person
  [:map
   [:id :uuid]
   [:name Name]])

(def person!? (spock/transform Person))

(def Parents
  [:map
   [:child :uuid]
   [:father :uuid]
   [:mother :uuid]])

(def parents!? (spock/transform Parents))

(def Children
  [:map
   [:parent :uuid]
   [:child :uuid]])

(def children!? (spock/transform Children))

(comment
  (let [q nil]
    (l/run 3 [q]
      (name!? q)))
  ;; => ([[:first _0] [:middle _1] [:last _2]])
  (let [q nil]
    (l/run 3 [q]
      (l/conde
       [(l/== q [[:first "Damien"] [:middle "Robert"] [:last "Kick"]])]
       [(l/== q [[:first "Damien"] [:middle nil] [:last "Kick"]])]
       [(l/== q [[:nickname "D'amy"]])])
      (name!? q)))
  ;; => ([[:first "Damien"] [:middle "Robert"] [:last "Kick"]]
  ;;     [[:first "Damien"] [:middle nil] [:last "Kick"]])
  (let [name!? (spock/transform Name)
        q nil]
    (l/run 100 [q]
      (name!? {:first "Damien", :middle "Robert", :last "Kick"} q)
      (name!? q)))
  ;; => ([[:first "Damien"] [:middle "Robert"] [:last "Kick"]])
  (let [q nil]
    (l/run 10 [q]
      (person!? nil q)))
  ;; => ([[:id #uuid "61bf0770-a14b-476d-9635-14f1432c9462"]
  ;;      [:name
  ;;       [[:first "NMBZ43"] [:middle nil] [:last "4dgq2u67t0XzE97"]]]]
  ;;     [[:id #uuid "61bf0770-a14b-476d-9635-14f1432c9462"]
  ;;      [:name
  ;;       [[:first "NMBZ43"] [:middle nil] [:last "6PTKv0toA8WiFNboQ77"]]]])
  (let [q nil]
    (l/run 10 [q]
      (l/or* (->> [name!? person!? parents!? children!?]
                  (map #(% q))))))
  ;; => ([[:first _0] [:middle _1] [:last _2]]
  ;;     [[:id _0] [:name _1]]
  ;;     [[:child _0] [:father _1] [:mother _2]]
  ;;     [[:parent _0] [:child _1]])
  :comment)
