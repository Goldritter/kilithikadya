(ns kilithikadya.outrider-knights
  (:require [clojure.math.combinatorics :as combo]
            [clojure.test :refer :all]
            [clojure.walk :refer [postwalk]]
            [kilithikadya.kilithikadya :as k]
            [kilithikadya.csv :as CSV])
  (:import (org.apache.commons.numbers.core Precision)))


(def terminator-340-stormbolter {:attacker  "Terminator Stormbolter 340 pts sustained"
                                 :attacks   20
                                 :skill     3
                                 :strength  4
                                 :ap        0
                                 :damage    1
                                 :sustained 1
                                 })

(def terminator-340-stormbolter-12-inch {:attacker  "Terminator Stormbolter 12\" 340 pts sustained"
                                         :attacks   40
                                         :skill     3
                                         :strength  4
                                         :ap        0
                                         :damage    1
                                         :sustained 1
                                         })

(def terminator-340-stormbolter-oath {:attacker       "Terminator Stormbolter oath 340 pts sustained"
                                      :attacks        20
                                      :skill          3
                                      :strength       4
                                      :ap             0
                                      :damage         1
                                      :sustained      1
                                      :reroll-attack? true
                                      :attack-mod     1
                                      })

(def terminator-340-stormbolter-12-inch-oath {:attacker       "Terminator Stormbolter 12\" oath 340 pts sustained"
                                              :attacks        40
                                              :skill          3
                                              :strength       4
                                              :ap             0
                                              :damage         1
                                              :sustained      1
                                              :reroll-attack? true
                                              :attack-mod     1
                                              })

(def outrider-80-melee {:attacker "Outrider melee 80 pts"
                        :attacks  12
                        :skill    3
                        :strength 4
                        :ap       -1
                        :damage   1
                        })

(def outrider-80-bolter {:attacker      "Outrider 80 bolter pts"
                         :attacks       6
                         :skill         3
                         :strength      4
                         :ap            -1
                         :damage        1
                         :reroll-wound? true
                         })


(def outrider-80-heavy-bolt-pistol {:attacker "Outrider 80 heavy bolt pistol pts"
                                    :attacks  3
                                    :skill    3
                                    :strength 4
                                    :ap       -1
                                    :damage   1
                                    })


(def outrider-160-melee {:attacker "Outrider melee 160 pts"
                         :attacks  24
                         :skill    3
                         :strength 4
                         :ap       -1
                         :damage   1
                         })

(def outrider-160-bolter {:attacker      "Outrider 160 bolter pts"
                          :attacks       12
                          :skill         3
                          :strength      4
                          :ap            -1
                          :damage        1
                          :reroll-wound? true
                          })

(def outrider-160-heavy-bolt-postol {:attacker "Outrider 160 heavy bolt pistol pts"
                                     :attacks  6
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

(def black-knights-90 {:attacker     "Ravenwing Black Knights 90 pts "
                       :attacks      9
                       :skill        3
                       :strength     5
                       :ap           -2
                       :damage       1
                       :devastating? true
                       })

(def black-knights-180 {:attacker     "Ravenwing Black Knights 180 pts "
                        :attacks      18
                        :skill        3
                        :strength     5
                        :ap           -2
                        :damage       1
                        :devastating? true
                        })

(def black-knights-plasma-180 {:attacker "Ravenwing Black Knights plasma 180 pts "
                               :attacks  12
                               :skill    3
                               :strength 7
                               :ap       -2
                               :damage   1
                               })

(def black-knights-plasma-charged-180 {:attacker "Ravenwing Black Knights plasma charged 180 pts "
                                       :attacks  12
                                       :skill    3
                                       :strength 8
                                       :ap       -3
                                       :damage   2
                                       })

(def black-knights-plasma-90 {:attacker "Ravenwing Black Knights plasma 90 pts "
                              :attacks  12
                              :skill    3
                              :strength 7
                              :ap       -2
                              :damage   1
                              })

(def black-knights-plasma-charged-90 {:attacker "Ravenwing Black Knights plasma charged 90 pts "
                                      :attacks  12
                                      :skill    3
                                      :strength 8
                                      :ap       -3
                                      :damage   2
                                      })

(def black-knights-90-charge-vehicles {:attacker     "Ravenwing Black Knights 90 pts charge vehicles"
                                       :attacks      9
                                       :skill        3
                                       :strength     5
                                       :ap           -2
                                       :damage       1
                                       :anti         4
                                       :devastating? true
                                       })

(def black-knights-180-charge-vehicles {:attacker     "Ravenwing Black Knights 180 pts charge vehicles"
                                        :attacks      18
                                        :skill        3
                                        :strength     5
                                        :ap           -2
                                        :damage       1
                                        :anti         4
                                        :devastating? true
                                        })


(def intercessor {:defender  "Intercessor"
                  :toughness 4
                  :wounds    2
                  :save      3})

(def termagants {:defender  "Termangants"
                 :toughness 3
                 :wounds    1
                 :save      5})

(def terminator {:defender   "Terminator"
                 :toughness  5
                 :wounds     3
                 :save       2
                 :invul-save 4})

(def rhino {:defender  "Rhino"
            :toughness 9
            :wounds    10
            :save      3})

(def landraider {:defender  "Landraider"
                 :toughness 12
                 :wounds    16
                 :save      2})


(def black-knights {:defender   "Black Knights"
                    :toughness  5
                    :wounds     3
                    :save       3
                    :invul-save 5})

(def outrider {:defender  "Outrider"
               :toughness 5
               :wounds    4
               :save      3
               })

(def attack-combinations (map #(merge (first %) (second %))
                              (concat
                                (combo/cartesian-product [outrider-80-charge outrider-80-melee outrider-160-melee outrider-160-charge outrider-80-heavy-bolt-pistol outrider-160-heavy-bolt-postol
                                                          outrider-80-bolter outrider-160-bolter
                                                          black-knights-plasma-90 black-knights-plasma-180 black-knights-plasma-charged-90 black-knights-plasma-charged-180
                                                          terminator-340-stormbolter-12-inch-oath terminator-340-stormbolter-12-inch
                                                          terminator-340-stormbolter terminator-340-stormbolter-oath
                                                          ]
                                                         [termagants terminator rhino landraider intercessor black-knights])
                                (combo/cartesian-product [black-knights-90 black-knights-180] [terminator termagants intercessor landraider rhino outrider])
                                (combo/cartesian-product [black-knights-90-charge-vehicles black-knights-180-charge-vehicles] [landraider rhino])
                                )))

(def result
  (apply merge-with merge
         (map #(sorted-map
                  (:attacker %)
                  (sorted-map (:defender %) (k/get-probabilities-for %)))
               attack-combinations)))

(defn round-if-number [x]
  (if (number? x) (Precision/round x 3) x))


(defn get-averages [key-val]
  (map #(postwalk round-if-number
                  (assoc (select-keys (val %) [:expected-wounds :expected-damage :expected-kills :attacks :damage :wounds-needed-to-kill :expected-wounds-to-hits])
                    :defender (key %)
                    :attacker (key key-val)))
       (val key-val)))

