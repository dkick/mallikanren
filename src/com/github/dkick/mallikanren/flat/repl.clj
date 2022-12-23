(ns com.github.dkick.mallikanren.flat.repl
  (:require
   [clojure.core.logic :as l]
   [com.github.dkick.mallikanren.flat.spock :as spock]))

(def Name
  [:map
   [:first :string]
   [:middle [:maybe :string]]
   [:last :string]])

(def Person
  [:map
   [:id :uuid]
   [:name Name]])

(def Parents
  [:map
   [:child :uuid]
   [:father :uuid]
   [:mother :uuid]])

(def Children
  [:map
   [:parent :uuid]
   [:child :uuid]])

(comment
  (let [name!? (spock/transform Name)
        q nil]
    (l/run 3 [q]
      (l/== q [[:first "Damien"] [:middle "Robert"] [:last "Kick"]])
      (name!? q)))
  ;; => ([[:first "Damien"] [:middle "Robert"] [:last "Kick"]])
  (spock/transform Person)
  (spock/-transform Parents)
  (spock/-transform Children)
  :comment)
