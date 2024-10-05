(ns kilithikadya.da-sparing
  (:require [clojure.math.combinatorics :as combo]
            [kilithikadya.kilithikadya :as k]))

;; The Inner Circle squad is lead by Azrael and attacks a swaud lead by a character model. 
;; This results into a Bonus of +1 if this squad attacks the squad of Belial and a malus of -1 to attacks if the squad of Belial attacks them.
(def azrael {:defender "Azrael"
             :toughnesss 4
             :wounds 6
             :save 2
             :invul-save 4
             :attack-mod -1})
(def the-sword-of-secrets {:attacker "Azrael"
                           :attacks 6
                           :skill 2
                           :strength 6
                           :ap -4
                           :damage 2
                           :devastating? true
                           :sustained 1
                           :attack-mod 1})

(def inner-circle-companions {:defender       "Inner Circle Companions"
                              :toughnesss 4
                              :wounds     3
                              :save       3
                              :invul-save 4
                              :attack-mod -1})
(def caliban-greatsword-strike {:attacker    "Inner Circle Companions"
                                :attacks      24
                                :skill        3
                                :strength     6
                                :ap           -2
                                :damage       2
                                :lethal? true
                                :sustained    1
                                :attack-mod 1})


(def belial {:defender "Belial"
             :toughnesss 5
             :wounds 6
             :save 2
             :invul-save 4})
(def the-sword-of-silence {:attacker "Belial"
                           :attacks 6
                           :skill 2
                           :strength 6
                           :ap -2
                           :damage 2})

(def deathwing-knights {:defender "Deathwing Knights"
                        :toughnesss 5
                        :wounds 4
                        :save 2
                        :invul-save 4})
(def dwk-power-weapon {:attacker "Deathwing Knights"
                       :attacks   20
                       :skill     2
                       :strength  6
                       :ap        -2
                       :damage    2})

(def knight-master {:defender "Knight Master"
                        :toughnesss 5
                        :wounds 4
                        :save 2
                        :invul-save 4})
(def km-relic-weapon {:attacker "Knight Master"
                      :attacks  6
                      :skill    2
                      :strength 7
                      :ap       -2
                      :damage   2
                      :lethal?  true})

(def  attack-combinations (map #(merge (first %) (second %)) 
                               (concat 
                                (combo/cartesian-product [caliban-greatsword-strike the-sword-of-secrets] [belial deathwing-knights knight-master])
                                (combo/cartesian-product  [km-relic-weapon dwk-power-weapon the-sword-of-silence] [azrael inner-circle-companions])
                                )))

(def result 
  (apply merge-with merge 
         (pmap #(sorted-map 
                 (:attacker %) 
                 (sorted-map (:defender %) (k/get-probabilities-for %))) 
               attack-combinations)))
