(ns com.github.dkick.mallikanren.flat.repl
  (:require
   [com.github.dkick.mallikanren.flat.spock :as spock]
   [encaje.core :refer [fx]]
   [malli.core :as m]
   [malli.json-schema :as mj]))

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
  (spock/transform Name)
  ;; => [:map [:first :string] [:middle [:maybe :string]] [:last :string]]
  (spock/transform Person)
  ;; => [:map [:id :uuid] [:name [:map [:first :string] [:middle [:maybe :string]] [:last :string]]]]
  (spock/-transform Parents)
  ;; => [:map [:child :uuid] [:father :uuid] [:mother :uuid]]
  (spock/-transform Children)
  ;; => [:map [:parent :uuid] [:child :uuid]]
  :comment)
