(ns kilithikadya.core
  (:require [kilithikadya.kilithikadya :as K]
            [clojure.main :refer :all]
            [cheshire.core :as JSON])
  (:gen-class))

(defn -main []
  ;;  ...
  (clojure.main/main "-e" "(in-ns 'kilithikadya.core)"))