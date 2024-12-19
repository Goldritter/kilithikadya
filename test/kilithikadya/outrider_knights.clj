(ns kilithikadya.outrider-knights
  (:require [clojure.math.combinatorics :as combo]
            [clojure.test :refer :all]
            [clojure.walk :refer [postwalk]]
            [kilithikadya.kilithikadya :as k]
            [kilithikadya.csv :as csv])
  (:import (org.apache.commons.numbers.core Precision)))


(def outrider-80 {:attacker "Outrider 80 pts"
                  :attacks  12
                  :skill    3
                  :strength 4
                  :ap       -1
                  :damage   1
                  })

(def outrider-160 {:attacker "Outrider 160 pts"
                   :attacks  24
                   :skill    3
                   :strength 4
                   :ap       -1
                   :damage   1
                   })

(def outrider-80-charge {:attacker "Outrider 80 pts charge"
                         :attacks  12
                         :skill    3
                         :strength 5
                         :ap       -1
                         :damage   2
                         })
(def outrider-160-charge {:attacker "Outrider 160 pts charge"
                          :attacks  24
                          :skill    3
                          :strength 5
                          :ap       -1
                          :damage   2
                          })

(def black-knights-90 {:attacker "Ravenwing Black Knights 90 pts "
                       :attacks  9
                       :skill    3
                       :strength 5
                       :ap       -2
                       :damage   1
                       :devastating? true
                       })

(def black-knights-180 {:attacker "Ravenwing Black Knights 180 pts "
                        :attacks  18
                        :skill    3
                        :strength 5
                        :ap       -2
                        :damage   1
                        :devastating? true
                        })

(def black-knights-90-charge-vehicles {:attacker "Ravenwing Black Knights 90 pts charge vehicles"
                                       :attacks  9
                                       :skill    3
                                       :strength 5
                                       :ap       -2
                                       :damage   1
                                       :anti     4
                                       :devastating? true
                                       })

(def black-knights-180-charge-vehicles {:attacker "Ravenwing Black Knights 180 pts charge vehicles"
                                        :attacks  18
                                        :skill    3
                                        :strength 5
                                        :ap       -2
                                        :damage   1
                                        :anti     4
                                        :devastating? true
                                        })


(def intercessor {:defender   "Intercessor"
                  :toughness 4
                  :wounds     2
                  :save       3})

(def termagants {:defender   "Termangants"
                 :toughness 3
                 :wounds     1
                 :save       5})

(def terminator {:defender   "Terminator"
                 :toughness 5
                 :wounds     3
                 :save       2
                 :invul-save 4})


(def rhino {:defender   "Rhino"
            :toughness 9
            :wounds     10
            :save       3})

(def landraider {:defender   "Landraider"
                 :toughness 12
                 :wounds     16
                 :save       2})


(def black-knights {:defender   "Black Knights"
                    :toughness 5
                    :wounds     3
                    :save       3
                    :invul-save 5})

(def outrider {:defender   "Outrider"
               :toughness 5
               :wounds     4
               :save       3
               })

(def attack-combinations (map #(merge (first %) (second %))
                              (concat
                                (combo/cartesian-product [outrider-80-charge outrider-80 outrider-160 outrider-160-charge]
                                                         [termagants terminator rhino landraider intercessor black-knights])
                                (combo/cartesian-product [black-knights-90 black-knights-180] [terminator termagants intercessor landraider rhino outrider])
                                (combo/cartesian-product [black-knights-90-charge-vehicles black-knights-180-charge-vehicles] [landraider rhino])
                                )))

(def result
  (apply merge-with merge
         (pmap #(sorted-map
                  (:attacker %)
                  (sorted-map (:defender %) (k/get-probabilities-for %)))
               attack-combinations)))

(defn round-if-number [x]
  (if (number? x) (Precision/round x 3) x))


(defn get-averages [key-val]
  (map #(postwalk round-if-number
                  (assoc (select-keys (val %) [:expected-wounds :expected-damage :expected-kills :attacks :damage :wounds-needed-to-kill])
                    :defender (key %)
                    :attacker (key key-val)))
       (val key-val)))

