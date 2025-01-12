(ns kilithikadya.dark-angels-tests
  (:require [clojure.math.combinatorics :as combo]
            [clojure.test :refer :all]
            [clojure.walk :refer [postwalk]]
            [kilithikadya.csv :as CSV]
            [kilithikadya.kilithikadya :as k])

  (:import (org.apache.commons.numbers.core Precision)))

(def repulsor-180-onslaught {:attacker     "Repulsor heavy onslaught cannon"
                             :attacks      12
                             :skill        3
                             :strength     6
                             :ap           0
                             :damage       1
                             :points       180
                             :devastating? true})

(def repulsor-180-defensive-array {:attacker "Repulsor defensive array"
                                   :attacks  18
                                   :skill    3
                                   :strength 4
                                   :ap       0
                                   :damage   1
                                   :points   180})

(def heavy-intercessor-220-heavy-bolt-rifle {:attacker "Heavy Intercessor Squad heavy bolt rifle"
                                             :attacks  20
                                             :skill    3
                                             :strength 5
                                             :ap       -1
                                             :damage   2
                                             :points   220})

(def heavy-intercessor-220-heavy-bolt-rifle-oath-pyro {:attacker       "Heavy Intercessor Squad heavy bolt rifle oath and pyro"
                                                       :attacks        20
                                                       :skill          3
                                                       :strength       5
                                                       :ap             -2
                                                       :damage         2
                                                       :points         285
                                                       :reroll-attack? true})

(def heavy-intercessor-220-heavy-bolt-rifle-standing {:attacker "Heavy Intercessor Squad heavy bolt rifle not moving"
                                                      :attacks  20
                                                      :skill    2
                                                      :strength 5
                                                      :ap       -1
                                                      :damage   2
                                                      :points   220})

(def heavy-intercessor-220-heavy-bolt-rifle-oath-pyro-standing {:attacker       "Heavy Intercessor Squad heavy bolt rifle oath and pyro not moving"
                                                                :attacks        20
                                                                :skill          2
                                                                :strength       5
                                                                :ap             -2
                                                                :damage         2
                                                                :points         285
                                                                :reroll-attack? true})

(def assault-intercessor-150-chain-sword {:attacker "Assault Intercessor Squad chain sword"
                                          :attacks  40
                                          :skill    3
                                          :strength 4
                                          :ap       -1
                                          :damage   1
                                          :points   150})

(def assault-intercessor-150-chain-sword-objective-marker {:attacker      "Assault Intercessor Squad chain sword against targets on objective marker"
                                                           :attacks       40
                                                           :skill         3
                                                           :strength      4
                                                           :ap            -1
                                                           :damage        1
                                                           :points        150
                                                           :reroll-wound? true})

(def intercessor-160-bolt-rifle {:attacker "Intercessor Squad bolt rifle"
                                 :attacks  40
                                 :skill    3
                                 :strength 4
                                 :ap       -1
                                 :damage   1
                                 :points   160})

(def intercessor-160-bolt-rifle-oath-pyro {:attacker       "Intercessor Squad bolt rifle oath and pyro"
                                           :attacks        40
                                           :skill          3
                                           :strength       4
                                           :ap             -2
                                           :damage         1
                                           :reroll-attack? true
                                           :points         225})


(def intercessor-160-bolt-rifle-standing {:attacker "Intercessor Squad bolt rifle not moving"
                                          :attacks  40
                                          :skill    2
                                          :strength 4
                                          :ap       -1
                                          :damage   1
                                          :points   160})

(def intercessor-160-bolt-rifle-oath-pyro-standing {:attacker       "Intercessor Squad bolt rifle oath and pyro not moving"
                                                    :attacks        40
                                                    :skill          2
                                                    :strength       4
                                                    :ap             -2
                                                    :damage         1
                                                    :reroll-attack? true
                                                    :points         225})

(def repulsor-executioner-marcro-plasma-standart {:attacker "Repulsor Executioner Macro plasma incinerator standart"
                                                  :attacks  5
                                                  :skill    3
                                                  :strength 8
                                                  :ap       -3
                                                  :damage   2
                                                  :points   220})

(def repulsor-executioner-marcro-plasma-charged {:attacker "Repulsor Executioner Macro plasma incinerator charged"
                                                 :attacks  5
                                                 :skill    3
                                                 :strength 9
                                                 :ap       -4
                                                 :damage   3
                                                 :points   220})

(def inceptor-240-plasma-standart {:attacker      "Inceptor Plasma exterminatores standart"
                                   :attacks       12
                                   :skill         3
                                   :strength      7
                                   :ap            -2
                                   :damage        2
                                   :reroll-wound? true
                                   :points        240})

(def inceptor-240-plasma-supercharged {:attacker      "Inceptor Plasma exterminatores supercharged"
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
                                          :attacks   30
                                          :skill     3
                                          :strength  8
                                          :ap        -2
                                          :damage    2
                                          :sustained 1
                                          :points    340})

(def terminator-340-stormbolter-sustained {:attacker  "Terminator Stormbolter sustained"
                                           :attacks   20
                                           :skill     3
                                           :strength  4
                                           :ap        0
                                           :damage    1
                                           :sustained 1
                                           :points    340})

(def terminator-340-stormbolter {:attacker "Terminator Stormbolter"
                                 :attacks  20
                                 :skill    3
                                 :strength 4
                                 :ap       0
                                 :damage   1
                                 :points   340})

(def terminator-340-stormbolter-12-inch {:attacker  "Terminator Stormbolter 12\" sustained"
                                         :attacks   40
                                         :skill     3
                                         :strength  4
                                         :ap        0
                                         :damage    1
                                         :sustained 1
                                         :points    340})

(def terminator-340-stormbolter-12-inch-pyro {:attacker  "Terminator Stormbolter 12\" sustained pyro"
                                              :attacks   40
                                              :skill     3
                                              :strength  4
                                              :ap        -1
                                              :damage    1
                                              :sustained 1
                                              :points    415})

(def terminator-340-stormbolter-oath {:attacker       "Terminator Stormbolter oath sustained"
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
                         :attacks  24
                         :skill    3
                         :strength 4
                         :ap       -1
                         :damage   1
                         :points   160})

(def outrider-160-melee-chaplain {:attacker  "Outrider melee with Chaplain"
                                  :attacks   24
                                  :skill     3
                                  :strength  4
                                  :ap        -1
                                  :damage    1
                                  :wound-mod 1
                                  :points    160})

(def outrider-160-bolter-chaplain {:attacker      "Outrider bolter with Chaplain"
                                   :attacks       12
                                   :skill         3
                                   :strength      4
                                   :ap            -1
                                   :damage        1
                                   :devastating?  true
                                   :reroll-wound? true
                                   :points        160})

(def outrider-160-bolter {:attacker      "Outrider bolter"
                          :attacks       12
                          :skill         3
                          :strength      4
                          :ap            -1
                          :damage        1
                          :reroll-wound? true
                          :points        160})

(def outrider-160-heavy-bolt-pistol {:attacker "Outrider heavy bolt pistol"
                                     :attacks  6
                                     :skill    3
                                     :strength 4
                                     :ap       -1
                                     :damage   1
                                     :points   160})
(def outrider-160-heavy-bolt-postol-chaplain {:attacker     "Outrider heavy bolt pistol with chaplain"
                                              :attacks      6
                                              :skill        3
                                              :strength     4
                                              :ap           -1
                                              :damage       1
                                              :devastating? true
                                              :points       160})


(def outrider-160-charge {:attacker "Outrider charge"
                          :attacks  24
                          :skill    3
                          :strength 5
                          :ap       -1
                          :damage   2
                          :points   160})

(def outrider-160-charge-chaplain {:attacker  "Outrider charge with chaplain"
                                   :attacks   24
                                   :skill     3
                                   :strength  5
                                   :ap        -1
                                   :damage    2
                                   :wound-mod 1
                                   :points    160})

(def black-knights-180-melee {:attacker     "Ravenwing Black Knights melee"
                              :attacks      18
                              :skill        3
                              :strength     5
                              :ap           -2
                              :damage       1
                              :devastating? true
                              :points       180})

(def black-knights-180-chaplain-melee {:attacker     "Ravenwing Black Knights melee with chaplain"
                                       :attacks      18
                                       :skill        3
                                       :strength     5
                                       :ap           -2
                                       :damage       1
                                       :devastating? true
                                       :wound-mod    1
                                       :points       180})

(def black-knights-plasma-180 {:attacker "Ravenwing Black Knights plasma "
                               :attacks  12
                               :skill    3
                               :strength 7
                               :ap       -2
                               :damage   1
                               :points   180})

(def black-knights-plasma-180-chaplain {:attacker     "Ravenwing Black Knights plasma with chaplain"
                                        :attacks      12
                                        :skill        3
                                        :strength     7
                                        :ap           -2
                                        :damage       1
                                        :devastating? true
                                        :points       180})

(def black-knights-plasma-charged-180 {:attacker "Ravenwing Black Knights plasma charged "
                                       :attacks  12
                                       :skill    3
                                       :strength 8
                                       :ap       -3
                                       :damage   2
                                       :points   180})

(def black-knights-plasma-charged-180-chaplain {:attacker     "Ravenwing Black Knights plasma charged with chaplain"
                                                :attacks      12
                                                :skill        3
                                                :strength     8
                                                :ap           -3
                                                :damage       2
                                                :devastating? true
                                                :points       180})

(def black-knights-180-charge-vehicles {:attacker     "Ravenwing Black Knights charge vehicles"
                                        :attacks      18
                                        :skill        3
                                        :strength     5
                                        :ap           -2
                                        :damage       1
                                        :anti         4
                                        :devastating? true
                                        :points       180})

(def black-knights-180-charge-vehicles-chaplain {:attacker     "Ravenwing Black Knights charge vehicles with chaplain"
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
                                                    :attacks  16
                                                    :skill    2
                                                    :strength 6
                                                    :ap       -2
                                                    :damage   2
                                                    :anti     4
                                                    :points   250})

(def deathwing-knights-mace-of-absolution-vehicles-sustained {:attacker  "Deathwing Knights Mace of absolution vehicles sustained"
                                                              :attacks   16
                                                              :skill     2
                                                              :strength  6
                                                              :ap        -2
                                                              :damage    2
                                                              :anti      4
                                                              :sustained 1
                                                              :points    250})

(def deathwing-knights-mace-of-absolution-normal {:attacker "Deathwing Knights Mace of absolution"
                                                  :attacks  16
                                                  :skill    2
                                                  :strength 6
                                                  :ap       -2
                                                  :damage   2
                                                  :points   250})

(def deathwing-knights-mace-of-absolution-normal-sustained {:attacker  "Deathwing Knights Mace of absolution sustained"
                                                            :attacks   16
                                                            :skill     2
                                                            :strength  6
                                                            :ap        -2
                                                            :damage    2
                                                            :sustained 1
                                                            :points    250})

(def deathwing-knights-power-weapon {:attacker "Deathwing Knights Power Weapon"
                                     :attacks  20
                                     :skill    2
                                     :strength 6
                                     :ap       -2
                                     :damage   2
                                     :points   250})

(def deathwing-knights-power-weapon-sustained {:attacker  "Deathwing Knights Power Weapon sustained"
                                               :attacks   20
                                               :skill     2
                                               :strength  6
                                               :ap        -2
                                               :damage    2
                                               :sustained 1
                                               :points    250})


(def sternguard-veteran-bolt-rifle {:attacker  "Sternguard bolt rifle"
                                    :attacks (* 8 2)
                                    :skill     3
                                    :strength  4
                                    :ap        -1
                                    :damage    1
                                    :devastating? true
                                    :points    160})


(def sternguard-veteran-bolt-rifle-vehicle-librarian {:attacker  "Sternguard bolt rifle against vehicle librarian"
                                                      :attacks (* 8 2)
                                                      :skill     3
                                                      :strength  4
                                                      :ap        -1
                                                      :damage    1
                                                      :devastating? true
                                                      :anti 5
                                                      :points    225})

(def sternguard-veteran-bolt-rifle-standing {:attacker  "Sternguard bolt rifle standing"
                                             :attacks (* 8 2)
                                             :skill     2
                                             :strength  4
                                             :ap        -1
                                             :damage    1
                                             :devastating? true
                                             :points    160})

(def sternguard-veteran-bolt-rifle-standing-pyro-12-inch-oath {:attacker  "Sternguard bolt rifle standing 12\" oath pyro"
                                                               :attacks (* 8 3)
                                                               :skill     2
                                                               :strength  4
                                                               :ap        -2
                                                               :damage    1
                                                               :devastating? true
                                                               :reroll-wound? true
                                                               :points    160})


(def sternguard-veteran-bolt-rifle-vehicle-librarian-standing {:attacker  "Sternguard bolt rifle against vehicle librarian standing"
                                                               :attacks (* 8 2)
                                                               :skill     2
                                                               :strength  4
                                                               :ap        -1
                                                               :damage    1
                                                               :devastating? true
                                                               :anti 5
                                                               :points    225})

(def sternguard-veteran-bolt-rifle-vehicle-librarian-standing-pyro-oath-12-inch {:attacker  "Sternguard bolt rifle against vehicle librarian standing pyro and oath 12\""
                                                                                 :attacks (* 8 3)
                                                                                 :skill     2
                                                                                 :strength  4
                                                                                 :ap        -2
                                                                                 :damage    1
                                                                                 :devastating? true
                                                                                 :reroll-wound? true
                                                                                 :anti 5
                                                                                 :points    225})


(def sternguard-veteran-heavy-bolter-standing {:attacker  "Sternguard heavy bolter standing"
                                               :attacks (* 2 3)
                                               :skill     3
                                               :strength  5
                                               :ap        -1
                                               :damage    2
                                               :devastating? true
                                               :sustained 1
                                               :points    40})

(def sternguard-veteran-heavy-bolter-standing-oath {:attacker  "Sternguard heavy bolter standing and oath"
                                                    :attacks (* 2 3)
                                                    :skill     3
                                                    :strength  5
                                                    :ap        -1
                                                    :damage    2
                                                    :devastating? true
                                                    :sustained 1
                                                    :reroll-wound? true
                                                    :points    40})


(def sternguard-veteran-heavy-bolter-vehicle-librarian-standing {:attacker  "Sternguard heavy bolter against vehicle librarian standing"
                                                                 :attacks (* 2 3)
                                                                 :skill     3
                                                                 :strength  5
                                                                 :ap        -1
                                                                 :damage    2
                                                                 :devastating? true
                                                                 :sustained 1
                                                                 :anti 5
                                                                 :points    105})

(def sternguard-veteran-heavy-bolter-standing {:attacker  "Sternguard heavy bolter standing"
                                               :attacks (* 2 3)
                                               :skill     3
                                               :strength  5
                                               :ap        -1
                                               :damage    2
                                               :devastating? true
                                               :sustained 1
                                               :points    40})


(def sternguard-veteran-heavy-bolter-vehicle-librarian {:attacker  "Sternguard heavy bolter against vehicle standing and  librarian"
                                                        :attacks (* 2 3)
                                                        :skill     3
                                                        :strength  5
                                                        :ap        -1
                                                        :damage    2
                                                        :devastating? true
                                                        :sustained 1
                                                        :anti 5
                                                        :points    105})

(def sternguard-veteran-heavy-bolter-vehicle-librarian-standing-pyro-oath {:attacker  "Sternguard heavy bolter against vehicle standing librarian pyro and oath"
                                                                           :attacks (* 2 3)
                                                                           :skill     3
                                                                           :strength  5
                                                                           :ap        -2
                                                                           :damage    2
                                                                           :devastating? true
                                                                           :sustained 1
                                                                           :anti 5
                                                                           :reroll-wound? true
                                                                           :points    105})

(def defender (group-by :type
                        [{:defender  "Intercessor"
                          :toughness 4
                          :wounds    2
                          :save      3
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

                         {:defender   "Terminator"
                          :toughness  5
                          :wounds     3
                          :save       2
                          :invul-save 4
                          :type       :infantry}

                         {:defender  "Rhino"
                          :toughness 9
                          :wounds    10
                          :save      3
                          :type      :vehicle}

                         {:defender   "Ravager"
                          :toughness  9
                          :wounds     11
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

                         {:defender   "Black Knights"
                          :toughness  5
                          :wounds     3
                          :save       3
                          :invul-save 5
                          :type       :infantry}

                         {:defender  "Hive Tyrant"
                          :toughness 10
                          :wounds    10
                          :save      2
                          :invul-save 4
                          :type      :monster}

                         {:defender  "Hive Tyrant guarded"
                          :toughness 10
                          :wounds    10
                          :save      2
                          :invul-save 4
                          :feel-no-pain 5
                          :type      :monster}

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
                                                         assault-intercessor-150-chain-sword-objective-marker assault-intercessor-150-chain-sword
                                                         sternguard-veteran-heavy-bolter-standing sternguard-veteran-heavy-bolter-standing-oath
                                                         sternguard-veteran-bolt-rifle-standing-pyro-12-inch-oath sternguard-veteran-bolt-rifle-standing
                                                         sternguard-veteran-bolt-rifle
                                                         intercessor-160-bolt-rifle-oath-pyro-standing intercessor-160-bolt-rifle-standing heavy-intercessor-220-heavy-bolt-rifle-oath-pyro-standing heavy-intercessor-220-heavy-bolt-rifle-standing]
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
                (sorted-map (:defender %) (k/get-probabilities-for %)))
              attack-combinations)))

(defn round-if-number [x]
  (if (number? x) (Precision/round x 3) x))


(defn get-averages [key-val]
  (map #(postwalk round-if-number
                  (assoc (select-keys (val %) [:expected-wounds :expected-damage :expected-kills :attacks :points :damage :wounds-needed-to-kill :expected-wounds-to-hits :median-damage :median-wounds :median-kills :expected-point-kill-ratio])
                         :defender (key %)
                         :attacker (key key-val)))
       (val key-val)))

(comment
  (CSV/write-csv "calculations.csv" [:attacker :defender :expected-wounds :expected-damage :expected-kills :expected-point-kill-ratio :expected-wounds-to-hits :attacks :wounds-needed-to-kill :damage :points] (apply concat (map get-averages result)))
  
  )