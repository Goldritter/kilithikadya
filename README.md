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
If you want to know the probabilites for a troop of 6 Inner Circle Companions lead by Azrael and a Lieutenant attacking a group of Tyranid Warriors you call: 

```clojure
(get-probabilities-for :attacks 24 :skill 3 :save 4 :toughness 4 :strength 6
                        :sustained 1 :wounds 3 :lethal? true :damage 2 :ap -2)
```

The Inner Companions choose to strike witht their Greatswords so they have 6 * 4 = 24 attacks, with a skill of 3+, a strenght of 6, AP -2, Damage 2 and Sustained 1 as well lethal. The Tyranid warriors have a toughness of 4 with 3 wounds and a save value of 4+.  
<details>
 <summary>Huge output </summary>
 
```clojure
{:average-critical-hits 4.000000000000001,
 :average-damage 24.444444444442077,
 :average-extra-hits 4.000000000000001,
 :average-hits 12.0,
 :average-kills 8.148148148147358,
 :average-total-hits 16.0,
 :average-wounds 12.222222222221038,
 :critical-wound-probabilities
 {0 0.06301552874475323,
  1 0.18069907463430548,
  2 0.25221346278291357,
  3 0.22834015657499648,
  4 0.15076520164900045,
  5 0.07739184029244431,
  6 0.03215330618534736,
  7 0.011113464435637379,
  8 0.0032601194962509784,
  9 8.239826814146769E-4,
  10 1.8154618961718625E-4,
  11 3.519505801384659E-5,
  12 6.0487878566522395E-6,
  13 9.273023063705007E-7,
  14 1.2745293689479682E-7,
  15 1.5772109384341854E-8,
  16 1.7634826525228266E-9,
  17 1.7867703984899667E-10,
  18 1.6445231127288203E-11,
  19 1.3776995251470174E-12,
  20 1.0522439280551431E-13,
  21 7.336372851636146E-15,
  22 4.673792741535072E-16,
  23 2.7225404880440336E-17,
  24 1.4506742156376014E-18,
  25 7.071533735179302E-20,
  26 3.1531743913206785E-21,
  27 1.2855802934966243E-22,
  28 4.789316192838747E-24,
  29 1.62873662056479E-25,
  30 5.04981658734067E-27,
  31 1.4250944289627781E-28,
  32 3.653300159052799E-30,
  33 8.486785286936526E-32,
  34 1.781322494799675E-33,
  35 3.3663038622087693E-35,
  36 5.703475746334144E-37,
  37 8.619701010143522E-39,
  38 1.154880171931062E-40,
  39 1.3614670688789865E-42,
  40 1.399142171458176E-44,
  41 1.2388452568759882E-46,
  42 9.309631805165113E-49,
  43 5.820245626428037E-51,
  44 2.945008297526737E-53,
  45 1.1585291745420578E-55,
  46 3.323779582330317E-58,
  47 6.1852769443422216E-61,
  48 5.602605927846225E-64},
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
 {0 5.9604644775390625E-8,
  1 1.430511474609369E-6,
  2 1.64508819580078E-5,
  3 1.2063980102539084E-4,
  4 6.33358955383301E-4,
  5 0.0025334358215332023,
  6 0.008022546768188454,
  7 0.020629405975341752,
  8 0.04383748769760141,
  9 0.07793331146240232,
  10 0.11689996719360349,
  11 0.14878177642822268,
  12 0.16118025779724124,
  13 0.14878177642822266,
  14 0.11689996719360349,
  15 0.07793331146240232,
  16 0.04383748769760141,
  17 0.020629405975341752,
  18 0.008022546768188454,
  19 0.0025334358215332023,
  20 6.33358955383301E-4,
  21 1.2063980102539084E-4,
  22 1.64508819580078E-5,
  23 1.4305114746093716E-6,
  24 5.9604644775390625E-8},
 :hits-probability-map
 {0 7.49773693828303E-10,
  1 2.1593482382255055E-8,
  2 3.015889706054961E-7,
  3 2.7216425194594375E-6,
  4 1.784000250498711E-5,
  5 9.053096855896508E-5,
  6 3.7018301753138135E-4,
  7 0.0012535027868978469,
  8 0.003585124274997461,
  9 0.008790345436945962,
  10 0.01869072120650071,
  11 0.03477898278885369,
  12 0.05705013438962692,
  13 0.08299126214216501,
  14 0.10758897342013514,
  15 0.12480043440245178,
  16 0.12996560692521242,
  17 0.12184427609048361,
  18 0.1030706557489679,
  19 0.07881806490281934,
  20 0.05456661619553618,
  21 0.03424116829404408,
  22 0.01949278438879824,
  23 0.010073260492215382,
  24 0.00472709964218372,
  25 0.002014652098443076,
  26 7.797113755519294E-4,
  27 2.7392934635235257E-4,
  28 8.730658591285787E-5,
  29 2.5221780768902196E-5,
  30 6.596521967933951E-6,
  31 1.5596067339581925E-6,
  32 3.327119537285444E-7,
  33 6.389782241405546E-8,
  34 1.1017110878221866E-8,
  35 1.6996610486715442E-9,
  36 2.336773504599127E-10,
  37 2.8490942700629056E-11,
  38 3.0622877624730894E-12,
  39 2.8804203927784647E-13,
  40 2.349547044862344E-14,
  41 1.6429911728427485E-15,
  42 9.704125694774635E-17,
  43 4.746430044384264E-18,
  44 1.8706598466669362E-19,
  45 5.7076980529694044E-21,
  46 1.2649558257665229E-22,
  47 1.811392590596453E-24,
  48 1.2579115212475406E-26},
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
 {0 0.9999999999999618,
  1 0.9999989116297384,
  2 0.9999819866928783,
  3 0.9998527970144252,
  4 0.9992075535108521,
  5 0.9968357913682767,
  6 0.9899935070361467,
  7 0.9738594949080073,
  8 0.9418832693146196,
  9 0.8875199538392301,
  10 0.8069985961710129,
  11 0.701819800695092,
  12 0.5794681511282053,
  13 0.4516934093841306,
  14 0.33109487575559476,
  15 0.227638590072146,
  16 0.14658240266713962,
  17 0.08834045822807744,
  18 0.049821608059812,
  19 0.026299985037334335,
  20 0.013000976963125138,
  21 0.006022057779775344,
  22 0.0026155423630656225,
  23 0.0010659567836725113,
  24 4.079352571174611E-4,
  25 1.466964835317901E-4,
  26 4.960343425475464E-5,
  27 1.5780964921394453E-5,
  28 4.726371611744925E-6,
  29 1.3332514901110085E-6,
  30 3.5438638600442284E-7,
  31 8.879452322544399E-8,
  32 2.0978527587357038E-8,
  33 4.6746637993981095E-9,
  34 9.826289983270004E-10,
  35 1.9486676065623576E-10,
  36 3.6458716918474544E-11,
  37 6.434723938635947E-12,
  38 1.0709479637206977E-12,
  39 1.6793488425342948E-13,
  40 2.476232392854909E-14,
  41 3.4187396977382037E-15,
  42 4.381405513620786E-16,
  43 5.1290186002539845E-17,
  44 5.334349890643356E-18,
  45 4.711943195081484E-19,
  46 3.287369904960835E-20,
  47 1.5937325231862359E-21,
  48 3.984235309171945E-23},
 :not-pass-save-test-probability 1/6,
 :received-damage-probability
 {0 1.0883702233240321E-6,
  2 1.692493686019872E-5,
  4 1.2918967845309512E-4,
  6 6.452435035730302E-4,
  8 0.002371762142575431,
  10 0.0068422843321299,
  12 0.0161340121281395,
  14 0.03197622559338767,
  16 0.05436331547538951,
  18 0.08052135766821726,
  20 0.10517879547592088,
  22 0.12235164956688677,
  24 0.12777474174407474,
  26 0.12059853362853583,
  28 0.10345628568344867,
  30 0.08105618740500642,
  32 0.05824194443906223,
  34 0.03851885016826543,
  36 0.02352162302247766,
  38 0.013299008074209199,
  40 0.006978919183349795,
  42 0.0034065154167097216,
  44 0.0015495855793931123,
  46 6.5802152655505E-4,
  48 2.6123877358567074E-4,
  50 9.70930492770356E-5,
  52 3.382246933336017E-5,
  54 1.105459330964953E-5,
  56 3.393120121633918E-6,
  58 9.788651041065854E-7,
  60 2.6559186277897884E-7,
  62 6.781599563808696E-8,
  64 1.6303863787958928E-8,
  66 3.6920348010711087E-9,
  68 7.877622376707646E-10,
  70 1.584080437377612E-10,
  72 3.0023992979838605E-11,
  74 5.363775974915248E-12,
  76 9.030130794672684E-13,
  78 1.431725603248804E-13,
  80 2.1343584230810883E-14,
  82 2.980599146376125E-15,
  84 3.8685036535953874E-16,
  86 4.5955836111896485E-17,
  88 4.863155571135208E-18,
  90 4.3832062045854005E-19,
  92 3.127996652642211E-20,
  94 1.5538901700945164E-21,
  96 3.984235309171945E-23},
 :received-wounds-probability
 {0 1.0883702233240321E-6,
  1 1.692493686019872E-5,
  2 1.2918967845309512E-4,
  3 6.452435035730302E-4,
  4 0.002371762142575431,
  5 0.0068422843321299,
  6 0.0161340121281395,
  7 0.03197622559338767,
  8 0.05436331547538951,
  9 0.08052135766821726,
  10 0.10517879547592088,
  11 0.12235164956688677,
  12 0.12777474174407474,
  13 0.12059853362853583,
  14 0.10345628568344867,
  15 0.08105618740500642,
  16 0.05824194443906223,
  17 0.03851885016826543,
  18 0.02352162302247766,
  19 0.013299008074209199,
  20 0.006978919183349795,
  21 0.0034065154167097216,
  22 0.0015495855793931123,
  23 6.5802152655505E-4,
  24 2.6123877358567074E-4,
  25 9.70930492770356E-5,
  26 3.382246933336017E-5,
  27 1.105459330964953E-5,
  28 3.393120121633918E-6,
  29 9.788651041065854E-7,
  30 2.6559186277897884E-7,
  31 6.781599563808696E-8,
  32 1.6303863787958928E-8,
  33 3.6920348010711087E-9,
  34 7.877622376707646E-10,
  35 1.584080437377612E-10,
  36 3.0023992979838605E-11,
  37 5.363775974915248E-12,
  38 9.030130794672684E-13,
  39 1.431725603248804E-13,
  40 2.1343584230810883E-14,
  41 2.980599146376125E-15,
  42 3.8685036535953874E-16,
  43 4.5955836111896485E-17,
  44 4.863155571135208E-18,
  45 4.3832062045854005E-19,
  46 3.127996652642211E-20,
  47 1.5538901700945164E-21,
  48 3.984235309171945E-23},
 :to-hit-critical-probability 1/6,
 :to-hit-probability 1/2,
 :wound-probabilities
 {0 4.423839262756364E-8,
  1 8.759201740257599E-7,
  2 8.509365417893441E-6,
  3 5.4067947807244306E-5,
  4 2.527244629110716E-4,
  5 9.267195284793932E-4,
  6 0.0027763231721524275,
  7 0.006987841585471862,
  8 0.0150805188527556,
  9 0.028341371962412765,
  10 0.04695059907187209,
  11 0.06923546516704272,
  12 0.09161601772700191,
  13 0.10951587149033694,
  14 0.11893262111585295,
  15 0.11790693627709158,
  16 0.10715140104344335,
  17 0.08958673914177777,
  18 0.0691266953834364,
  19 0.049363476007368776,
  20 0.032702644161109906,
  21 0.020142530757407098,
  22 0.011556588560240532,
  23 0.006186810947325734,
  24 0.0030951396508568057,
  25 0.0014489417666295875,
  26 6.354681991008895E-4,
  27 2.613749122439763E-4,
  28 1.009163107636136E-4,
  29 3.6604624342102246E-5,
  30 1.2482182602760029E-5,
  31 4.003907781932637E-6,
  32 1.2087432624059524E-6,
  33 3.435726161734435E-7,
  34 9.197663995100314E-8,
  35 2.3196062785361304E-8,
  36 5.511829905750614E-9,
  37 1.2341072878411789E-9,
  38 2.603620404640004E-10,
  39 5.175026002981278E-11,
  40 9.687451500994177E-12,
  41 1.7064428066700064E-12,
  42 2.8218791552312823E-13,
  43 4.3532970548716304E-14,
  44 6.168868397689469E-15,
  45 7.76152304509554E-16,
  46 8.10643204029314E-17,
  47 6.169146893662501E-18,
  48 2.5179365976509813E-19},
 :wound-probability 2/3,
 :wounds-by-failed-save
 {0 1.0883702233240321E-6,
  1 1.692493686019872E-5,
  2 1.2918967845309512E-4,
  3 6.452435035730302E-4,
  4 0.002371762142575431,
  5 0.0068422843321299,
  6 0.0161340121281395,
  7 0.03197622559338767,
  8 0.05436331547538951,
  9 0.08052135766821726,
  10 0.10517879547592088,
  11 0.12235164956688677,
  12 0.12777474174407474,
  13 0.12059853362853583,
  14 0.10345628568344867,
  15 0.08105618740500642,
  16 0.05824194443906223,
  17 0.03851885016826543,
  18 0.02352162302247766,
  19 0.013299008074209199,
  20 0.006978919183349795,
  21 0.0034065154167097216,
  22 0.0015495855793931123,
  23 6.5802152655505E-4,
  24 2.6123877358567074E-4,
  25 9.70930492770356E-5,
  26 3.382246933336017E-5,
  27 1.105459330964953E-5,
  28 3.393120121633918E-6,
  29 9.788651041065854E-7,
  30 2.6559186277897884E-7,
  31 6.781599563808696E-8,
  32 1.6303863787958928E-8,
  33 3.6920348010711087E-9,
  34 7.877622376707646E-10,
  35 1.584080437377612E-10,
  36 3.0023992979838605E-11,
  37 5.363775974915248E-12,
  38 9.030130794672684E-13,
  39 1.431725603248804E-13,
  40 2.1343584230810883E-14,
  41 2.980599146376125E-15,
  42 3.8685036535953874E-16,
  43 4.5955836111896485E-17,
  44 4.863155571135208E-18,
  45 4.3832062045854005E-19,
  46 3.127996652642211E-20,
  47 1.5538901700945164E-21,
  48 3.984235309171945E-23}}
```
</details>

If you want to have a smaller output, because your are only interested in the average damage, wounds and kills, use `select-keys`. 
```clojure
=> (-> (get-probabilities-for :attacks 24 :skill 3 :save 4 :toughness 4 :strength 6
                        :sustained 1 :wounds 3 :lethal? true :damage 2 :ap -2)
    (select-keys [:average-wounds :average-damage :average-kills])                        
    )

{:average-wounds 12.222222222221038, :average-damage 24.444444444442077, :average-kills 8.148148148147358}
```

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

## Disclaimer
This project is completely unofficial and in no way endorsed by Games Workshop Limited.

40k, Adeptus Astartes, Blood Angels, Bloodquest, Cadian, Catachan, Chaos, the Chaos device, the Chaos logo, Citadel, Citadel Device, Cityfight, Codex, Daemonhunters, Dark Angels, Dark Eldar, Dawn of War, ‘Eavy Metal, Eldar, Eldar symbol devices, Eye of Terror, Fire Warrior, the Fire Warrior logo, Forge World, Games Workshop, Games Workshop logo, Genestealer, Golden Demon, Gorkamorka, Great Unclean One, GW, GWI, the GWI logo, Inquisitor, the Inquisitor logo, the Inquisitor device, Inquisitor:Conspiracies, Keeper of Secrets, Khorne, the Khorne device, Kroot, Lord of Change, Necron, Nurgle, the Nurgle device, Ork, Ork skull devices, Sisters of Battle, Slaanesh, the Slaanesh device, Space Hulk, Space Marine, Space Marine chapters, Space Marine chapter logos, Tau, the Tau caste designations, Tyranid, Tyrannid, Tzeentch, the Tzeentch device, Ultramarines, Warhammer, Warhammer 40k Device, White Dwarf, the White Dwarf logo, and all associated marks, names, races, race insignia, characters, vehicles, locations, units, illustrations and images from the Warhammer 40,000 universe are either ®, TM and/or © Games Workshop Ltd 2000-2016, variably registered in the UK and other countries around the world. Used without permission. No challenge to their status intended. All Rights Reserved to their respective owners.

## License

Copyright © 2024 Marcus Lindner

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
