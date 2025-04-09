# kilithikadya

Kilithikadya (Near-future-to-come) means "The near-future" in Aledari. This Programm should allow to take a peek view into the near future of the possible effects of your decisions during a round of Warhammer 40k. Mainly about the possibilities of wounds and damages your units can dish out against other units. 
An example Spreadsheet with calculations can be found [here](https://docs.google.com/spreadsheets/d/1-BMw_JhIYTRAWCwF6bjdzlQ30lWMzRbW/edit?usp=sharing&ouid=106005814345395628856&rtpof=true&sd=true) .

## Usage

### Leiningen

![https://clojars.org/com.github.goldritter/kilithikadya](https://clojars.org/com.github.goldritter/kilithikadya/latest-version.svg)

### Example
First include somehow the namespace like
```clojure
(require '[kilithikadya.kilithikadya :as kilithikadya])
```
or 
```clojure
(ns <my-name-space>
 (:require [kilithikadya.kilithikadya :as kilithikadya]))
```
Then call 
```clojure
(get-probabilities-for <named arguments>)
```
The named arguments are: 
- **:anti**: Anti-Ability of the weapon e.g Anti-Infantry 4+. *Default: 0*
- **:ap**: Ap-Value of the weapon. *Default: 0*
- **:attack-mod**: The value by which the to hit roll is modified. *Default: 0*
- **:attacks**: The number of attacks. *Default: 1*
- **:damage**: The damage of the weapon. *Default: 1*
- **:devastating?**: If *true* then the weapon has the Devastating-Ability.
- **:feel-no-pain**: The feel no pain save value. *Default: 0*
- **:invul-save**: If the target has a invulnerbale save this value is any number between 2 and 6. *Default: 0*
- **:lethal?**: If *true* then the weapon has the Lethal-Ability. *Default: false*
- **:reroll-attack?**: If *true* then the attack roll can be rerolled if it fails. *Default: false*
- **:reroll-wound?**: If *true* then the wound roll can be rerolled if it fails. *Default: false*
- **:save**: The armor save value of the target. *Default: 6*
- **:save-mod**:  The modifier for the save roll of the target. *Default: 0*
- **:skill**: The ballistic or melee skill of the attacker. *Default: 4*
- **:strength**: The Strength of the weapon. *Default: 4*
- **:sustained**: The value of the Sustained-Ability of the weapon. If the weapon has none the value is 0. *Default: 0*
- **:toughness**: The tougness of the target. *Default: 4*
- **:wound-mod**: The modifier of the wound roll. *Default: 0*
- **:wounds**: The number of wounds of the target. *Default: 1*


## Examples
Say a squad of Inner Circle Companions lead by Azrael are sparring against a squad of Deathwing Knights by Belial and we want to know the probabilties who might be better. 

First we include the namespace and add the needed stats of the units and weapons.

```clojure
(ns kilithikadya.da-sparing
  (:use [kilithikadya.kilithikadya])
  (:require [clojure.math.combinatorics :as combo]))

;; The Inner Circle squad is lead by Azrael and attacks a sqaud lead by a character model. 
;; This results into a Bonus of +1 if this squad attacks the squad of Belial and a malus of -1 to attacks if the squad of Belial attacks them.
(def azrael {:defender   "Azrael"
             :toughness 4
             :wounds     6
             :save       2
             :invul-save 4
             :attack-mod -1})
(def the-sword-of-secrets {:attacker     "Azrael"
                           :attacks      6
                           :skill        2
                           :strength     6
                           :ap           -4
                           :damage       2
                           :devastating? true
                           :sustained    1
                           :attack-mod   1})

(def the-sword-of-secrets-vs-knights {:attacker     "Azrael vs. Deathwing Knights"
                                      :attacks      6
                                      :skill        2
                                      :strength     6
                                      :ap           -4
                                      :damage       1       ;; Damage is only 1 against Deathwing-Knights
                                      :devastating? true
                                      :sustained    1
                                      :attack-mod   1})

(def inner-circle-companions {:defender   "Inner Circle Companions"
                              :toughness 4
                              :wounds     3
                              :save       3
                              :invul-save 4
                              :attack-mod -1})
(def caliban-greatsword-strike-vs-knights {:attacker   "Inner Circle Companions vs. Deathwing Knights"
                                           :attacks    24
                                           :skill      3
                                           :strength   6
                                           :ap         -2
                                           :damage     1    ;; Damage is only 1 against Deathwing-Knights
                                           :lethal?    true
                                           :sustained  1
                                           :attack-mod 1})

(def caliban-greatsword-strike {:attacker   "Inner Circle Companions"
                                :attacks    24
                                :skill      3
                                :strength   6
                                :ap         -2
                                :damage     2
                                :lethal?    true
                                :sustained  1
                                :attack-mod 1})


(def belial {:defender   "Belial"
             :toughness 5
             :wounds     6
             :save       2
             :invul-save 4})
(def the-sword-of-silence {:attacker "Belial"
                           :attacks  6
                           :skill    2
                           :strength 6
                           :ap       -2
                           :damage   2})

(def deathwing-knights {:defender   "Deathwing Knights"
                        :toughness 5
                        :wounds     4
                        :save       2
                        :invul-save 4})

(def dwk-power-weapon {:attacker "Deathwing Knights"
                       :attacks  20
                       :skill    2
                       :strength 6
                       :ap       -2
                       :damage   2})

(def dwk-power-weapon-decimated {:attacker "2 Deathwing Knights"
                                 :attacks  10
                                 :skill    2
                                 :strength 6
                                 :ap       -2
                                 :damage   2})

(def knight-master {:defender   "Knight Master"
                    :toughness 5
                    :wounds     4
                    :save       2
                    :invul-save 4})
(def km-relic-weapon {:attacker "Knight Master"
                      :attacks  6
                      :skill    2
                      :strength 7
                      :ap       -2
                      :damage   2
                      :lethal?  true})
```
Then we create the combinations of attacks from Azrael's unit against Belial's unit.

```clojure
(def attack-combinations (map #(merge (first %) (second %))
                              (concat
                                (combo/cartesian-product [caliban-greatsword-strike the-sword-of-secrets] [belial])
                                (combo/cartesian-product [caliban-greatsword-strike-vs-knights the-sword-of-secrets-vs-knights] [deathwing-knights knight-master])
                                (combo/cartesian-product [km-relic-weapon dwk-power-weapon the-sword-of-silence] [azrael inner-circle-companions])
                                )))
```
One element in `attack-combinations` looks like this:
```clojure
{:skill 3,
 :attacker "Inner Circle Companions",
 :save 2,
 :lethal? true,
 :wounds 6,
 :attack-mod 1,
 :strength 6,
 :damage 2,
 :sustained 1,
 :invul-save 4,
 :ap -2,
 :attacks 24,
 :toughness 5,
 :defender "Belial"}
```
To have a nice workable collection of probabilites wie create a nested sorted map with the attacker as the first key with an sorted map with the defender as key and the probabilites as value.

```clojure
(def result 
  (apply merge-with merge 
         (pmap #(sorted-map 
                 (:attacker %) 
                 (sorted-map (:defender %) (get-probabilities-for %))) 
               attack-combinations)))
```
Now we can get for example the probabilities when the Inner Circle Companions attacks the Deathwing Knights with ` (get-in result ["Inner Circle Companions vs. Deathwing Knights" "Deathwing Knights"])` 

<details>
 <summary>Huge output </summary>

```clojure
(get-in result ["Inner Circle Companions vs. Deathwing Knights" "Deathwing Knights"])
=>
{:anti                          0,
 :attacks                       24,
 :average-critical-hits         4.000000000000001,
 :average-extra-hits            4.000000000000001,
 :average-hits                  16.0,
 :average-total-hits            20.0,
 :critical-wound-probabilities  {0  0.029534305728066198,
                                 1  0.10802403964496408,
                                 2  0.19128636559568815,
                                 3  0.21831836467263852,
                                 4  0.18035183026315824,
                                 5  0.11479164845875643,
                                 6  0.05849981304350965,
                                 7  0.024484632391076427,
                                 8  0.008564591604005186,
                                 9  0.0025342590612108637,
                                 10 6.395938998393368E-4,
                                 11 1.3840216242114167E-4,
                                 12 2.574925452888384E-5,
                                 13 4.121068980370532E-6,
                                 14 5.665046522599521E-7,
                                 15 6.663865972951313E-8,
                                 16 6.6666404255994505E-9,
                                 17 5.620962786399365E-10,
                                 18 3.94338111333306E-11,
                                 19 2.2607009082160784E-12,
                                 20 1.0320233964996463E-13,
                                 21 3.608815744028601E-15,
                                 22 9.078995695735278E-17,
                                 23 1.4631221044677432E-18,
                                 24 1.1345297680170546E-20,
                                 25 0.0,
                                 26 0.0,
                                 27 0.0,
                                 28 0.0,
                                 29 0.0,
                                 30 0.0,
                                 31 0.0,
                                 32 0.0,
                                 33 0.0,
                                 34 0.0,
                                 35 0.0,
                                 36 0.0,
                                 37 0.0,
                                 38 0.0,
                                 39 0.0,
                                 40 0.0,
                                 41 0.0,
                                 42 0.0,
                                 43 0.0,
                                 44 0.0,
                                 45 0.0,
                                 46 0.0,
                                 47 0.0,
                                 48 0.0},
 :critical-wound-probability    1/6,
 :damage                        1,
 :expected-damage               8.000244260405657,
 :expected-kills                2.000061065101414,
 :expected-wounds               8.000244260405657,
 :extra-hit-probabilites        {0  0.012579115212475307,
                                 1  0.0603797530198815,
                                 2  0.13887343194572746,
                                 3  0.2036810335204003,
                                 4  0.2138650851964204,
                                 5  0.17109206815713637,
                                 6  0.10835830983285308,
                                 7  0.055727130771181625,
                                 8  0.023684030577752164,
                                 9  0.008420988649867433,
                                 10 0.0025262965949602346,
                                 11 6.430573150807876E-4,
                                 12 1.3932908493417048E-4,
                                 13 2.5722292603231448E-5,
                                 14 4.04207455193638E-6,
                                 15 5.389432735915181E-7,
                                 16 6.063111827904574E-8,
                                 17 5.706458190969032E-9,
                                 18 4.438356370753653E-10,
                                 19 2.8031724446865138E-11,
                                 20 1.4015862223432628E-12,
                                 21 5.3393760851171796E-14,
                                 22 1.4561934777592385E-15,
                                 23 2.5325103961030215E-17,
                                 24 2.1104253300858578E-19},
 :hit-combinations              ((0 0)
                                 (0 1)
                                 (0 2)
                                 (0 3)
                                 (0 4)
                                 (0 5)
                                 (0 6)
                                 (0 7)
                                 (0 8)
                                 (0 9)
                                 (0 10)
                                 (0 11)
                                 (0 12)
                                 (0 13)
                                 (0 14)
                                 (0 15)
                                 (0 16)
                                 (0 17)
                                 (0 18)
                                 (0 19)
                                 (0 20)
                                 (0 21)
                                 (0 22)
                                 (0 23)
                                 (0 24)
                                 (1 0)
                                 (1 1)
                                 (1 2)
                                 (1 3)
                                 (1 4)
                                 (1 5)
                                 (1 6)
                                 (1 7)
                                 (1 8)
                                 (1 9)
                                 (1 10)
                                 (1 11)
                                 (1 12)
                                 (1 13)
                                 (1 14)
                                 (1 15)
                                 (1 16)
                                 (1 17)
                                 (1 18)
                                 (1 19)
                                 (1 20)
                                 (1 21)
                                 (1 22)
                                 (1 23)
                                 (1 24)
                                 (2 0)
                                 (2 1)
                                 (2 2)
                                 (2 3)
                                 (2 4)
                                 (2 5)
                                 (2 6)
                                 (2 7)
                                 (2 8)
                                 (2 9)
                                 (2 10)
                                 (2 11)
                                 (2 12)
                                 (2 13)
                                 (2 14)
                                 (2 15)
                                 (2 16)
                                 (2 17)
                                 (2 18)
                                 (2 19)
                                 (2 20)
                                 (2 21)
                                 (2 22)
                                 (2 23)
                                 (2 24)
                                 (3 0)
                                 (3 1)
                                 (3 2)
                                 (3 3)
                                 (3 4)
                                 (3 5)
                                 (3 6)
                                 (3 7)
                                 (3 8)
                                 (3 9)
                                 (3 10)
                                 (3 11)
                                 (3 12)
                                 (3 13)
                                 (3 14)
                                 (3 15)
                                 (3 16)
                                 (3 17)
                                 (3 18)
                                 (3 19)
                                 (3 20)
                                 (3 21)
                                 (3 22)
                                 (3 23)
                                 (3 24)
                                 (4 0)
                                 (4 1)
                                 (4 2)
                                 (4 3)
                                 (4 4)
                                 (4 5)
                                 (4 6)
                                 (4 7)
                                 (4 8)
                                 (4 9)
                                 (4 10)
                                 (4 11)
                                 (4 12)
                                 (4 13)
                                 (4 14)
                                 (4 15)
                                 (4 16)
                                 (4 17)
                                 (4 18)
                                 (4 19)
                                 (4 20)
                                 (4 21)
                                 (4 22)
                                 (4 23)
                                 (4 24)
                                 (5 0)
                                 (5 1)
                                 (5 2)
                                 (5 3)
                                 (5 4)
                                 (5 5)
                                 (5 6)
                                 (5 7)
                                 (5 8)
                                 (5 9)
                                 (5 10)
                                 (5 11)
                                 (5 12)
                                 (5 13)
                                 (5 14)
                                 (5 15)
                                 (5 16)
                                 (5 17)
                                 (5 18)
                                 (5 19)
                                 (5 20)
                                 (5 21)
                                 (5 22)
                                 (5 23)
                                 (5 24)
                                 (6 0)
                                 (6 1)
                                 (6 2)
                                 (6 3)
                                 (6 4)
                                 (6 5)
                                 (6 6)
                                 (6 7)
                                 (6 8)
                                 (6 9)
                                 (6 10)
                                 (6 11)
                                 (6 12)
                                 (6 13)
                                 (6 14)
                                 (6 15)
                                 (6 16)
                                 (6 17)
                                 (6 18)
                                 (6 19)
                                 (6 20)
                                 (6 21)
                                 (6 22)
                                 (6 23)
                                 (6 24)
                                 (7 0)
                                 (7 1)
                                 (7 2)
                                 (7 3)
                                 (7 4)
                                 (7 5)
                                 (7 6)
                                 (7 7)
                                 (7 8)
                                 (7 9)
                                 (7 10)
                                 (7 11)
                                 (7 12)
                                 (7 13)
                                 (7 14)
                                 (7 15)
                                 (7 16)
                                 (7 17)
                                 (7 18)
                                 (7 19)
                                 (7 20)
                                 (7 21)
                                 (7 22)
                                 (7 23)
                                 (7 24)
                                 (8 0)
                                 (8 1)
                                 (8 2)
                                 (8 3)
                                 (8 4)
                                 (8 5)
                                 (8 6)
                                 (8 7)
                                 (8 8)
                                 (8 9)
                                 (8 10)
                                 (8 11)
                                 (8 12)
                                 (8 13)
                                 (8 14)
                                 (8 15)
                                 (8 16)
                                 (8 17)
                                 (8 18)
                                 (8 19)
                                 (8 20)
                                 (8 21)
                                 (8 22)
                                 (8 23)
                                 (8 24)
                                 (9 0)
                                 (9 1)
                                 (9 2)
                                 (9 3)
                                 (9 4)
                                 (9 5)
                                 (9 6)
                                 (9 7)
                                 (9 8)
                                 (9 9)
                                 (9 10)
                                 (9 11)
                                 (9 12)
                                 (9 13)
                                 (9 14)
                                 (9 15)
                                 (9 16)
                                 (9 17)
                                 (9 18)
                                 (9 19)
                                 (9 20)
                                 (9 21)
                                 (9 22)
                                 (9 23)
                                 (9 24)
                                 (10 0)
                                 (10 1)
                                 (10 2)
                                 (10 3)
                                 (10 4)
                                 (10 5)
                                 (10 6)
                                 (10 7)
                                 (10 8)
                                 (10 9)
                                 (10 10)
                                 (10 11)
                                 (10 12)
                                 (10 13)
                                 (10 14)
                                 (10 15)
                                 (10 16)
                                 (10 17)
                                 (10 18)
                                 (10 19)
                                 (10 20)
                                 (10 21)
                                 (10 22)
                                 (10 23)
                                 (10 24)
                                 (11 0)
                                 (11 1)
                                 (11 2)
                                 (11 3)
                                 (11 4)
                                 (11 5)
                                 (11 6)
                                 (11 7)
                                 (11 8)
                                 (11 9)
                                 (11 10)
                                 (11 11)
                                 (11 12)
                                 (11 13)
                                 (11 14)
                                 (11 15)
                                 (11 16)
                                 (11 17)
                                 (11 18)
                                 (11 19)
                                 (11 20)
                                 (11 21)
                                 (11 22)
                                 (11 23)
                                 (11 24)
                                 (12 0)
                                 (12 1)
                                 (12 2)
                                 (12 3)
                                 (12 4)
                                 (12 5)
                                 (12 6)
                                 (12 7)
                                 (12 8)
                                 (12 9)
                                 (12 10)
                                 (12 11)
                                 (12 12)
                                 (12 13)
                                 (12 14)
                                 (12 15)
                                 (12 16)
                                 (12 17)
                                 (12 18)
                                 (12 19)
                                 (12 20)
                                 (12 21)
                                 (12 22)
                                 (12 23)
                                 (12 24)
                                 (13 0)
                                 (13 1)
                                 (13 2)
                                 (13 3)
                                 (13 4)
                                 (13 5)
                                 (13 6)
                                 (13 7)
                                 (13 8)
                                 (13 9)
                                 (13 10)
                                 (13 11)
                                 (13 12)
                                 (13 13)
                                 (13 14)
                                 (13 15)
                                 (13 16)
                                 (13 17)
                                 (13 18)
                                 (13 19)
                                 (13 20)
                                 (13 21)
                                 (13 22)
                                 (13 23)
                                 (13 24)
                                 (14 0)
                                 (14 1)
                                 (14 2)
                                 (14 3)
                                 (14 4)
                                 (14 5)
                                 (14 6)
                                 (14 7)
                                 (14 8)
                                 (14 9)
                                 (14 10)
                                 (14 11)
                                 (14 12)
                                 (14 13)
                                 (14 14)
                                 (14 15)
                                 (14 16)
                                 (14 17)
                                 (14 18)
                                 (14 19)
                                 (14 20)
                                 (14 21)
                                 (14 22)
                                 (14 23)
                                 (14 24)
                                 (15 0)
                                 (15 1)
                                 (15 2)
                                 (15 3)
                                 (15 4)
                                 (15 5)
                                 (15 6)
                                 (15 7)
                                 (15 8)
                                 (15 9)
                                 (15 10)
                                 (15 11)
                                 (15 12)
                                 (15 13)
                                 (15 14)
                                 (15 15)
                                 (15 16)
                                 (15 17)
                                 (15 18)
                                 (15 19)
                                 (15 20)
                                 (15 21)
                                 (15 22)
                                 (15 23)
                                 (15 24)
                                 (16 0)
                                 (16 1)
                                 (16 2)
                                 (16 3)
                                 (16 4)
                                 (16 5)
                                 (16 6)
                                 (16 7)
                                 (16 8)
                                 (16 9)
                                 (16 10)
                                 (16 11)
                                 (16 12)
                                 (16 13)
                                 (16 14)
                                 (16 15)
                                 (16 16)
                                 (16 17)
                                 (16 18)
                                 (16 19)
                                 (16 20)
                                 (16 21)
                                 (16 22)
                                 (16 23)
                                 (16 24)
                                 (17 0)
                                 (17 1)
                                 (17 2)
                                 (17 3)
                                 (17 4)
                                 (17 5)
                                 (17 6)
                                 (17 7)
                                 (17 8)
                                 (17 9)
                                 (17 10)
                                 (17 11)
                                 (17 12)
                                 (17 13)
                                 (17 14)
                                 (17 15)
                                 (17 16)
                                 (17 17)
                                 (17 18)
                                 (17 19)
                                 (17 20)
                                 (17 21)
                                 (17 22)
                                 (17 23)
                                 (17 24)
                                 (18 0)
                                 (18 1)
                                 (18 2)
                                 (18 3)
                                 (18 4)
                                 (18 5)
                                 (18 6)
                                 (18 7)
                                 (18 8)
                                 (18 9)
                                 (18 10)
                                 (18 11)
                                 (18 12)
                                 (18 13)
                                 (18 14)
                                 (18 15)
                                 (18 16)
                                 (18 17)
                                 (18 18)
                                 (18 19)
                                 (18 20)
                                 (18 21)
                                 (18 22)
                                 (18 23)
                                 (18 24)
                                 (19 0)
                                 (19 1)
                                 (19 2)
                                 (19 3)
                                 (19 4)
                                 (19 5)
                                 (19 6)
                                 (19 7)
                                 (19 8)
                                 (19 9)
                                 (19 10)
                                 (19 11)
                                 (19 12)
                                 (19 13)
                                 (19 14)
                                 (19 15)
                                 (19 16)
                                 (19 17)
                                 (19 18)
                                 (19 19)
                                 (19 20)
                                 (19 21)
                                 (19 22)
                                 (19 23)
                                 (19 24)
                                 (20 0)
                                 (20 1)
                                 (20 2)
                                 (20 3)
                                 (20 4)
                                 (20 5)
                                 (20 6)
                                 (20 7)
                                 (20 8)
                                 (20 9)
                                 (20 10)
                                 (20 11)
                                 (20 12)
                                 (20 13)
                                 (20 14)
                                 (20 15)
                                 (20 16)
                                 (20 17)
                                 (20 18)
                                 (20 19)
                                 (20 20)
                                 (20 21)
                                 (20 22)
                                 (20 23)
                                 (20 24)
                                 (21 0)
                                 (21 1)
                                 (21 2)
                                 (21 3)
                                 (21 4)
                                 (21 5)
                                 (21 6)
                                 (21 7)
                                 (21 8)
                                 (21 9)
                                 (21 10)
                                 (21 11)
                                 (21 12)
                                 (21 13)
                                 (21 14)
                                 (21 15)
                                 (21 16)
                                 (21 17)
                                 (21 18)
                                 (21 19)
                                 (21 20)
                                 (21 21)
                                 (21 22)
                                 (21 23)
                                 (21 24)
                                 (22 0)
                                 (22 1)
                                 (22 2)
                                 (22 3)
                                 (22 4)
                                 (22 5)
                                 (22 6)
                                 (22 7)
                                 (22 8)
                                 (22 9)
                                 (22 10)
                                 (22 11)
                                 (22 12)
                                 (22 13)
                                 (22 14)
                                 (22 15)
                                 (22 16)
                                 (22 17)
                                 (22 18)
                                 (22 19)
                                 (22 20)
                                 (22 21)
                                 (22 22)
                                 (22 23)
                                 (22 24)
                                 (23 0)
                                 (23 1)
                                 (23 2)
                                 (23 3)
                                 (23 4)
                                 (23 5)
                                 (23 6)
                                 (23 7)
                                 (23 8)
                                 (23 9)
                                 (23 10)
                                 (23 11)
                                 (23 12)
                                 (23 13)
                                 (23 14)
                                 (23 15)
                                 (23 16)
                                 (23 17)
                                 (23 18)
                                 (23 19)
                                 (23 20)
                                 (23 21)
                                 (23 22)
                                 (23 23)
                                 (23 24)
                                 (24 0)
                                 (24 1)
                                 (24 2)
                                 (24 3)
                                 (24 4)
                                 (24 5)
                                 (24 6)
                                 (24 7)
                                 (24 8)
                                 (24 9)
                                 (24 10)
                                 (24 11)
                                 (24 12)
                                 (24 13)
                                 (24 14)
                                 (24 15)
                                 (24 16)
                                 (24 17)
                                 (24 18)
                                 (24 19)
                                 (24 20)
                                 (24 21)
                                 (24 22)
                                 (24 23)
                                 (24 24)),
 :hit-probabilities             {0  3.5407061614721307E-12,
                                 1  1.6995389575066338E-10,
                                 2  3.908939602265244E-9,
                                 3  5.733111416655696E-8,
                                 4  6.019766987488477E-7,
                                 5  4.8158135899907835E-6,
                                 6  3.0500152736608198E-5,
                                 7  1.5685792835969968E-4,
                                 8  6.666461955287248E-4,
                                 9  0.00237029758410213,
                                 10 0.00711089275230638,
                                 11 0.018100454278598077,
                                 12 0.0392176509369625,
                                 13 0.07240181711439242,
                                 14 0.11377428403690253,
                                 15 0.15169904538253662,
                                 16 0.17066142605535367,
                                 17 0.16062251864033292,
                                 18 0.12492862560914782,
                                 19 0.07890228985840916,
                                 20 0.039451144929204617,
                                 21 0.015029007592077972,
                                 22 0.004098820252384895,
                                 23 7.128383047625925E-4,
                                 24 5.9403192063549296E-5},
 :to-wound-hits-probability-map {0  4.4538950738679127E-14,
                                 1  2.351656599002272E-12,
                                 2  5.992448588184867E-11,
                                 3  9.815187535809062E-10,
                                 4  1.1612194238915967E-8,
                                 5  1.057207768153518E-7,
                                 6  7.705838482477698E-7,
                                 7  4.619073964547962E-6,
                                 8  2.3212396865582065E-5,
                                 9  9.92032425207862E-5,
                                 10 3.645105353035257E-4,
                                 11 0.001161317733697532,
                                 12 0.00322952061003025,
                                 13 0.00788052665873247,
                                 14 0.016943845851840108,
                                 15 0.03220613213084916,
                                 16 0.05425735748598997,
                                 17 0.08117943740474312,
                                 18 0.10803652843631759,
                                 19 0.12803722100937934,
                                 20 0.13524238861694923,
                                 21 0.12739832302561688,
                                 22 0.1070701674763138,
                                 23 0.08030480980749755,
                                 24 0.05375834680542328},
 :lethal-hits-probabilities     {0  0.012579115212475307,
                                 1  0.0603797530198815,
                                 2  0.13887343194572746,
                                 3  0.2036810335204003,
                                 4  0.2138650851964204,
                                 5  0.17109206815713637,
                                 6  0.10835830983285308,
                                 7  0.055727130771181625,
                                 8  0.023684030577752164,
                                 9  0.008420988649867433,
                                 10 0.0025262965949602346,
                                 11 6.430573150807876E-4,
                                 12 1.3932908493417048E-4,
                                 13 2.5722292603231448E-5,
                                 14 4.04207455193638E-6,
                                 15 5.389432735915181E-7,
                                 16 6.063111827904574E-8,
                                 17 5.706458190969032E-9,
                                 18 4.438356370753653E-10,
                                 19 2.8031724446865138E-11,
                                 20 1.4015862223432628E-12,
                                 21 5.3393760851171796E-14,
                                 22 1.4561934777592385E-15,
                                 23 2.5325103961030215E-17,
                                 24 2.1104253300858578E-19},
 :maximal-hits                  48,
 :min-wound-probability         {0  0.9371983572626935,
                                 1  0.9371231137273489,
                                 2  0.9363378283930793,
                                 3  0.932326164721686,
                                 4  0.9189567280050108,
                                 5  0.8862726020661679,
                                 6  0.8237857808573298,
                                 7  0.7265269680372763,
                                 8  0.5998523287128482,
                                 9  0.45902638553031905,
                                 10 0.3233944549545318,
                                 11 0.20892433586227607,
                                 12 0.12350396355755715,
                                 13 0.06674280497537481,
                                 14 0.03296587669297844,
                                 15 0.01488435284373774,
                                 16 0.006145242921339743,
                                 17 0.002320826302135867,
                                 18 8.019899179075293E-4,
                                 19 2.536318726646776E-4,
                                 20 7.34142693936136E-5,
                                 21 1.9447945501650944E-5,
                                 22 4.71408742649087E-6,
                                 23 1.0452110426543838E-6,
                                 24 2.1187556964141232E-7,
                                 25 3.9241943315102576E-8,
                                 26 6.635378798110976E-9,
                                 27 1.0233046642919919E-9,
                                 28 1.437689359972326E-10,
                                 29 1.837603364719137E-11,
                                 30 2.133375779479219E-12,
                                 31 2.245405501665139E-13,
                                 32 2.1378527832377962E-14,
                                 33 1.8365203705274072E-15,
                                 34 1.4191610554394102E-16,
                                 35 9.82950206702545E-18,
                                 36 6.076467275995848E-19,
                                 37 3.3356987861415154E-20,
                                 38 1.616148369742387E-21,
                                 39 6.859528576006588E-23,
                                 40 2.5270822350174124E-24,
                                 41 7.987545774588095E-26,
                                 42 2.1339377955742318E-27,
                                 43 4.723972342193612E-29,
                                 44 8.43112682213607E-31,
                                 45 1.1654933637657814E-32,
                                 46 1.1707522474464503E-34,
                                 47 7.60158608634244E-37,
                                 48 2.3943403601596145E-39},
 :pass-save-test-probability    1/2,
 :received-damage-probability   {0  7.524353534458952E-5,
                                 1  7.852853342697263E-4,
                                 2  0.004011663671393413,
                                 3  0.01336943671667502,
                                 4  0.03268412593884288,
                                 5  0.06248682120883812,
                                 6  0.09725881282005346,
                                 7  0.1266746393244282,
                                 8  0.14082594318252922,
                                 9  0.13563193057578726,
                                 10 0.11447011909225561,
                                 11 0.08542037230471898,
                                 12 0.05676115858218232,
                                 13 0.033776928282396375,
                                 14 0.018081523849240694,
                                 15 0.008739109922397997,
                                 16 0.0038244166192038754,
                                 17 0.0015188363842283373,
                                 18 5.483580452428521E-4,
                                 19 1.8021760327106387E-4,
                                 20 5.396632389196268E-5,
                                 21 1.4733858075160074E-5,
                                 22 3.668876383836487E-6,
                                 23 8.333354730129716E-7,
                                 24 1.7263362632630973E-7,
                                 25 3.260656451699161E-8,
                                 26 5.612074133818984E-9,
                                 27 8.795357282947595E-10,
                                 28 1.2539290235004123E-10,
                                 29 1.6242657867712155E-11,
                                 30 1.908835229312705E-12,
                                 31 2.031620223341359E-13,
                                 32 1.954200746185056E-14,
                                 33 1.6946042649834663E-15,
                                 34 1.320866034769156E-16,
                                 35 9.221855339425866E-18,
                                 36 5.742897397381696E-19,
                                 37 3.174083949167277E-20,
                                 38 1.547553083982321E-21,
                                 39 6.606820352504848E-23,
                                 40 2.447206777271531E-24,
                                 41 7.774151995030671E-26,
                                 42 2.086698072152296E-27,
                                 43 4.639661073972251E-29,
                                 44 8.314577485759493E-31,
                                 45 1.153785841291317E-32,
                                 46 1.163150661360108E-34,
                                 47 7.577642682740844E-37,
                                 48 2.3943403601596145E-39},
 :received-wounds-probability   {0  7.524353534458952E-5,
                                 1  7.852853342697263E-4,
                                 2  0.004011663671393413,
                                 3  0.01336943671667502,
                                 4  0.03268412593884288,
                                 5  0.06248682120883812,
                                 6  0.09725881282005346,
                                 7  0.1266746393244282,
                                 8  0.14082594318252922,
                                 9  0.13563193057578726,
                                 10 0.11447011909225561,
                                 11 0.08542037230471898,
                                 12 0.05676115858218232,
                                 13 0.033776928282396375,
                                 14 0.018081523849240694,
                                 15 0.008739109922397997,
                                 16 0.0038244166192038754,
                                 17 0.0015188363842283373,
                                 18 5.483580452428521E-4,
                                 19 1.8021760327106387E-4,
                                 20 5.396632389196268E-5,
                                 21 1.4733858075160074E-5,
                                 22 3.668876383836487E-6,
                                 23 8.333354730129716E-7,
                                 24 1.7263362632630973E-7,
                                 25 3.260656451699161E-8,
                                 26 5.612074133818984E-9,
                                 27 8.795357282947595E-10,
                                 28 1.2539290235004123E-10,
                                 29 1.6242657867712155E-11,
                                 30 1.908835229312705E-12,
                                 31 2.031620223341359E-13,
                                 32 1.954200746185056E-14,
                                 33 1.6946042649834663E-15,
                                 34 1.320866034769156E-16,
                                 35 9.221855339425866E-18,
                                 36 5.742897397381696E-19,
                                 37 3.174083949167277E-20,
                                 38 1.547553083982321E-21,
                                 39 6.606820352504848E-23,
                                 40 2.447206777271531E-24,
                                 41 7.774151995030671E-26,
                                 42 2.086698072152296E-27,
                                 43 4.639661073972251E-29,
                                 44 8.314577485759493E-31,
                                 45 1.153785841291317E-32,
                                 46 1.163150661360108E-34,
                                 47 7.577642682740844E-37,
                                 48 2.3943403601596145E-39},
 :strength                      6,
 :to-hit-critical-probability   1/6,
 :to-hit-probability            2/3,
 :toughness                     5,
 :wound-probabilities           {0  5.564792603820553E-10,
                                 1  1.5024923744512185E-8,
                                 2  1.9819083463864345E-7,
                                 3  1.7024203176299108E-6,
                                 4  1.0709731542013054E-5,
                                 5  5.261510428843557E-5,
                                 6  2.1020936631267233E-4,
                                 7  7.022570811832474E-4,
                                 8  0.0020019411939502423,
                                 9  0.004945427836840113,
                                 10 0.010714807300319365,
                                 11 0.020557744795691436,
                                 12 0.03520239454324375,
                                 13 0.0541442350174949,
                                 14 0.07519482643259927,
                                 15 0.09469343053683113,
                                 16 0.10849338878796132,
                                 17 0.1133806549481585,
                                 18 0.10826667545615365,
                                 19 0.09456401695365355,
                                 20 0.0755784890897281,
                                 21 0.05526097316236545,
                                 22 0.03693787229488353,
                                 23 0.0225456977307376,
                                 24 0.012547681127551946,
                                 25 0.006356902270779251,
                                 26 0.0029263253032439616,
                                 27 0.0012217149131430738,
                                 28 4.616767007932889E-4,
                                 29 1.5759944568986812E-4,
                                 30 4.8497194141540605E-5,
                                 31 1.3423671376810727E-5,
                                 32 3.3341991231274976E-6,
                                 33 7.412109021512893E-7,
                                 34 1.4704059400477076E-7,
                                 35 2.5941407659961642E-8,
                                 36 4.053827378538718E-9,
                                 37 5.584296162467057E-10,
                                 38 6.741867586037634E-11,
                                 39 7.082782316504854E-12,
                                 40 6.417721976480387E-13,
                                 41 4.95926322163999E-14,
                                 42 3.2208148998349264E-15,
                                 43 1.7240646415330814E-16,
                                 44 7.403125375222426E-18,
                                 45 2.4506253048737266E-19,
                                 46 5.868820920684746E-21,
                                 47 9.047111435158801E-23,
                                 48 6.739468971133112E-25},
 :wound-probability             2/3,
 :wounds-by-failed-save         {0  7.524353534458952E-5,
                                 1  7.852853342697263E-4,
                                 2  0.004011663671393413,
                                 3  0.01336943671667502,
                                 4  0.03268412593884288,
                                 5  0.06248682120883812,
                                 6  0.09725881282005346,
                                 7  0.1266746393244282,
                                 8  0.14082594318252922,
                                 9  0.13563193057578726,
                                 10 0.11447011909225561,
                                 11 0.08542037230471898,
                                 12 0.05676115858218232,
                                 13 0.033776928282396375,
                                 14 0.018081523849240694,
                                 15 0.008739109922397997,
                                 16 0.0038244166192038754,
                                 17 0.0015188363842283373,
                                 18 5.483580452428521E-4,
                                 19 1.8021760327106387E-4,
                                 20 5.396632389196268E-5,
                                 21 1.4733858075160074E-5,
                                 22 3.668876383836487E-6,
                                 23 8.333354730129716E-7,
                                 24 1.7263362632630973E-7,
                                 25 3.260656451699161E-8,
                                 26 5.612074133818984E-9,
                                 27 8.795357282947595E-10,
                                 28 1.2539290235004123E-10,
                                 29 1.6242657867712155E-11,
                                 30 1.908835229312705E-12,
                                 31 2.031620223341359E-13,
                                 32 1.954200746185056E-14,
                                 33 1.6946042649834663E-15,
                                 34 1.320866034769156E-16,
                                 35 9.221855339425866E-18,
                                 36 5.742897397381696E-19,
                                 37 3.174083949167277E-20,
                                 38 1.547553083982321E-21,
                                 39 6.606820352504848E-23,
                                 40 2.447206777271531E-24,
                                 41 7.774151995030671E-26,
                                 42 2.086698072152296E-27,
                                 43 4.639661073972251E-29,
                                 44 8.314577485759493E-31,
                                 45 1.153785841291317E-32,
                                 46 1.163150661360108E-34,
                                 47 7.577642682740844E-37,
                                 48 2.3943403601596145E-39},
 :wounds-needed-to-kill         1/4}

```
</details>

But this might be to much, we are only interested in the average wounds, damage and kill as well the possibilities for a minimum ammount of wounds.

```clojure
=> (-> 
    (get-in result ["Inner Circle Companions vs. Deathwing Knights" "Deathwing Knights"]) 
    (select-keys [:expected-wounds :expected-damage :expected-kills :min-wound-probability]))

{:expected-wounds 8.000244260405657,
 :expected-damage 8.000244260405657,
 :expected-kills 2.000061065101414,
 :min-wound-probability {0 0.9371983572626935,
                         1 0.9371231137273489,
                         2 0.9363378283930793,
                         3 0.932326164721686,
                         4 0.9189567280050108,
                         5 0.8862726020661679,
                         6 0.8237857808573298,
                         7 0.7265269680372763,
                         8 0.5998523287128482,
                         9 0.45902638553031905,
                         10 0.3233944549545318,
                         11 0.20892433586227607,
                         12 0.12350396355755715,
                         13 0.06674280497537481,
                         14 0.03296587669297844,
                         15 0.01488435284373774,
                         16 0.006145242921339743,
                         17 0.002320826302135867,
                         18 8.019899179075293E-4,
                         19 2.536318726646776E-4,
                         20 7.34142693936136E-5,
                         21 1.9447945501650944E-5,
                         22 4.71408742649087E-6,
                         23 1.0452110426543838E-6,
                         24 2.1187556964141232E-7,
                         25 3.9241943315102576E-8,
                         26 6.635378798110976E-9,
                         27 1.0233046642919919E-9,
                         28 1.437689359972326E-10,
                         29 1.837603364719137E-11,
                         30 2.133375779479219E-12,
                         31 2.245405501665139E-13,
                         32 2.1378527832377962E-14,
                         33 1.8365203705274072E-15,
                         34 1.4191610554394102E-16,
                         35 9.82950206702545E-18,
                         36 6.076467275995848E-19,
                         37 3.3356987861415154E-20,
                         38 1.616148369742387E-21,
                         39 6.859528576006588E-23,
                         40 2.5270822350174124E-24,
                         41 7.987545774588095E-26,
                         42 2.1339377955742318E-27,
                         43 4.723972342193612E-29,
                         44 8.43112682213607E-31,
                         45 1.1654933637657814E-32,
                         46 1.1707522474464503E-34,
                         47 7.60158608634244E-37,
                         48 2.3943403601596145E-39}}

```

But nicer to read is a map with only the effects of the Inner Circle Companions against Belial's squad.
```clojure
=> (apply merge
          (pmap
            #(sorted-map (str (first %) " vs. " (second %))
                         (->
                           (get-in result %)
                           (select-keys [:expected-wounds :expected-damage :expected-kills])))
            [["Inner Circle Companions" "Belial"]
             ["Inner Circle Companions vs. Deathwing Knights" "Deathwing Knights"]
             ["Inner Circle Companions vs. Deathwing Knights" "Knight Master"]]))

{"Inner Circle Companions vs. Belial"                                  {:expected-wounds 8.000244260405657, :expected-damage 16.000488520811313, :expected-kills 2.6667480868018854},
 "Inner Circle Companions vs. Deathwing Knights vs. Deathwing Knights" {:expected-wounds 8.000244260405657, :expected-damage 8.000244260405657, :expected-kills 2.000061065101414},
 "Inner Circle Companions vs. Deathwing Knights vs. Knight Master"     {:expected-wounds 8.000244260405657, :expected-damage 8.000244260405657, :expected-kills 2.000061065101414}}
```
So as it seems, the Inner Circle Companions will on average kill 2 of the Deathwing Knights and Belial and the Knight Master will remains unharmed. 
Then we will take a look how much damage the remainer of Belials squad might decimate the Inner Circle Companions. Therefore we calculate the stats anew for the Deathwing Knights with 

```clojure
(def dwk-power-weapon-decimated {:attacker "2 Deathwing Knights"
                                 :attacks  10
                                 :skill    2
                                 :strength 6
                                 :ap       -2
                                 :damage   2})
```

```clojure
(apply merge
       (pmap
         #(sorted-map (str (first %) " vs. " (second %))
                      (->
                        (get-in result %)
                        (select-keys [:average-wounds :average-damage :average-kills])))
         [["Belial" "Inner Circle Companions"]
          ["Knight Master" "Inner Circle Companions"]
          ["2 Deathwing Knights" "Inner Circle Companions"]]))
=>
{"2 Deathwing Knights vs. Inner Circle Companions" {:average-wounds 2.2222222222222228, :average-damage 4.4444444444444455, :average-kills 1.4814814814814818},
 "Belial vs. Inner Circle Companions" {:average-wounds 1.3333333333333335, :average-damage 2.666666666666667, :average-kills 0.888888888888889},
 "Knight Master vs. Inner Circle Companions" {:average-wounds 0.45244981518675836, :average-damage 0.9048996303735167, :average-kills 0.30163321012450556}}

```
If we calculate the averages then the remaining 2 Deathwing Knights, the Knight Master and Belial might kill 2 Inner Circle Companions and wound one.

## What you should know
This project is work in progress and in an actuall alpha version. 
Planned changes are acutally more probability statistics and features. 
The keywords in the returned map **should** be fix, except the naming is horrible wrong and causes more trouble. 

## Bugs
The project is not mature and in constant changes. So bugs can and will be occur. 😞

## To-Do
- [] Better documentation.
- [] Feel no pain calculation.
- [] Reroll if roll a 1 
- [] Find a practicable way to handle rounding errors for multiple additions with doubles. 

## Disclaimer
This project is completely unofficial and in no way endorsed by Games Workshop Limited.

40k, Adeptus Astartes, Blood Angels, Bloodquest, Cadian, Catachan, Chaos, the Chaos device, the Chaos logo, Citadel, Citadel Device, Cityfight, Codex, Daemonhunters, Dark Angels, Dark Eldar, Dawn of War, ‘Eavy Metal, Eldar, Eldar symbol devices, Eye of Terror, Fire Warrior, the Fire Warrior logo, Forge World, Games Workshop, Games Workshop logo, Genestealer, Golden Demon, Gorkamorka, Great Unclean One, GW, GWI, the GWI logo, Inquisitor, the Inquisitor logo, the Inquisitor device, Inquisitor:Conspiracies, Keeper of Secrets, Khorne, the Khorne device, Kroot, Lord of Change, Necron, Nurgle, the Nurgle device, Ork, Ork skull devices, Sisters of Battle, Slaanesh, the Slaanesh device, Space Hulk, Space Marine, Space Marine chapters, Space Marine chapter logos, Tau, the Tau caste designations, Tyranid, Tyrannid, Tzeentch, the Tzeentch device, Ultramarines, Warhammer, Warhammer 40k Device, White Dwarf, the White Dwarf logo, and all associated marks, names, races, race insignia, characters, vehicles, locations, units, illustrations and images from the Warhammer 40,000 universe are either ®, TM and/or © Games Workshop Ltd 2000-2016, variably registered in the UK and other countries around the world. Used without permission. No challenge to their status intended. All Rights Reserved to their respective owners.

## License

Copyright © 2024 Marcus Lindner

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
