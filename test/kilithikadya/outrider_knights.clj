(ns kilithikadya.outrider-knights
  (:require [clojure.math.combinatorics :as combo]
            [clojure.test :refer :all]
            [clojure.walk :refer [postwalk]]
            [kilithikadya.csv :as CSV]
            [kilithikadya.kilithikadya :as k])

  (:import (org.apache.commons.numbers.core Precision)))


(def repulsor-executioner-marcro-plasma-standart {:attacker "Repulsor Executioner Macro plasma incinerator standart"
                                                  :attacks  5
                                                  :skill    3
                                                  :strength 8
                                                  :ap       -3
                                                  :damage   2
                                                  :points   220
                                                  })

(def repulsor-executioner-marcro-plasma-charged {:attacker "Repulsor Executioner Macro plasma incinerator charged"
                                                 :attacks  5
                                                 :skill    3
                                                 :strength 9
                                                 :ap       -4
                                                 :damage   3
                                                 :points   220
                                                 })

(def inceptor-240-plasma-standart {:attacker      "Inceptor 240 pts Plasma exterminatores standart"
                                   :attacks       12
                                   :skill         3
                                   :strength      7
                                   :ap            -2
                                   :damage        2
                                   :reroll-wound? true
                                   :points        240
                                   })

(def inceptor-240-plasma-supercharged {:attacker      "Inceptor 240 pts Plasma exterminatores supercharged"
                                       :attacks       12
                                       :skill         3
                                       :strength      7
                                       :ap            -3
                                       :damage        3
                                       :reroll-wound? true
                                       :points        240
                                       })

(def terminator-340-power-fist {:attacker "Terminator Power fists 340 pts"
                                :attacks  30
                                :skill    3
                                :strength 8
                                :ap       -2
                                :damage   2
                                :points   340
                                })


(def terminator-340-power-fist-sustained {:attacker  "Terminator Power fists 340 pts sustained"
                                          :attacks   30
                                          :skill     3
                                          :strength  8
                                          :ap        -2
                                          :damage    2
                                          :sustained 1
                                          :points    340
                                          })

(def terminator-340-stormbolter-sustained {:attacker  "Terminator Stormbolter 340 pts sustained"
                                           :attacks   20
                                           :skill     3
                                           :strength  4
                                           :ap        0
                                           :damage    1
                                           :sustained 1
                                           :points    340
                                           })

(def terminator-340-stormbolter {:attacker "Terminator Stormbolter 340 pts"
                                 :attacks  20
                                 :skill    3
                                 :strength 4
                                 :ap       0
                                 :damage   1
                                 :points   340
                                 })

(def terminator-340-stormbolter-12-inch {:attacker  "Terminator Stormbolter 12\" 340 pts sustained"
                                         :attacks   40
                                         :skill     3
                                         :strength  4
                                         :ap        0
                                         :damage    1
                                         :sustained 1
                                         :points    340
                                         })

(def terminator-340-stormbolter-12-inch-pyro {:attacker  "Terminator Stormbolter 12\" 340 pts sustained pyro"
                                              :attacks   40
                                              :skill     3
                                              :strength  4
                                              :ap        -1
                                              :damage    1
                                              :sustained 1
                                              :points    340
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
                                      :points         340
                                      })

(def terminator-340-stormbolter-12-inc-oath-pyro {:attacker       "Terminator Stormbolter 12\" oath 340 pts sustained pyro"
                                                  :attacks        40
                                                  :skill          3
                                                  :strength       4
                                                  :ap             -1
                                                  :damage         1
                                                  :sustained      1
                                                  :reroll-attack? true
                                                  :attack-mod     1
                                                  :points         340
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
                                              :points         340
                                              })

(def outrider-80-melee {:attacker "Outrider melee 80 pts"
                        :attacks  12
                        :skill    3
                        :strength 4
                        :ap       -1
                        :damage   1
                        :points   80
                        })

(def outrider-80-bolter {:attacker      "Outrider 80 bolter pts"
                         :attacks       6
                         :skill         3
                         :strength      4
                         :ap            -1
                         :damage        1
                         :reroll-wound? true
                         :points        80
                         })


(def outrider-80-heavy-bolt-pistol {:attacker "Outrider 80 heavy bolt pistol pts"
                                    :attacks  3
                                    :skill    3
                                    :strength 4
                                    :ap       -1
                                    :damage   1
                                    :points   80
                                    })


(def outrider-160-melee {:attacker "Outrider melee 160 pts"
                         :attacks  24
                         :skill    3
                         :strength 4
                         :ap       -1
                         :damage   1
                         :points   160
                         })

(def outrider-160-melee-chaplain {:attacker  "Outrider melee 160 pts with Chaplain"
                                  :attacks   24
                                  :skill     3
                                  :strength  4
                                  :ap        -1
                                  :damage    1
                                  :wound-mod 1
                                  :points    160
                                  })

(def outrider-160-bolter-chaplain {:attacker      "Outrider 160 bolter pts with Chaplain"
                                   :attacks       12
                                   :skill         3
                                   :strength      4
                                   :ap            -1
                                   :damage        1
                                   :reroll-wound? true
                                   :devastating?  true
                                   :points        160
                                   })

(def outrider-160-bolter {:attacker      "Outrider 160 bolter pts"
                          :attacks       12
                          :skill         3
                          :strength      4
                          :ap            -1
                          :damage        1
                          :reroll-wound? true
                          :points        160
                          })

(def outrider-160-heavy-bolt-pistol {:attacker "Outrider 160 heavy bolt pistol pts"
                                     :attacks  6
                                     :skill    3
                                     :strength 4
                                     :ap       -1
                                     :damage   1
                                     :points   160
                                     })
(def outrider-160-heavy-bolt-postol-chaplain {:attacker     "Outrider 160 heavy bolt pistol pts with chaplain"
                                              :attacks      6
                                              :skill        3
                                              :strength     4
                                              :ap           -1
                                              :damage       1
                                              :devastating? true
                                              :points       160
                                              })

(def outrider-80-charge {:attacker "Outrider 80 pts charge"
                         :attacks  12
                         :skill    3
                         :strength 5
                         :ap       -1
                         :damage   2
                         :points   80
                         })

(def outrider-160-charge {:attacker "Outrider 160 pts charge"
                          :attacks  24
                          :skill    3
                          :strength 5
                          :ap       -1
                          :damage   2
                          :points   160
                          })

(def outrider-160-charge-chaplain {:attacker  "Outrider 160 pts charge with chaplain"
                                   :attacks   24
                                   :skill     3
                                   :strength  5
                                   :ap        -1
                                   :damage    2
                                   :wound-mod 1
                                   :points    160
                                   })

(def black-knights-90 {:attacker     "Ravenwing Black Knights 90 pts "
                       :attacks      9
                       :skill        3
                       :strength     5
                       :ap           -2
                       :damage       1
                       :devastating? true
                       :points       90
                       })

(def black-knights-180 {:attacker     "Ravenwing Black Knights 180 pts "
                        :attacks      18
                        :skill        3
                        :strength     5
                        :ap           -2
                        :damage       1
                        :devastating? true
                        :points       180
                        })

(def black-knights-180-chaplain {:attacker     "Ravenwing Black Knights 180 pts with chaplain"
                                 :attacks      18
                                 :skill        3
                                 :strength     5
                                 :ap           -2
                                 :damage       1
                                 :devastating? true
                                 :wound-mod    1
                                 :points       180
                                 })

(def black-knights-plasma-180 {:attacker "Ravenwing Black Knights plasma 180 pts"
                               :attacks  12
                               :skill    3
                               :strength 7
                               :ap       -2
                               :damage   1
                               :points   180
                               })

(def black-knights-plasma-180-chaplain {:attacker     "Ravenwing Black Knights plasma 180 pts with chaplain"
                                        :attacks      12
                                        :skill        3
                                        :strength     7
                                        :ap           -2
                                        :damage       1
                                        :devastating? true
                                        :points       180
                                        })

(def black-knights-plasma-charged-180 {:attacker "Ravenwing Black Knights plasma charged 180 pts "
                                       :attacks  12
                                       :skill    3
                                       :strength 8
                                       :ap       -3
                                       :damage   2
                                       :points   180
                                       })

(def black-knights-plasma-charged-180-chaplain {:attacker     "Ravenwing Black Knights plasma charged 180 pts with chaplain"
                                                :attacks      12
                                                :skill        3
                                                :strength     8
                                                :ap           -3
                                                :damage       2
                                                :devastating? true
                                                :points       180
                                                })

(def black-knights-plasma-90 {:attacker "Ravenwing Black Knights plasma 90 pts "
                              :attacks  6
                              :skill    3
                              :strength 7
                              :ap       -2
                              :damage   1
                              :points   90
                              })

(def black-knights-plasma-charged-90 {:attacker "Ravenwing Black Knights plasma charged 90 pts "
                                      :attacks  6
                                      :skill    3
                                      :strength 8
                                      :ap       -3
                                      :damage   2
                                      :points   90
                                      })

(def black-knights-90-charge-vehicles {:attacker     "Ravenwing Black Knights 90 pts charge vehicles"
                                       :attacks      9
                                       :skill        3
                                       :strength     5
                                       :ap           -2
                                       :damage       1
                                       :anti         4
                                       :devastating? true
                                       :points       90
                                       })

(def black-knights-180-charge-vehicles {:attacker     "Ravenwing Black Knights 180 pts charge vehicles"
                                        :attacks      18
                                        :skill        3
                                        :strength     5
                                        :ap           -2
                                        :damage       1
                                        :anti         4
                                        :devastating? true
                                        :points       180
                                        })
(def black-knights-180-charge-vehicles-chaplain {:attacker     "Ravenwing Black Knights 180 pts charge vehicles with chaplain"
                                                 :attacks      18
                                                 :skill        3
                                                 :strength     5
                                                 :ap           -2
                                                 :damage       1
                                                 :anti         4
                                                 :devastating? true
                                                 :wound-mod    1
                                                 :points       180
                                                 })


(def deathwing-knights-mace-of-absolution-vehicles {:attacker "Deathwing Knights 250 pts Mace of absolution vehicles"
                                                    :attacks  16
                                                    :skill    2
                                                    :strength 6
                                                    :ap       -2
                                                    :damage   2
                                                    :anti     4
                                                    :points   250
                                                    })

(def deathwing-knights-mace-of-absolution-vehicles-sustained {:attacker "Deathwing Knights 250 pts Mace of absolution vehicles sustained"
                                                              :attacks  16
                                                              :skill    2
                                                              :strength 6
                                                              :ap       -2
                                                              :damage   2
                                                              :anti     4
                                                              :sustained 1
                                                              :points 250
                                                              })

(def deathwing-knights-mace-of-absolution-normal {:attacker "Deathwing Knights 250 pts Mace of absolution"
                                                  :attacks  16
                                                  :skill    2
                                                  :strength 6
                                                  :ap       -2
                                                  :damage   2
                                                  :points   250
                                                  })

(def deathwing-knights-mace-of-absolution-normal-sustained {:attacker  "Deathwing Knights 250 pts Mace of absolution sustained"
                                                            :attacks   16
                                                            :skill     2
                                                            :strength  6
                                                            :ap        -2
                                                            :damage    2
                                                            :sustained 1
                                                            :points    250
                                                            })

(def deathwing-knights-power-weapon {:attacker "Deathwing Knights 250 pts Power Weapon"
                                     :attacks  20
                                     :skill    2
                                     :strength 6
                                     :ap       -2
                                     :damage   2
                                     :points   250
                                     })

(def deathwing-knights-power-weapon-sustained {:attacker  "Deathwing Knights 250 pts Power Weapon sustained"
                                               :attacks   20
                                               :skill     2
                                               :strength  6
                                               :ap        -2
                                               :damage    2
                                               :sustained 1
                                               :points    250
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
                                (combo/cartesian-product [outrider-80-charge outrider-80-melee outrider-160-melee outrider-160-charge outrider-80-heavy-bolt-pistol outrider-160-heavy-bolt-pistol
                                                          black-knights-plasma-180-chaplain outrider-80-bolter outrider-160-bolter outrider-160-bolter-chaplain outrider-160-charge-chaplain outrider-160-heavy-bolt-postol-chaplain
                                                          black-knights-plasma-180 black-knights-plasma-90 black-knights-plasma-180 black-knights-plasma-charged-90 black-knights-plasma-charged-180
                                                          black-knights-plasma-charged-180-chaplain black-knights-180-chaplain repulsor-executioner-marcro-plasma-charged repulsor-executioner-marcro-plasma-standart
                                                          terminator-340-stormbolter-12-inch-oath terminator-340-stormbolter-12-inch terminator-340-power-fist-sustained terminator-340-stormbolter-sustained
                                                          terminator-340-stormbolter terminator-340-stormbolter-oath terminator-340-stormbolter-12-inch-pyro terminator-340-stormbolter-12-inc-oath-pyro
                                                          outrider-160-melee-chaplain inceptor-240-plasma-standart inceptor-240-plasma-supercharged terminator-340-power-fist
                                                          deathwing-knights-mace-of-absolution-normal-sustained deathwing-knights-power-weapon-sustained deathwing-knights-power-weapon deathwing-knights-mace-of-absolution-normal]
                                                         [termagants terminator rhino landraider intercessor black-knights outrider])
                                (combo/cartesian-product [black-knights-90 black-knights-180] [terminator termagants intercessor landraider rhino outrider])
                                (combo/cartesian-product [black-knights-90-charge-vehicles black-knights-180-charge-vehicles black-knights-180-charge-vehicles-chaplain
                                                          deathwing-knights-mace-of-absolution-vehicles-sustained deathwing-knights-mace-of-absolution-vehicles]
                                                         [landraider rhino])
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

(comment
  (CSV/write-csv "calculations.csv" [:attacker :defender :expected-wounds :expected-damage :expected-kills :expected-wounds-to-hits :attacks :wounds-needed-to-kill :damage] (apply concat (map get-averages result))))