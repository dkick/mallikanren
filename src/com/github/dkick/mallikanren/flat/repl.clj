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
      (name!? q)
      (l/member1o [:first "Damien"] q)
      (name!? q)))
  ;; => ([[:first "Damien"] [:middle _0] [:last _1]])
  (let [q nil]
    (l/run 3 [q]
      (l/conde
       [(l/== q [[:first "Damien"] [:middle "Robert"] [:last "Kick"]])]
       [(l/== q [[:first "Damien"] [:middle nil] [:last "Kick"]])]
       [(l/== q [[:nickname "D'amy"]])])
      (name!? q)))
  ;; => ([[:first "Damien"] [:middle "Robert"] [:last "Kick"]]
  ;;     [[:first "Damien"] [:middle nil] [:last "Kick"]])
  (let [q nil]
    (l/run 100 [q]
      (name!? {:first "Damien", :middle "Robert", :last "Kick"} q)
      (name!? q)))
  ;; => ([[:first "Damien"] [:middle "Robert"] [:last "Kick"]])
  (let [q nil]
    (l/run 10 [q]
      (person!? nil q)))
  ;; => ([[:id #uuid "d8508462-6ab2-4675-8435-837e88cd4dce"]
  ;;      [:name
  ;;       [[:first "qib5Vta00hj70L1mu0TBXJ5zz"]
  ;;        [:middle "K806Bi7qt2tIKX77Y4Ij4r014nsP"]
  ;;        [:last "J5tS8lOgzd"]]]]
  ;;     [[:id #uuid "d8508462-6ab2-4675-8435-837e88cd4dce"]
  ;;      [:name
  ;;       [[:first "qib5Vta00hj70L1mu0TBXJ5zz"]
  ;;        [:middle nil]
  ;;        [:last "xhgt13y11Y4I"]]]])
  (let [q nil]
    (l/run 10 [q]
      (l/or* (->> [name!? person!? parents!? children!?]
                  (map #(% q))))))
  ;; => ([[:first _0] [:middle _1] [:last _2]]
  ;;     [[:id _0] [:name _1]]
  ;;     [[:child _0] [:father _1] [:mother _2]]
  ;;     [[:parent _0] [:child _1]])
  :comment)
