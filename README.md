# kilithikadya

Kilithikadya (Near-future-to-come) means "The near-future" in Aledari. This Programm should allow to take a peek view into the near future of the possible effects of your decisions during a round of Warhammer 40k. Mainly about the possibilities of wounds and damages your units can dish out against other units. 

## Installation
Acutally you have to download the project and run it via [Leiningen](https://leiningen.org/).

## Usage

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
- **:damage**: The damage of the weapone. *Default: 1*
- **:devastating?**: If *true* then the weapon has the Devastating-Ability.
- **:invul-save**: If the target has a invulnerbale save this value is any number between 2 and 6. *Default: 0*
- **:lethal?**: If *true* then the weapon has the Lethal-Ability. *Default: false*
- **:reroll-attack?**: If *true* then the attack roll can be rerolled if it fails. *Default: false*
- **:reroll-wound?**: If *true* then the wound roll can be rerolled if it fails. *Default: false*
- **:save**: The armor save value of the target. *Default: 6*
- **:save-mod**:  The modifier for the save roll of the target. *Default: 0*
- **:skill**: The ballistik or melee skill of the attacker. *Default: 4*
- **:strength**: The Strength of the weapon. *Default: 4*
- **:sustained**: The value of the Sustained-Ability of the weapon. If the weapon has none the value is 0. *Default: 0*
- **:toughness**: The tougness of the target. *Default: 4*
- **:wound-mod**: The modifier of the wound roll. *Default: 0*
- **:wounds**: The number of wounds of the target. *Default: 1*

## Examples
Say a squad of Inner Circle Companions lead b Azrael are sparring against a squad of Deathwing Knights by Belial and we want to know the probabilties who might be better. 

First we include the namespace and add the needed stats of the units and weapons.

```clojure
(ns kilithikadya.da-sparing
  (:use  [kilithikadya.kilithikadya]) 
  (:require [clojure.math.combinatorics :as combo]))

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

(def knight-master {:defender "Knight-master"
                        :toughnesss 5
                        :wounds 4
                        :save 2
                        :invul-save 4})
(def km-relic-weapon {:attacker "Deathwing Knights"
                      :attacks  6
                      :skill    2
                      :strength 7
                      :ap       -2
                      :damage   2
                      :lethal?  true})
```
Then we create the combinations of attacks from Azrael's unit against Belial's unit.

```clojure
(def  attack-combinations (map #(merge (first %) (second %)) 
                               (concat 
                                (combo/cartesian-product [caliban-greatsword-strike the-sword-of-secrets] [belial deathwing-knights knight-master])
                                (combo/cartesian-product  [km-relic-weapon dwk-power-weapon the-sword-of-silence] [azrael inner-circle-companions])
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
 :toughnesss 5,
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
Now we can get for example the probabilities when the Inner Circle Companions attacks the Deathwing Knights with ` (get-in result ["Inner Circle Companions" "Deathwing Knights"])` 

<details>
 <summary>Huge output </summary>
 
```clojure
{:average-critical-hits 4.000000000000001,
 :average-damage 17.33333333304475,
 :average-extra-hits 4.000000000000001,
 :average-hits 16.0,
 :average-kills 4.333333333261187,
 :average-total-hits 20.0,
 :average-wounds 8.666666666522374,
 :critical-wound-probabilities
 {0 0.0301101214499711,
  1 0.11097730477275065,
  2 0.1985747147318733,
  3 0.22984239697698988,
  4 0.19346617977490008,
  5 0.12623130176455627,
  6 0.0664544556260346,
  7 0.029012089097156496,
  8 0.01071380547603341,
  9 0.003397061039878733,
  10 9.355916392011157E-4,
  11 2.258803725176439E-4,
  12 4.816059837113252E-5,
  13 9.123198500729992E-6,
  14 1.5431261306648133E-6,
  15 2.3401337707414787E-7,
  16 3.192621994872741E-8,
  17 3.929634686094355E-9,
  18 4.3739267437969783E-10,
  19 4.411014760505394E-11,
  20 4.036689636870988E-12,
  21 3.35630084708908E-13,
  22 2.5377206292181403E-14,
  23 1.7460329236441206E-15,
  24 1.0935815168354365E-16,
  25 6.235831870157566E-18,
  26 3.2368885576762014E-19,
  27 1.5289271351535578E-20,
  28 6.567396457320884E-22,
  29 2.5629985137478972E-23,
  30 9.076602512004507E-25,
  31 2.9123660461104113E-26,
  32 8.45051643171402E-28,
  33 2.212169508526042E-29,
  34 5.2098007276750905E-31,
  35 1.1000407651926363E-32,
  36 2.0739367057380733E-34,
  37 3.473954194880122E-36,
  38 5.138944097252818E-38,
  39 6.663967654046922E-40,
  40 7.506115717790567E-42,
  41 7.259261975630101E-44,
  42 5.938538010602586E-46,
  43 4.028695703776067E-48,
  44 2.2051819511083356E-50,
  45 9.356458248798235E-53,
  46 2.887003150377035E-55,
  47 5.762347597475486E-58,
  48 5.583670152592533E-61},
 :critical-wound-probability 1/6,
 :extra-hit-probabilites
 {0 0.012579115212475307,
  1 0.0603797530198815,
  2 0.13887343194572746,
  3 0.2036810335204003,
  4 0.2138650851964204,
  5 0.17109206815713637,
  6 0.10835830983285308,
  7 0.055727130771181625,
  8 0.023684030577752164,
  9 0.008420988649867433,
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
 :hit-probabilities
 {0 3.5407061614721307E-12,
  1 1.6995389575066338E-10,
  2 3.908939602265244E-9,
  3 5.733111416655696E-8,
  4 6.019766987488477E-7,
  5 4.8158135899907835E-6,
  6 3.0500152736608198E-5,
  7 1.5685792835969968E-4,
  8 6.666461955287248E-4,
  9 0.00237029758410213,
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
 :hits-probability-map
 {0 4.4538950738679127E-14,
  1 2.351656599002272E-12,
  2 5.992448588184867E-11,
  3 9.815187535809062E-10,
  4 1.1612194238915967E-8,
  5 1.057207768153518E-7,
  6 7.705838482477698E-7,
  7 4.619073964547962E-6,
  8 2.3212396865582065E-5,
  9 9.92032425207862E-5,
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
  24 0.05375834680542328,
  25 0.03212192392299904,
  26 0.01713122679621023,
  27 0.008153492673639493,
  28 0.0034622051485939075,
  29 0.0013111011431360478,
  30 4.425176204751582E-4,
  31 1.3300439024393164E-4,
  32 3.5558101802018544E-5,
  33 8.44264430130937E-6,
  34 1.7766910107939209E-6,
  35 3.305332448682847E-7,
  36 5.41823648509297E-8,
  37 7.793471385149658E-9,
  38 9.784755176100697E-10,
  39 1.0651867057098438E-10,
  40 9.969648539944887E-12,
  41 7.935508646215499E-13,
  42 5.2954118832800276E-14,
  43 2.9060305851497797E-15,
  44 1.2767742589681672E-16,
  45 4.316765129769678E-18,
  46 1.0542027042494723E-19,
  47 1.6548312160890747E-21,
  48 1.2536600121886963E-23},
 :lethal-hits-probabilities
 {0 0.012579115212475307,
  1 0.0603797530198815,
  2 0.13887343194572746,
  3 0.2036810335204003,
  4 0.2138650851964204,
  5 0.17109206815713637,
  6 0.10835830983285308,
  7 0.055727130771181625,
  8 0.023684030577752164,
  9 0.008420988649867433,
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
 :maximal-wounds 48,
 :min-wound-probability
 {0 0.9999999999922553,
  1 0.9999245183634691,
  2 0.9991356805622491,
  3 0.9950982779574196,
  4 0.9816081718335288,
  5 0.9485124923034403,
  6 0.884937177732178,
  7 0.7853543517697035,
  8 0.6545624349341015,
  9 0.5075619647482529,
  10 0.3639727327383859,
  11 0.24058602015312633,
  12 0.14639609240385829,
  13 0.08200677499980072,
  14 0.04232412794185912,
  15 0.020151458504658618,
  16 0.008864920657164594,
  17 0.003609174408404694,
  18 0.0013621540192457259,
  19 4.7734538294984914E-4,
  20 1.555601745388879E-4,
  21 4.721226040359431E-5,
  22 1.3362570087459517E-5,
  23 3.5314043773767995E-6,
  24 8.724205754674776E-7,
  25 2.0168809322763554E-7,
  26 4.3673989235625294E-8,
  27 8.865938674650691E-9,
  28 1.6885600615887384E-9,
  29 3.019178164811162E-10,
  30 5.0709613217599594E-11,
  31 8.004307787784543E-12,
  32 1.1877766041293192E-12,
  33 1.6572579869296789E-13,
  34 2.1738900849060922E-14,
  35 2.6793886828622037E-15,
  36 3.0991403097650173E-16,
  37 3.3561556205690454E-17,
  38 3.3894718500832944E-18,
  39 3.1727394463314546E-19,
  40 2.727671562622399E-20,
  41 2.1263383109644733E-21,
  42 1.4769836904864383E-22,
  43 8.930432578682694E-24,
  44 4.554073068507096E-25,
  45 1.8733452142180888E-26,
  46 5.806384335576403E-28,
  47 1.2012657660240131E-29,
  48 1.2404523082004626E-31},
 :not-pass-save-test-probability 1/2,
 :received-damage-probability
 {0 7.54816287863256E-5,
  2 7.888378012199657E-4,
  4 0.004037402604829489,
  6 0.013490106123890844,
  8 0.03309567953008835,
  10 0.0635753145712625,
  12 0.09958282596247435,
  14 0.13079191683560198,
  16 0.1470004701858487,
  18 0.14358923200986712,
  20 0.12338671258525956,
  22 0.09418992774926803,
  24 0.06438931740405757,
  26 0.03968264705794159,
  28 0.02217266943720051,
  30 0.011286537847494026,
  32 0.005255746248759899,
  34 0.002247020389158968,
  36 8.848086362958773E-4,
  38 3.2178520841096134E-4,
  40 1.0834791413529357E-4,
  42 3.3849690316134795E-5,
  44 9.831165710082718E-6,
  46 2.658983801909321E-6,
  48 6.70732482239842E-7,
  50 1.580141039920103E-7,
  52 3.4808050560974614E-8,
  54 7.177378613061952E-9,
  56 1.3866422451076222E-9,
  58 2.512082032635167E-10,
  60 4.270530542981506E-11,
  62 6.816531183655223E-12,
  64 1.0220508054363515E-12,
  66 1.4398689784390704E-13,
  68 1.9059512166198717E-14,
  70 2.369474651885702E-15,
  72 2.763524747708113E-16,
  74 3.0172084355607155E-17,
  76 3.0721979054501486E-18,
  78 2.8999722900692155E-19,
  80 2.5150377315259507E-20,
  82 1.9786399419158292E-21,
  84 1.387679364699611E-22,
  86 8.475025271831986E-24,
  88 4.366738547085287E-25,
  90 1.8152813708623248E-26,
  92 5.686257758974002E-28,
  94 1.1888612429420085E-29,
  96 1.2404523082004626E-31},
 :received-wounds-probability
 {0 7.54816287863256E-5,
  1 7.888378012199657E-4,
  2 0.004037402604829489,
  3 0.013490106123890844,
  4 0.03309567953008835,
  5 0.0635753145712625,
  6 0.09958282596247435,
  7 0.13079191683560198,
  8 0.1470004701858487,
  9 0.14358923200986712,
  10 0.12338671258525956,
  11 0.09418992774926803,
  12 0.06438931740405757,
  13 0.03968264705794159,
  14 0.02217266943720051,
  15 0.011286537847494026,
  16 0.005255746248759899,
  17 0.002247020389158968,
  18 8.848086362958773E-4,
  19 3.2178520841096134E-4,
  20 1.0834791413529357E-4,
  21 3.3849690316134795E-5,
  22 9.831165710082718E-6,
  23 2.658983801909321E-6,
  24 6.70732482239842E-7,
  25 1.580141039920103E-7,
  26 3.4808050560974614E-8,
  27 7.177378613061952E-9,
  28 1.3866422451076222E-9,
  29 2.512082032635167E-10,
  30 4.270530542981506E-11,
  31 6.816531183655223E-12,
  32 1.0220508054363515E-12,
  33 1.4398689784390704E-13,
  34 1.9059512166198717E-14,
  35 2.369474651885702E-15,
  36 2.763524747708113E-16,
  37 3.0172084355607155E-17,
  38 3.0721979054501486E-18,
  39 2.8999722900692155E-19,
  40 2.5150377315259507E-20,
  41 1.9786399419158292E-21,
  42 1.387679364699611E-22,
  43 8.475025271831986E-24,
  44 4.366738547085287E-25,
  45 1.8152813708623248E-26,
  46 5.686257758974002E-28,
  47 1.1888612429420085E-29,
  48 1.2404523082004626E-31},
 :to-hit-critical-probability 1/6,
 :to-hit-probability 2/3,
 :wound-probabilities
 {0 5.564798376768408E-10,
  1 1.5024955617274704E-8,
  2 1.9819168538820108E-7,
  3 1.7024349416058757E-6,
  4 1.070991348196368E-5,
  5 5.261684996596429E-5,
  6 2.1022280721762145E-4,
  7 7.023424035464566E-4,
  8 0.0020023965061469143,
  9 0.004947500244623563,
  10 0.010722943192270797,
  11 0.020585535275011273,
  12 0.03528556487312996,
  13 0.05436354190928183,
  14 0.07570664855437075,
  15 0.09575458697530086,
  16 0.1104538498168579,
  17 0.1166162500692645,
  18 0.11304735048761366,
  19 0.10089907878674877,
  20 0.08311950199603742,
  21 0.06333618017529864,
  22 0.04472755716063488,
  23 0.029324051832010006,
  24 0.01787598586042158,
  25 0.010146457058547921,
  26 0.005369021149606075,
  27 0.0026515064762251505,
  28 0.0012233070673405044,
  29 5.277201349689513E-4,
  30 2.1302601171793638E-4,
  31 8.052223120381186E-5,
  32 2.851719306748517E-5,
  33 9.467191193289429E-6,
  34 2.947391118981314E-6,
  35 8.607947391925068E-7,
  36 2.358923374225266E-7,
  37 6.066667722675378E-8,
  38 1.4643123390631255E-8,
  39 3.3167964625384547E-9,
  40 7.047049873206643E-10,
  41 1.4025332760674693E-10,
  42 2.605360235980465E-11,
  43 4.477485514876112E-12,
  44 6.981949210584213E-13,
  45 9.507280861832018E-14,
  46 1.0540112827938576E-14,
  47 8.35198370399139E-16,
  48 3.491562845614047E-17},
 :wound-probability 2/3,
 :wounds-by-failed-save
 {0 7.54816287863256E-5,
  1 7.888378012199657E-4,
  2 0.004037402604829489,
  3 0.013490106123890844,
  4 0.03309567953008835,
  5 0.0635753145712625,
  6 0.09958282596247435,
  7 0.13079191683560198,
  8 0.1470004701858487,
  9 0.14358923200986712,
  10 0.12338671258525956,
  11 0.09418992774926803,
  12 0.06438931740405757,
  13 0.03968264705794159,
  14 0.02217266943720051,
  15 0.011286537847494026,
  16 0.005255746248759899,
  17 0.002247020389158968,
  18 8.848086362958773E-4,
  19 3.2178520841096134E-4,
  20 1.0834791413529357E-4,
  21 3.3849690316134795E-5,
  22 9.831165710082718E-6,
  23 2.658983801909321E-6,
  24 6.70732482239842E-7,
  25 1.580141039920103E-7,
  26 3.4808050560974614E-8,
  27 7.177378613061952E-9,
  28 1.3866422451076222E-9,
  29 2.512082032635167E-10,
  30 4.270530542981506E-11,
  31 6.816531183655223E-12,
  32 1.0220508054363515E-12,
  33 1.4398689784390704E-13,
  34 1.9059512166198717E-14,
  35 2.369474651885702E-15,
  36 2.763524747708113E-16,
  37 3.0172084355607155E-17,
  38 3.0721979054501486E-18,
  39 2.8999722900692155E-19,
  40 2.5150377315259507E-20,
  41 1.9786399419158292E-21,
  42 1.387679364699611E-22,
  43 8.475025271831986E-24,
  44 4.366738547085287E-25,
  45 1.8152813708623248E-26,
  46 5.686257758974002E-28,
  47 1.1888612429420085E-29,
  48 1.2404523082004626E-31}}
```
</details>

But this might be to much, we are only ineterested in the average wounds, damage and kill as well the posibilities for a minimum ammount of wounds.

```clojure
=> (-> 
    (get-in result ["Inner Circle Companions" "Deathwing Knights"]) 
    (select-keys [:average-wounds :average-damage :average-kills :min-wound-probability]))

{:average-wounds 8.666666666522374,
 :average-damage 17.33333333304475,
 :average-kills 4.333333333261187,
 :min-wound-probability
 {0 0.9999999999922553,
  1 0.9999245183634691,
  2 0.9991356805622491,
  3 0.9950982779574196,
  4 0.9816081718335288,
  5 0.9485124923034403,
  6 0.884937177732178,
  7 0.7853543517697035,
  8 0.6545624349341015,
  9 0.5075619647482529,
  10 0.3639727327383859,
  11 0.24058602015312633,
  12 0.14639609240385829,
  13 0.08200677499980072,
  14 0.04232412794185912,
  15 0.020151458504658618,
  16 0.008864920657164594,
  17 0.003609174408404694,
  18 0.0013621540192457259,
  19 4.7734538294984914E-4,
  20 1.555601745388879E-4,
  21 4.721226040359431E-5,
  22 1.3362570087459517E-5,
  23 3.5314043773767995E-6,
  24 8.724205754674776E-7,
  25 2.0168809322763554E-7,
  26 4.3673989235625294E-8,
  27 8.865938674650691E-9,
  28 1.6885600615887384E-9,
  29 3.019178164811162E-10,
  30 5.0709613217599594E-11,
  31 8.004307787784543E-12,
  32 1.1877766041293192E-12,
  33 1.6572579869296789E-13,
  34 2.1738900849060922E-14,
  35 2.6793886828622037E-15,
  36 3.0991403097650173E-16,
  37 3.3561556205690454E-17,
  38 3.3894718500832944E-18,
  39 3.1727394463314546E-19,
  40 2.727671562622399E-20,
  41 2.1263383109644733E-21,
  42 1.4769836904864383E-22,
  43 8.930432578682694E-24,
  44 4.554073068507096E-25,
  45 1.8733452142180888E-26,
  46 5.806384335576403E-28,
  47 1.2012657660240131E-29,
  48 1.2404523082004626E-31}}

```

But nicer to read is a map with only the effects of the Inner Circle Companions against Belial's squad.
```clojure
=> (apply merge 
       (pmap 
        #(sorted-map (str (first %) " vs. " (second %)) 
          (-> 
           (get-in result %) 
           (select-keys [:average-wounds :average-damage :average-kills]))) 
        [["Inner Circle Companions" "Belial"]
         ["Inner Circle Companions" "Deathwing Knights"]
         ["Inner Circle Companions" "Knight Master"]]))

{"Inner Circle Companions vs. Belial"
 {:average-wounds 8.666666666522374, :average-damage 17.33333333304475, :average-kills 2.8888888888407913},
 "Inner Circle Companions vs. Deathwing Knights"
 {:average-wounds 8.666666666522374, :average-damage 17.33333333304475, :average-kills 4.333333333261187},
 "Inner Circle Companions vs. Knight Master"
 {:average-wounds 8.666666666522374, :average-damage 17.33333333304475, :average-kills 4.333333333261187}}
```
So as it seems, the Inner Circle Companions will on average wipe out the Deathwing Knights and only Belial and the Knight Master will remain. 
Then we will take a look how much these two might decimate the Inner Circle Companions. 

```clojure
(apply merge
       (pmap
        #(sorted-map (str (first %) " vs. " (second %))
                     (->
                      (get-in result %)
                      (select-keys [:average-wounds :average-damage :average-kills])))
        [["Belial" "Inner Circle Companions"]
         ["Knight Master" "Inner Circle Companions" ]]))

{"Belial vs. Inner Circle Companions"
 {:average-wounds 1.3333333333333335, :average-damage 2.666666666666667, :average-kills 0.888888888888889},
 "Knight Master vs. Inner Circle Companions"
 {:average-wounds 0.45244981518675836, :average-damage 0.9048996303735167, :average-kills 0.30163321012450556}}
```
Ok this looks no so good. Both will kill on average 1 Inner Circle Companion. :(

## What you should know
This project is work in progress and in an actuall alpha version. 
Planned changes are acutally more probability statistics and features. 
The keywords in the returned map **should** be fix, except the naming is horrible wrong and causes more trouble. 

## Bugs
Actually none known.

## To-Do
- [] Add Feel No Pain to calculate received damage.
- [] Better documentation.
- [] Find a practicable way to handle rounding errors for multiple additions with doubles. 

## License

Copyright © 2024 Marcus Lindner

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
