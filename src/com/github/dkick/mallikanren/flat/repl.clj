(ns com.github.dkick.mallikanren.flat.repl
  (:require
   [malli.generator :as mg]))

(def Name
  [:map
   [:id :uuid]
   [:first :string]
   [:middle [:maybe :string]]
   [:last :string]])

(comment
  (mg/generate Name)
  ;; => {:id #uuid "b372e59e-dab9-425e-8e2c-bcfc41f43ac3",
  ;;     :first "8lYtN3VChaHRpE",
  ;;     :middle "T11yXna3vuuE3SuV",
  ;;     :last "PXk7R65i67N49h5sVzP9Bk6t16"}
  :comment)
