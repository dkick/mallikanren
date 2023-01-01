(ns com.github.dkick.mallikanren.flat.repl
  (:require
   [clojure.core.logic :as l]
   [com.github.dkick.mallikanren.flat.spock :as spock]))

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

(comment
  (require '[malli.util :as mu])
  (mu/get Person :id)
  ;; => :uuid
  (let [q nil]
    (l/run 3 [q]
      (name!? q)
      (l/conde
       [(l/member1o [:first "Damien"] q)]
       [(l/member1o [:first 13] q)])
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
  :comment)
