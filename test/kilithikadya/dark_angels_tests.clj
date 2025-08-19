(ns kilithikadya.dark-angels-tests
  (:require [clojure.math.combinatorics :as combo]
            [clojure.test :refer :all]
            [clojure.walk :refer [postwalk]]
            [kilithikadya.csv :as CSV]
            [kilithikadya.kilithikadya :as k])

  (:import (org.apache.commons.numbers.core Precision)))

(def blade-champion-behemor {:attacker "Blade Champion Behemor"
                             :unit     "Blade Champion"
                             :attacks  6
                             :skill    2
                             :strength 7
                             :ap       -2
                             :damage   2
                             :points   145})

(def blade-champion-hurricanis {:attacker  "Blade Champion Hurricanis"
                                :unit      "Blade Champion"
                                :attacks   9
                                :skill     2
                                :strength  5
                                :ap        -1
                                :damage    1
                                :points    145
                                :sustained 1})

(def blade-champion-victus {:attacker     "Blade Champion Victus"
                            :unit         "Blade Champion"
                            :attacks      5
                            :skill        2
                            :strength     6
                            :ap           -3
                            :damage       3
                            :points       145
                            :devastating? true})

(def custodian-wardens-guardian-spear-melee {:attacker "Custodian Wardens Guardian Spear melee"
                                             :unit     "Custodian Wardens"
                                             :attacks  25
                                             :skill    2
                                             :strength 7
                                             :ap       -2
                                             :damage   2
                                             :points   260})

(def custodian-wardens-castellan-axe-melee {:attacker "Custodian Wardens Castellan axe melee"
                                            :attacks  20
                                            :unit     "Custodian Wardens"
                                            :skill    2
                                            :strength 9
                                            :ap       -1
                                            :damage   3
                                            :points   260})

(def custodian-wardens-castellan-axe-shoot {:attacker "Custodian Wardens Castellan axe shoot"
                                            :attacks  10
                                            :unit     "Custodian Wardens"
                                            :skill    2
                                            :strength 4
                                            :ap       -1
                                            :damage   2
                                            :points   260})


(def custodian-wardens-guardian-spear-shoot {:attacker "Custodian Wardens Guardian spear shoot"
                                             :attacks  10
                                             :unit     "Custodian Wardens"
                                             :skill    2
                                             :strength 4
                                             :ap       -1
                                             :damage   2
                                             :points   260})

(def hormagaunts-130 {:attacker "Hormagaunts"
                      :unit     "Hormagaunts"
                      :attacks  60
                      :skill    4
                      :strength 3
                      :ap       -1
                      :damage   1
                      :points   130})

(def termagants-120-fleshborer {:attacker "Termagants Fleshborer"
                                :unit     "Termagants"
                                :attacks  20
                                :skill    4
                                :strength 5
                                :ap       0
                                :damage   1
                                :points   120})

(def termagants-120-spinefists {:attacker      "Termagants Spinefists"
                                :unit          "Termagants"
                                :attacks       40
                                :skill         4
                                :strength      3
                                :ap            0
                                :damage        1
                                :points        120
                                :reroll-wound? true})


(def repulsor-180-onslaught {:attacker     "Repulsor heavy onslaught cannon"
                             :unit         "Repulsor"
                             :attacks      12
                             :skill        3
                             :strength     6
                             :ap           0
                             :damage       1
                             :points       180
                             :devastating? true})

(def repulsor-180-defensive-array {:attacker "Repulsor defensive array"
                                   :unit     "Repulsor"
                                   :attacks  18
                                   :skill    3
                                   :strength 4
                                   :ap       0
                                   :damage   1
                                   :points   180})

(def heavy-intercessor-220-heavy-bolt-rifle {:attacker "Heavy Intercessor Squad heavy bolt rifle"
                                             :unit     "Heavy Intercessors"
                                             :attacks  20
                                             :skill    3
                                             :strength 5
                                             :ap       -1
                                             :damage   2
                                             :points   220})

(def heavy-intercessor-220-heavy-bolt-rifle-oath-pyro {:attacker       "Heavy Intercessor Squad heavy bolt rifle oath and pyro"
                                                       :unit           "Heavy Intercessors"
                                                       :attacks        20
                                                       :skill          3
                                                       :strength       5
                                                       :ap             -2
                                                       :damage         2
                                                       :points         285
                                                       :reroll-attack? true})

(def heavy-intercessor-220-heavy-bolt-rifle-standing {:attacker "Heavy Intercessor Squad heavy bolt rifle not moving"
                                                      :unit     "Heavy Intercessors"
                                                      :attacks  20
                                                      :skill    2
                                                      :strength 5
                                                      :ap       -1
                                                      :damage   2
                                                      :points   220})

(def heavy-intercessor-220-heavy-bolt-rifle-oath-pyro-standing {:attacker       "Heavy Intercessor Squad heavy bolt rifle oath and pyro not moving"
                                                                :unit           "Heavy Intercessors"
                                                                :attacks        20
                                                                :skill          2
                                                                :strength       5
                                                                :ap             -2
                                                                :damage         2
                                                                :points         285
                                                                :reroll-attack? true})

(def assault-intercessor-150-chain-sword {:attacker "Assault Intercessor Squad chain sword"
                                          :unit     "Assault Intercessors"
                                          :attacks  40
                                          :skill    3
                                          :strength 4
                                          :ap       -1
                                          :damage   1
                                          :points   150})

(def assault-intercessor-150-chain-sword-objective-marker {:attacker      "Assault Intercessor Squad chain sword against targets on objective marker"
                                                           :unit          "Assault Intercessors"
                                                           :attacks       40
                                                           :skill         3
                                                           :strength      4
                                                           :ap            -1
                                                           :damage        1
                                                           :points        150
                                                           :reroll-wound? true})

(def intercessor-160-bolt-rifle {:attacker "Intercessor Squad bolt rifle"
                                 :unit     "Intercessor"
                                 :attacks  40
                                 :skill    3
                                 :strength 4
                                 :ap       -1
                                 :damage   1
                                 :points   160})

(def intercessor-160-bolt-rifle-lethal {:attacker "Intercessor Squad bolt rifle lethal"
                                        :unit     "Intercessor"
                                        :attacks  40
                                        :skill    3
                                        :strength 4
                                        :ap       -1
                                        :damage   1
                                        :lethal?  true
                                        :points   160})


(def intercessor-160-bolt-rifle-sustained-lethal {:attacker  "Intercessor Squad bolt rifle sustained 1 and lethal hits"
                                                  :unit      "Intercessor"
                                                  :attacks   40
                                                  :skill     3
                                                  :strength  4
                                                  :ap        -1
                                                  :damage    1
                                                  :lethal?   true
                                                  :sustained 1
                                                  :points    160})

(def intercessor-160-bolt-rifle-sustained-lethal-S6 {:attacker  "Intercessor Squad bolt rifle sustained 1 and lethal hits S+2"
                                                  :unit      "Intercessor"
                                                  :attacks   40
                                                  :skill     3
                                                  :strength  6
                                                  :ap        -1
                                                  :damage    1
                                                  :lethal?   true
                                                  :sustained 1
                                                  :points    340})

(def intercessor-standing-160-bolt-rifle-sustained-lethal-S6 {:attacker  "Intercessor Squad standing bolt rifle sustained 1 and lethal hits S+2"
                                                     :unit      "Intercessor"
                                                     :attacks   40
                                                     :skill     2
                                                     :strength  6
                                                     :ap        -1
                                                     :damage    1
                                                     :lethal?   true
                                                     :sustained 1
                                                     :points    340})

(def hellblaster-standart-sustained-lethal-S9 {:attacker  "Hellblaster Plasma Incinerator standart sustained 1 and lethal hits S+2"
                                                              :unit      "Hellblaster"
                                                              :attacks   20
                                                              :skill     3
                                                              :strength  9
                                                              :ap        -2
                                                              :damage    1
                                                              :lethal?   true
                                                              :sustained 1
                                                              :points    400})

(def hellblaster-overcharged-sustained-lethal-S10 {:attacker  "Hellblaster Plasma Incinerator overcharged sustained 1 and lethal hits S+2"
                                               :unit      "Hellblaster"
                                               :attacks   20
                                               :skill     3
                                               :strength  10
                                               :ap        -3
                                               :damage    2
                                               :lethal?   true
                                               :sustained 1
                                               :points    400})


(def intercessor-160-bolt-rifle-oath-pyro {:attacker       "Intercessor Squad bolt rifle oath and pyro"
                                           :unit           "Intercessor"
                                           :attacks        40
                                           :skill          3
                                           :strength       4
                                           :ap             -2
                                           :damage         1
                                           :reroll-attack? true
                                           :points         225})

(def intercessor-160-bolt-rifle-oath-pyro-lethal {:attacker       "Intercessor Squad bolt rifle oath, lethal and pyro"
                                                  :unit           "Intercessor"
                                                  :attacks        40
                                                  :skill          3
                                                  :strength       4
                                                  :ap             -2
                                                  :damage         1
                                                  :lethal?        true
                                                  :reroll-attack? true
                                                  :points         225})


(def intercessor-160-bolt-rifle-standing {:attacker "Intercessor Squad bolt rifle not moving"
                                          :unit     "Intercessor"
                                          :attacks  40
                                          :skill    2
                                          :strength 4
                                          :ap       -1
                                          :damage   1
                                          :points   160})

(def intercessor-160-bolt-rifle-standing-lethal {:attacker "Intercessor Squad bolt rifle not moving and lethal"
                                                 :unit     "Intercessor"
                                                 :attacks  40
                                                 :skill    2
                                                 :strength 4
                                                 :ap       -1
                                                 :damage   1
                                                 :lethal?  true
                                                 :points   160})

(def intercessor-160-bolt-rifle-standing-sustained-lethal {:attacker  "Intercessor Squad bolt rifle not moving sustained 1 and lethal hits"
                                                           :unit      "Intercessor"
                                                           :attacks   40
                                                           :skill     2
                                                           :strength  4
                                                           :ap        -1
                                                           :damage    1
                                                           :lethal?   true
                                                           :sustained 1
                                                           :points    160})



(def intercessor-160-bolt-rifle-oath-pyro-standing {:attacker       "Intercessor Squad bolt rifle oath and pyro not moving"
                                                    :unit           "Intercessor"
                                                    :attacks        40
                                                    :skill          2
                                                    :strength       4
                                                    :ap             -2
                                                    :damage         1
                                                    :reroll-attack? true
                                                    :points         225})

(def intercessor-160-bolt-rifle-oath-pyro-standing-lethal {:attacker       "Intercessor Squad bolt rifle oath, lethal and pyro not moving"
                                                           :unit           "Intercessor"
                                                           :attacks        40
                                                           :skill          2
                                                           :strength       4
                                                           :ap             -2
                                                           :damage         1
                                                           :lethal?        true
                                                           :reroll-attack? true
                                                           :points         225})

(def repulsor-executioner-marcro-plasma-standart {:attacker "Repulsor Executioner Macro plasma incinerator standart"
                                                  :unit     "Repulsor Executioner"
                                                  :attacks  5
                                                  :skill    3
                                                  :strength 8
                                                  :ap       -3
                                                  :damage   2
                                                  :points   220})

(def repulsor-executioner-marcro-plasma-charged {:attacker "Repulsor Executioner Macro plasma incinerator charged"
                                                 :unit     "Repulsor Executioner"
                                                 :attacks  5
                                                 :skill    3
                                                 :strength 9
                                                 :ap       -4
                                                 :damage   3
                                                 :points   220})

(def inceptor-240-plasma-standart {:attacker      "Inceptor Plasma exterminatores standart"
                                   :unit          "Inceptor"
                                   :attacks       12
                                   :skill         3
                                   :strength      7
                                   :ap            -2
                                   :damage        2
                                   :reroll-wound? true
                                   :points        240})

(def inceptor-240-plasma-supercharged {:attacker      "Inceptor Plasma exterminatores supercharged"
                                       :unit          "Inceptor"
                                       :attacks       12
                                       :skill         3
                                       :strength      7
                                       :ap            -3
                                       :damage        3
                                       :reroll-wound? true
                                       :points        240})

(def terminator-340-power-fist {:attacker "Terminator Power fists"
                                :attacks  30
                                :skill    3
                                :strength 8
                                :ap       -2
                                :damage   2
                                :points   340})


(def terminator-340-power-fist-sustained {:attacker  "Terminator Power fists sustained"
                                          :unit      "Terminator"
                                          :attacks   30
                                          :skill     3
                                          :strength  8
                                          :ap        -2
                                          :damage    2
                                          :sustained 1
                                          :points    340})

(def terminator-340-stormbolter-sustained {:attacker  "Terminator Stormbolter sustained"
                                           :unit      "Terminator"
                                           :attacks   20
                                           :skill     3
                                           :strength  4
                                           :ap        0
                                           :damage    1
                                           :sustained 1
                                           :points    340})

(def terminator-340-stormbolter-sustained-s6 {:attacker  "Terminator Stormbolter sustained S+2"
                                           :unit      "Terminator"
                                           :attacks   20
                                           :skill     3
                                           :strength  6
                                           :ap        0
                                           :damage    1
                                           :sustained 1
                                           :points    415})

(def terminator-340-stormbolter-sustained-s6-12-inch {:attacker  "Terminator Stormbolter sustained S+2 12 inch"
                                              :unit      "Terminator"
                                              :attacks   40
                                              :skill     3
                                              :strength  6
                                              :ap        0
                                              :damage    1
                                              :sustained 1
                                              :points    415})

(def terminator-340-stormbolter {:attacker "Terminator Stormbolter"
                                 :unit     "Terminator"
                                 :attacks  20
                                 :skill    3
                                 :strength 4
                                 :ap       0
                                 :damage   1
                                 :points   340})

(def terminator-340-stormbolter-12-inch {:attacker  "Terminator Stormbolter 12\" sustained"
                                         :unit      "Terminator"
                                         :attacks   40
                                         :skill     3
                                         :strength  4
                                         :ap        0
                                         :damage    1
                                         :sustained 1
                                         :points    340})

(def terminator-340-stormbolter-12-inch-pyro {:attacker  "Terminator Stormbolter 12\" sustained pyro"
                                              :unit      "Terminator"
                                              :attacks   40
                                              :skill     3
                                              :strength  4
                                              :ap        -1
                                              :damage    1
                                              :sustained 1
                                              :points    415})

(def terminator-340-stormbolter-oath {:attacker       "Terminator Stormbolter oath sustained"
                                      :unit           "Terminator"
                                      :attacks        20
                                      :skill          3
                                      :strength       4
                                      :ap             0
                                      :damage         1
                                      :sustained      1
                                      :reroll-attack? true
                                      :attack-mod     1
                                      :points         340})

(def terminator-340-stormbolter-12-inc-oath-pyro {:attacker       "Terminator Stormbolter 12\" oath sustained pyro"
                                                  :unit           "Terminator"
                                                  :attacks        40
                                                  :skill          3
                                                  :strength       4
                                                  :ap             -1
                                                  :damage         1
                                                  :sustained      1
                                                  :reroll-attack? true
                                                  :attack-mod     1
                                                  :points         415})

(def terminator-340-stormbolter-12-inch-oath {:attacker       "Terminator Stormbolter 12\" oath sustained"
                                              :unit           "Terminator"
                                              :attacks        40
                                              :skill          3
                                              :strength       4
                                              :ap             0
                                              :damage         1
                                              :sustained      1
                                              :reroll-attack? true
                                              :attack-mod     1
                                              :points         340})

(def outrider-160-melee {:attacker "Outrider melee"
                         :unit     "Outrider"
                         :attacks  24
                         :skill    3
                         :strength 4
                         :ap       -1
                         :damage   1
                         :points   160})

(def outrider-160-melee-chaplain {:attacker  "Outrider melee with Chaplain" :unit "Outrider"

                                  :attacks   24
                                  :skill     3
                                  :strength  4
                                  :ap        -1
                                  :damage    1
                                  :wound-mod 1
                                  :points    160})

(def outrider-160-bolter-chaplain {:attacker      "Outrider bolter with Chaplain"
                                   :unit          "Outrider"
                                   :attacks       12
                                   :skill         3
                                   :strength      4
                                   :ap            -1
                                   :damage        1
                                   :devastating?  true
                                   :reroll-wound? true
                                   :points        160})

(def outrider-160-bolter {:attacker      "Outrider bolter"
                          :unit          "Outrider"
                          :attacks       12
                          :skill         3
                          :strength      4
                          :ap            -1
                          :damage        1
                          :reroll-wound? true
                          :points        160})

(def outrider-160-heavy-bolt-pistol {:attacker "Outrider heavy bolt pistol"
                                     :unit     "Outrider"
                                     :attacks  6
                                     :skill    3
                                     :strength 4
                                     :ap       -1
                                     :damage   1
                                     :points   160})
(def outrider-160-heavy-bolt-postol-chaplain {:attacker     "Outrider heavy bolt pistol with chaplain"
                                              :unit         "Outrider"
                                              :attacks      6
                                              :skill        3
                                              :strength     4
                                              :ap           -1
                                              :damage       1
                                              :devastating? true
                                              :points       160})


(def outrider-160-charge {:attacker "Outrider charge"
                          :unit     "Outrider"
                          :attacks  24
                          :skill    3
                          :strength 5
                          :ap       -1
                          :damage   2
                          :points   160})

(def outrider-160-charge-chaplain {:attacker  "Outrider charge with chaplain"
                                   :unit      "Outrider"
                                   :attacks   24
                                   :skill     3
                                   :strength  5
                                   :ap        -1
                                   :damage    2
                                   :wound-mod 1
                                   :points    160})

(def black-knights-180-melee {:attacker     "Ravenwing Black Knights melee"
                              :unit         "Ravenwing Black Knights"
                              :attacks      18
                              :skill        3
                              :strength     5
                              :ap           -2
                              :damage       1
                              :devastating? true
                              :points       180})

(def black-knights-180-chaplain-melee {:attacker     "Ravenwing Black Knights melee with chaplain"
                                       :unit         "Ravenwing Black Knights"
                                       :attacks      18
                                       :skill        3
                                       :strength     5
                                       :ap           -2
                                       :damage       1
                                       :devastating? true
                                       :wound-mod    1
                                       :points       180})

(def black-knights-plasma-180 {:attacker "Ravenwing Black Knights plasma "
                               :unit     "Ravenwing Black Knights"
                               :attacks  12
                               :skill    3
                               :strength 7
                               :ap       -2
                               :damage   1
                               :points   180})

(def black-knights-plasma-180-chaplain {:attacker     "Ravenwing Black Knights plasma with chaplain"
                                        :unit         "Ravenwing Black Knights"
                                        :attacks      12
                                        :skill        3
                                        :strength     7
                                        :ap           -2
                                        :damage       1
                                        :devastating? true
                                        :points       180})

(def black-knights-plasma-charged-180 {:attacker "Ravenwing Black Knights plasma charged"
                                       :unit     "Ravenwing Black Knights"
                                       :attacks  12
                                       :skill    3
                                       :strength 8
                                       :ap       -3
                                       :damage   2
                                       :points   180})

(def black-knights-plasma-charged-180-chaplain {:attacker     "Ravenwing Black Knights plasma charged with chaplain"
                                                :unit         "Ravenwing Black Knights"
                                                :attacks      12
                                                :skill        3
                                                :strength     8
                                                :ap           -3
                                                :damage       2
                                                :devastating? true
                                                :points       180})

(def black-knights-180-charge-vehicles {:attacker     "Ravenwing Black Knights charge vehicles"
                                        :unit         "Ravenwing Black Knights"
                                        :attacks      18
                                        :skill        3
                                        :strength     5
                                        :ap           -2
                                        :damage       1
                                        :anti         4
                                        :devastating? true
                                        :points       180})

(def black-knights-180-charge-vehicles-chaplain {:attacker     "Ravenwing Black Knights charge vehicles with chaplain"
                                                 :unit         "Ravenwing Black Knights"
                                                 :attacks      18
                                                 :skill        3
                                                 :strength     5
                                                 :ap           -2
                                                 :damage       1
                                                 :anti         4
                                                 :devastating? true
                                                 :wound-mod    1
                                                 :points       180})


(def deathwing-knights-mace-of-absolution-vehicles {:attacker "Deathwing Knights Mace of absolution vehicles"
                                                    :unit     "Deathwing Knights"
                                                    :attacks  16
                                                    :skill    2
                                                    :strength 6
                                                    :ap       -2
                                                    :damage   2
                                                    :anti     4
                                                    :points   250})

(def deathwing-knights-mace-of-absolution-vehicles-sustained {:attacker  "Deathwing Knights Mace of absolution vehicles sustained"
                                                              :unit      "Deathwing Knights"
                                                              :attacks   16
                                                              :skill     2
                                                              :strength  6
                                                              :ap        -2
                                                              :damage    2
                                                              :anti      4
                                                              :sustained 1
                                                              :points    250})

(def deathwing-knights-mace-of-absolution-normal {:attacker "Deathwing Knights Mace of absolution"
                                                  :unit     "Deathwing Knights"
                                                  :attacks  16
                                                  :skill    2
                                                  :strength 6
                                                  :ap       -2
                                                  :damage   2
                                                  :points   250})

(def deathwing-knights-mace-of-absolution-normal-sustained {:attacker  "Deathwing Knights Mace of absolution sustained"
                                                            :unit      "Deathwing Knights"
                                                            :attacks   16
                                                            :skill     2
                                                            :strength  6
                                                            :ap        -2
                                                            :damage    2
                                                            :sustained 1
                                                            :points    250})

(def deathwing-knights-power-weapon {:attacker "Deathwing Knights Power Weapon"
                                     :unit     "Deathwing Knights"
                                     :attacks  20
                                     :skill    2
                                     :strength 6
                                     :ap       -2
                                     :damage   2
                                     :points   250})

(def deathwing-knights-power-weapon-sustained {:attacker  "Deathwing Knights Power Weapon sustained"
                                               :unit      "Deathwing Knights"
                                               :attacks   20
                                               :skill     2
                                               :strength  6
                                               :ap        -2
                                               :damage    2
                                               :sustained 1
                                               :points    250})


(def sternguard-veteran-bolt-rifle {:attacker     "Sternguard bolt rifle"
                                    :unit         "Sternguard Veteran"
                                    :attacks      (* 8 2)
                                    :skill        3
                                    :strength     4
                                    :ap           -1
                                    :damage       1
                                    :devastating? true
                                    :points       160})


(def sternguard-veteran-bolt-rifle-vehicle-librarian {:attacker     "Sternguard bolt rifle against vehicle librarian"
                                                      :unit         "Sternguard Veteran"
                                                      :attacks      (* 8 2)
                                                      :skill        3
                                                      :strength     4
                                                      :ap           -1
                                                      :damage       1
                                                      :devastating? true
                                                      :anti         5
                                                      :points       225})

(def sternguard-veteran-bolt-rifle-standing {:attacker     "Sternguard bolt rifle standing"
                                             :unit         "Sternguard Veteran"
                                             :attacks      (* 8 2)
                                             :skill        2
                                             :strength     4
                                             :ap           -1
                                             :damage       1
                                             :devastating? true
                                             :points       160})

(def sternguard-veteran-bolt-rifle-standing-pyro-12-inch-oath {:attacker      "Sternguard bolt rifle standing 12\" oath pyro"
                                                               :unit          "Sternguard Veteran"
                                                               :attacks       (* 8 3)
                                                               :skill         2
                                                               :strength      4
                                                               :ap            -2
                                                               :damage        1
                                                               :devastating?  true
                                                               :reroll-wound? true
                                                               :points        160})


(def sternguard-veteran-bolt-rifle-vehicle-librarian-standing {:attacker     "Sternguard bolt rifle against vehicle librarian standing"
                                                               :unit         "Sternguard Veteran"
                                                               :attacks      (* 8 2)
                                                               :skill        2
                                                               :strength     4
                                                               :ap           -1
                                                               :damage       1
                                                               :devastating? true
                                                               :anti         5
                                                               :points       225})

(def sternguard-veteran-bolt-rifle-vehicle-librarian-standing-pyro-oath-12-inch {:attacker      "Sternguard bolt rifle against vehicle librarian standing pyro and oath 12\""
                                                                                 :unit          "Sternguard Veteran"
                                                                                 :attacks       (* 8 3)
                                                                                 :skill         2
                                                                                 :strength      4
                                                                                 :ap            -2
                                                                                 :damage        1
                                                                                 :devastating?  true
                                                                                 :reroll-wound? true
                                                                                 :anti          5
                                                                                 :points        225})


(def sternguard-veteran-heavy-bolter-standing-oath {:attacker      "Sternguard heavy bolter standing and oath"
                                                    :unit          "Sternguard Veteran"
                                                    :attacks       (* 2 3)
                                                    :skill         3
                                                    :strength      5
                                                    :ap            -1
                                                    :damage        2
                                                    :devastating?  true
                                                    :sustained     1
                                                    :reroll-wound? true
                                                    :points        40})


(def sternguard-veteran-heavy-bolter-vehicle-librarian-standing {:attacker     "Sternguard heavy bolter against vehicle librarian standing"
                                                                 :unit         "Sternguard Veteran"
                                                                 :attacks      (* 2 3)
                                                                 :skill        3
                                                                 :strength     5
                                                                 :ap           -1
                                                                 :damage       2
                                                                 :devastating? true
                                                                 :sustained    1
                                                                 :anti         5
                                                                 :points       105})

(def sternguard-veteran-heavy-bolter-standing {:attacker     "Sternguard heavy bolter standing"
                                               :unit         "Sternguard Veteran"
                                               :attacks      (* 2 3)
                                               :skill        3
                                               :strength     5
                                               :ap           -1
                                               :damage       2
                                               :devastating? true
                                               :sustained    1
                                               :points       40})


(def sternguard-veteran-heavy-bolter-vehicle-librarian {:attacker     "Sternguard heavy bolter against vehicle standing and  librarian"
                                                        :unit         "Sternguard Veteran"
                                                        :attacks      (* 2 3)
                                                        :skill        3
                                                        :strength     5
                                                        :ap           -1
                                                        :damage       2
                                                        :devastating? true
                                                        :sustained    1
                                                        :anti         5
                                                        :points       105})

(def sternguard-veteran-heavy-bolter-vehicle-librarian-standing-pyro-oath {:attacker      "Sternguard heavy bolter against vehicle standing librarian pyro and oath"
                                                                           :unit          "Sternguard Veteran"
                                                                           :attacks       (* 2 3)
                                                                           :skill         3
                                                                           :strength      5
                                                                           :ap            -2
                                                                           :damage        2
                                                                           :devastating?  true
                                                                           :sustained     1
                                                                           :anti          5
                                                                           :reroll-wound? true
                                                                           :points        105})

(def armiger-helverin-autocannons {:attacker "Armiger Helverin Autocannons"
                                   :unit     "Armiger Helverin"
                                   :attacks  (* 2 4)
                                   :skill    3
                                   :strength 9
                                   :ap       -1
                                   :damage   3
                                   :points   130})

(def armiger-helverin-melta-12-inch {:attacker "Armiger Helverin Meltagun 12\" "
                                     :unit     "Armiger Helverin"
                                     :attacks  1
                                     :skill    3
                                     :strength 9
                                     :ap       -4
                                     :damage   3
                                     :points   130})

(def armiger-helverin-melta-6-inch {:attacker "Armiger Helverin Meltagun 6\" "
                                    :unit     "Armiger Helverin"
                                    :attacks  1
                                    :skill    3
                                    :strength 9
                                    :ap       -4
                                    :damage   5
                                    :points   130})

(def armiger-helverin-questoris-heavy-stubber-36-inch {:attacker "Armiger Helverin Questoris heavy stubber 36\" "
                                                       :unit     "Armiger Helverin"
                                                       :attacks  3
                                                       :skill    3
                                                       :strength 4
                                                       :ap       -1
                                                       :damage   1
                                                       :points   130})


(def armiger-helverin-questoris-heavy-stubber-18-inch {:attacker "Armiger Helverin Questoris heavy stubber 18\" "
                                                       :unit     "Armiger Helverin"
                                                       :attacks  6
                                                       :skill    3
                                                       :strength 4
                                                       :ap       -1
                                                       :damage   1
                                                       :points   130})

(def librarian-smite-average-damage-3-attacks {:attacker "Librarian smite witchfire average damage and 3 attacks "
                                               :unit     "Librarian"
                                               :attacks  3
                                               :skill    3
                                               :strength 5
                                               :ap       -1
                                               :damage   2
                                               :points   65})

(def librarian-focused-smite-average-damage-3-attacks {:attacker     "Librarian smite focused witchfire average damage and 3 attacks "
                                                       :unit         "Librarian"
                                                       :attacks      3
                                                       :skill        3
                                                       :strength     6
                                                       :ap           -2
                                                       :damage       2
                                                       :devastating? true
                                                       :points       65})

(def librarian-smite-average-damage-4-attacks {:attacker "Librarian smite witchfire average damage and 4 attacks "
                                               :unit     "Librarian"
                                               :attacks  4
                                               :skill    3
                                               :strength 5
                                               :ap       -1
                                               :damage   2
                                               :points   65})

(def librarian-focused-smite-average-damage-4-attacks {:attacker     "Librarian smite focused witchfire average damage and 4 attacks "
                                                       :unit         "Librarian"
                                                       :attacks      4
                                                       :skill        3
                                                       :strength     6
                                                       :ap           -2
                                                       :damage       2
                                                       :devastating? true
                                                       :points       65})

(def librarian-smite-average-damage-5-attacks {:attacker "Librarian smite witchfire average damage and 5 attacks "
                                               :unit     "Librarian"
                                               :attacks  5
                                               :skill    3
                                               :strength 5
                                               :ap       -1
                                               :damage   2
                                               :points   65})

(def librarian-focused-smite-average-damage-5-attacks {:attacker     "Librarian smite focused witchfire average damage and 5 attacks "
                                                       :unit         "Librarian"
                                                       :attacks      5
                                                       :skill        3
                                                       :strength     6
                                                       :ap           -2
                                                       :damage       2
                                                       :devastating? true
                                                       :points       65})

(def librarian-smite-average-damage-6-attacks {:attacker "Librarian smite witchfire average damage and 6 attacks "
                                               :unit     "Librarian"
                                               :attacks  6
                                               :skill    3
                                               :strength 5
                                               :ap       -1
                                               :damage   2
                                               :points   65})

(def librarian-focused-smite-average-damage-6-attacks {:attacker     "Librarian smite focused witchfire average damage and 6 attacks "
                                                       :unit         "Librarian"
                                                       :attacks      6
                                                       :skill        3
                                                       :strength     6
                                                       :ap           -2
                                                       :damage       2
                                                       :devastating? true
                                                       :points       65})

(def defender (group-by :type
                        [{:defender   "Grey Knight Librarian"
                          :toughness  5
                          :wounds     5
                          :save       2
                          :invul-save 4
                          :type       :infantry}

                         {:defender  "Intercessor"
                          :toughness 4
                          :wounds    2
                          :save      3
                          :type      :infantry}

                         {:defender  "Cadian Shock Troops"
                          :toughness 3
                          :wounds    1
                          :save      5
                          :type      :infantry}

                         {:defender  "Kaskrin"
                          :toughness 3
                          :wounds    1
                          :save      4
                          :type      :infantry}

                         {:defender  "Boy"
                          :toughness 5
                          :wounds    1
                          :save      5
                          :type      :infantry}

                         {:defender  "Nob"
                          :toughness 5
                          :wounds    2
                          :save      4
                          :type      :infantry}

                         {:defender  "Immortal"
                          :toughness 5
                          :wounds    1
                          :save      3
                          :type      :infantry}

                         {:defender  "Necron Warrior"
                          :toughness 4
                          :wounds    1
                          :save      4
                          :type      :infantry}

                         {:defender  "Sternguard Veteran"
                          :toughness 4
                          :wounds    2
                          :save      3
                          :type      :infantry}

                         {:defender   "Intercessor with Librarian"
                          :toughness  4
                          :wounds     2
                          :save       3
                          :invul-save 4
                          :type       :infantry}

                         {:defender   "Sternguard Veteran with Librarian"
                          :toughness  4
                          :wounds     2
                          :save       3
                          :invul-save 4
                          :type       :infantry}

                         {:defender  "Termangants"
                          :toughness 3
                          :wounds    1
                          :save      5
                          :type      :infantry}

                         {:defender  "Battle Sister"
                          :toughness 3
                          :wounds    1
                          :save      3
                          :type      :infantry}

                         {:defender   "Terminator"
                          :toughness  5
                          :wounds     3
                          :save       2
                          :invul-save 4
                          :type       :infantry}

                         {:defender   "Custodian Guard"
                          :toughness  6
                          :wounds     3
                          :save       2
                          :invul-save 4
                          :type       :infantry}

                         {:defender   "Allarus Custodian"
                          :toughness  7
                          :wounds     4
                          :save       2
                          :invul-save 4
                          :type       :infantry}

                         {:defender   "Dire Aveneger"
                          :toughness  3
                          :wounds     1
                          :save       4
                          :invul-save 5
                          :type       :infantry}

                         {:defender   "Fire Dragon"
                          :toughness  3
                          :wounds     1
                          :save       3
                          :invul-save 5
                          :type       :infantry}

                         {:defender  "Ogryn"
                          :toughness 6
                          :wounds    3
                          :save      5
                          :type      :infantry}

                         {:defender  "Taurox"
                          :toughness 8
                          :wounds    10
                          :save      3
                          :type      :infantry}

                         {:defender   "Nemesis Dreadknight"
                          :toughness  8
                          :wounds     13
                          :save       2
                          :invul-save 4
                          :type       :vehicle}

                         {:defender  "Rhino"
                          :toughness 9
                          :wounds    10
                          :save      3
                          :type      :vehicle}

                         {:defender   "Knight Castellan"
                          :toughness  13
                          :wounds     24
                          :save       2
                          :invul-save 5
                          :type       :vehicle}

                         {:defender  "Castigator"
                          :toughness 10
                          :wounds    11
                          :save      3
                          :type      :vehicle}

                         {:defender   "Paragon Warsuit"
                          :toughness  7
                          :wounds     4
                          :save       2
                          :invul-save 4
                          :type       :vehicle}

                         {:defender   "Ravager"
                          :toughness  9
                          :wounds     11
                          :save       4
                          :invul-save 6
                          :type       :vehicle}

                         {:defender   "Deffkopta"
                          :toughness  6
                          :wounds     4
                          :save       4
                          :invul-save 6
                          :type       :vehicle}

                         {:defender   "Gorkanaut"
                          :toughness  12
                          :wounds     20
                          :save       2
                          :invul-save 6
                          :type       :vehicle}

                         {:defender   "Morkanaut"
                          :toughness  12
                          :wounds     20
                          :save       3
                          :invul-save 5
                          :type       :vehicle}

                         {:defender  "Gladiator"
                          :toughness 10
                          :wounds    12
                          :save      3
                          :type      :vehicle}

                         {:defender  "Repulsor"
                          :toughness 12
                          :wounds    16
                          :save      3
                          :type      :vehicle}

                         {:defender   "Reaver Titan"
                          :toughness  14
                          :wounds     60
                          :save       2
                          :invul-save 5
                          :type       :vehicle}

                         {:defender   "Trukk"
                          :toughness  8
                          :wounds     10
                          :save       4
                          :invul-save 6
                          :type       :vehicle}

                         {:defender  "Landraider"
                          :toughness 12
                          :wounds    16
                          :save      2
                          :type      :vehicle}

                         {:defender   "Armiger Helverin"
                          :toughness  10
                          :wounds     12
                          :save       3
                          :invul-save 5
                          :type       :vehicle}

                         {:defender   "Warhound"
                          :toughness  13
                          :wounds     40
                          :save       2
                          :invul-save 5
                          :type       :vehicle}

                         {:defender  "Leman Russ"
                          :toughness 11
                          :wounds    13
                          :save      2
                          :type      :vehicle}

                         {:defender  "Baneblade"
                          :toughness 13
                          :wounds    24
                          :save      2
                          :type      :vehicle}

                         {:defender  "Questoris"
                          :toughness 12
                          :wounds    22
                          :save      3
                          :type      :vehicle}

                         {:defender  "Falcon"
                          :toughness 9
                          :wounds    12
                          :save      3
                          :type      :vehicle}

                         {:defender  "Vyper"
                          :toughness 6
                          :wounds    6
                          :save      3
                          :type      :vehicle}

                         {:defender  "Wraithknight"
                          :toughness 12
                          :wounds    18
                          :save      2
                          :type      :monster}

                         {:defender  "Chimera"
                          :toughness 9
                          :wounds    11
                          :save      3
                          :type      :vehicle}

                         {:defender  "Sentinel"
                          :toughness 7
                          :wounds    7
                          :save      3
                          :type      :vehicle}

                         {:defender  "Falcon"
                          :toughness 9
                          :wounds    12
                          :save      3
                          :type      :vehicle}

                         {:defender   "Black Knights"
                          :toughness  5
                          :wounds     3
                          :save       3
                          :invul-save 5
                          :type       :infantry}

                         {:defender   "Hive Tyrant"
                          :toughness  10
                          :wounds     10
                          :save       2
                          :invul-save 4
                          :type       :monster}

                         {:defender     "Hive Tyrant guarded"
                          :toughness    10
                          :wounds       10
                          :save         2
                          :invul-save   4
                          :feel-no-pain 5
                          :type         :monster}

                         {:defender  "Tyranid Warrior"
                          :toughness 5
                          :wounds    3
                          :save      4
                          :type      :infantry}

                         {:defender  "Outrider"
                          :toughness 5
                          :wounds    4
                          :save      3
                          :type      :infantry}]))


(def attack-combinations (map #(merge (first %) (second %))
                              (concat
                                (combo/cartesian-product [ hellblaster-overcharged-sustained-lethal-S10 hellblaster-standart-sustained-lethal-S9
                                                          intercessor-160-bolt-rifle-sustained-lethal-S6 intercessor-standing-160-bolt-rifle-sustained-lethal-S6
                                                          terminator-340-stormbolter-sustained-s6 terminator-340-stormbolter-sustained-s6-12-inch
                                                          ]
                                                         (apply concat (vals defender))))))



(def attack-combinations2 (map #(merge (first %) (second %))
                              (concat
                                (combo/cartesian-product [outrider-160-melee outrider-160-charge outrider-160-heavy-bolt-pistol
                                                          outrider-160-bolter outrider-160-bolter-chaplain outrider-160-charge-chaplain
                                                          outrider-160-heavy-bolt-postol-chaplain outrider-160-melee-chaplain

                                                          black-knights-plasma-180-chaplain black-knights-plasma-180 black-knights-plasma-180 black-knights-plasma-charged-180
                                                          black-knights-plasma-charged-180-chaplain black-knights-180-chaplain-melee black-knights-180-melee

                                                          repulsor-executioner-marcro-plasma-charged repulsor-executioner-marcro-plasma-standart repulsor-180-defensive-array repulsor-180-onslaught

                                                          terminator-340-stormbolter-12-inch-oath terminator-340-stormbolter-sustained terminator-340-stormbolter-12-inch
                                                          terminator-340-stormbolter terminator-340-stormbolter-oath terminator-340-stormbolter-12-inch-pyro terminator-340-stormbolter-12-inc-oath-pyro
                                                          terminator-340-power-fist-sustained terminator-340-power-fist

                                                          inceptor-240-plasma-standart inceptor-240-plasma-supercharged
                                                          deathwing-knights-power-weapon-sustained deathwing-knights-power-weapon

                                                          intercessor-160-bolt-rifle-oath-pyro intercessor-160-bolt-rifle heavy-intercessor-220-heavy-bolt-rifle-oath-pyro heavy-intercessor-220-heavy-bolt-rifle
                                                          intercessor-160-bolt-rifle-standing-sustained-lethal intercessor-160-bolt-rifle-sustained-lethal intercessor-160-bolt-rifle-standing-lethal
                                                          intercessor-160-bolt-rifle-oath-pyro-lethal intercessor-160-bolt-rifle-lethal intercessor-160-bolt-rifle-oath-pyro-standing-lethal

                                                          librarian-focused-smite-average-damage-6-attacks librarian-smite-average-damage-6-attacks
                                                          librarian-focused-smite-average-damage-5-attacks librarian-smite-average-damage-5-attacks
                                                          librarian-focused-smite-average-damage-4-attacks librarian-smite-average-damage-4-attacks
                                                          librarian-focused-smite-average-damage-3-attacks librarian-smite-average-damage-3-attacks

                                                          assault-intercessor-150-chain-sword-objective-marker assault-intercessor-150-chain-sword
                                                          sternguard-veteran-heavy-bolter-standing sternguard-veteran-heavy-bolter-standing-oath
                                                          sternguard-veteran-bolt-rifle-standing-pyro-12-inch-oath sternguard-veteran-bolt-rifle-standing
                                                          sternguard-veteran-bolt-rifle
                                                          intercessor-160-bolt-rifle-oath-pyro-standing intercessor-160-bolt-rifle-standing heavy-intercessor-220-heavy-bolt-rifle-oath-pyro-standing heavy-intercessor-220-heavy-bolt-rifle-standing
                                                          armiger-helverin-autocannons armiger-helverin-melta-12-inch armiger-helverin-melta-6-inch armiger-helverin-questoris-heavy-stubber-36-inch armiger-helverin-questoris-heavy-stubber-18-inch

                                                          termagants-120-fleshborer termagants-120-spinefists hormagaunts-130

                                                          custodian-wardens-castellan-axe-melee custodian-wardens-castellan-axe-shoot
                                                          custodian-wardens-guardian-spear-melee custodian-wardens-guardian-spear-shoot

                                                          blade-champion-behemor blade-champion-hurricanis blade-champion-victus
                                                          ]
                                                         (apply concat (vals defender)))

                                (combo/cartesian-product [black-knights-180-charge-vehicles black-knights-180-charge-vehicles-chaplain
                                                          deathwing-knights-mace-of-absolution-normal deathwing-knights-mace-of-absolution-normal-sustained]
                                                         (:infantry defender))

                                (combo/cartesian-product [black-knights-180-charge-vehicles black-knights-180-charge-vehicles-chaplain
                                                          deathwing-knights-mace-of-absolution-vehicles-sustained deathwing-knights-mace-of-absolution-vehicles
                                                          sternguard-veteran-bolt-rifle-vehicle-librarian sternguard-veteran-bolt-rifle-vehicle-librarian-standing
                                                          sternguard-veteran-bolt-rifle-vehicle-librarian-standing-pyro-oath-12-inch sternguard-veteran-heavy-bolter-vehicle-librarian-standing
                                                          sternguard-veteran-heavy-bolter-vehicle-librarian sternguard-veteran-heavy-bolter-vehicle-librarian-standing-pyro-oath]
                                                         (concat (:vehicle defender) (:monster defender))))))

(def result
  (apply merge-with merge
         (map #(sorted-map
                 (:attacker %)
                 (sorted-map (:defender %)
                             (assoc (k/get-probabilities-for %) :type (:type %) :unit (:unit %))))
              attack-combinations)))

(defn round-if-number [x]
  (if (number? x) (Precision/round x 3) x))


(defn get-averages [key-val]
  (map #(postwalk round-if-number
                  (assoc (select-keys (val %) [:expected-wounds :expected-damage :expected-kills :attacks :points :damage :wounds-needed-to-kill :expected-wounds-to-hits :median-damage :median-wounds :median-kills :expected-point-kill-ratio :toughness :wounds :save :invul-save :type :unit])
                    :defender (key %)
                    :attacker (key key-val)))
       (val key-val)))

(comment
  (CSV/write-csv "calculations.csv" [:unit :attacker :defender :expected-wounds :expected-damage :expected-kills :expected-point-kill-ratio :expected-wounds-to-hits :attacks :wounds-needed-to-kill :damage :points :toughness :wounds :save :invul-save :type] (apply concat (map get-averages result)))

  )