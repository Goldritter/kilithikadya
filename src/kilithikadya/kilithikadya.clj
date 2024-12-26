(ns kilithikadya.kilithikadya
  (:require [clojure.math.combinatorics :as combo])

  (:import (org.apache.commons.statistics.distribution BinomialDistribution)))

(def critical-prob 1/6)

(defn get-ccdf-for [probability-map]
  (loop [ccdf-map (sorted-map)
         sorted-kvs (sort-by key probability-map)]
    (if (empty? sorted-kvs)
      ccdf-map
      (recur (assoc ccdf-map (key (first sorted-kvs)) (apply + (map val sorted-kvs)))
             (rest sorted-kvs)))))

(defn get-roll-probability [min max]
  (if (or (>= 0 min) (> min max)) 0
                                  (/ (- (inc (- 6 min))
                                        (- 6 max))
                                     6
                                     )))

(defn get-save-probability [save invul-save ap save-mod]
  (let [min-roll (-> (+ save (* -1 save-mod) (abs ap))
                     (min (if (= 0 invul-save) 6 invul-save))
                     (max 2)
                     )]
    (get-roll-probability min-roll 6)
    ))

(defn get-lethal-wounds-occurence-probability-map [distribution range lethal-hits occurence-probability]
  (reduce #(assoc %1 (+ lethal-hits %2) (* occurence-probability (.probability distribution %2))) (sorted-map) range))

(defn get-critical-wound-probability [& {:keys [anti reroll?]
                                         :or   {anti    0
                                                reroll? false}}]
  (let [base-prob (get-roll-probability (if (not= 0 anti) anti 6) 6)]
    (if reroll?
      (- 1 (* (- 1 base-prob) (- 1 base-prob)))
      base-prob)))

(defn get-wound-probability
  [strength toughness & {:keys [wound-mod anti reroll? consider-critical?]
                         :or   {wound-mod          0
                                anti               0
                                reroll?            false
                                consider-critical? true}}]
  (let [min-roll (->>
                   (cond
                     (<= (* 2 toughness) strength) 2
                     (< toughness strength) 3
                     (= toughness strength) 4
                     (<= strength (* 1/2 toughness)) 6
                     :else 5)
                   (+ (max -1 (min 1 wound-mod) wound-mod))
                   (min 6)
                   (max 2))

        base-prob (get-roll-probability min-roll (cond
                                                   (not= 0 anti) anti
                                                   consider-critical? 6
                                                   :else 5))
        ]

    (if reroll?
      (- 1 (* (- 1 base-prob) (- 1 base-prob)))
      base-prob)))

(defn get-passed-wound-probability [save-probability wounds wound-probability]
  (if (= 0 wounds) (sorted-map 0 (* 1.0 wound-probability))
                   (let [fail-save-test-probability-density (BinomialDistribution/of wounds (- 1.0 save-probability))]
                     (reduce #(assoc %1 %2
                                        (* wound-probability (.probability fail-save-test-probability-density %2)))
                             (sorted-map) (range 0 (inc wounds))))))

(defn get-to-hit-probability [skill attack-mod reroll? consider-critical?]
  (let [base-prob (get-roll-probability
                    (max 2 (- skill attack-mod))
                    (if consider-critical? 6 5))]
    (if reroll? (- 1 (* (- 1 base-prob) (- 1 base-prob)))
                base-prob)))

(defn get-hit-probabilities [& {:keys [attacks skill
                                       sustained lethal? reroll-attack?
                                       attack-mod]
                                :or   {attacks        1
                                       skill          4
                                       sustained      0
                                       lethal?        false
                                       reroll-attack? false
                                       attack-mod     0}}]
  (let [separate-critical-hits? (or lethal? (not= 0 sustained))
        attack-range (range 0 (inc attacks))
        to-hit-probability (get-to-hit-probability skill attack-mod reroll-attack? (not separate-critical-hits?))
        to-hit-critically-probability (* (- 1 to-hit-probability)
                                         (if separate-critical-hits? (get-critical-wound-probability 6 0 reroll-attack? true) 0))

        to-hit-distribution (BinomialDistribution/of attacks to-hit-probability)
        to-hit-critical-distribution (BinomialDistribution/of attacks to-hit-critically-probability)

        to-wound-hits-probability-map (if (not= 0 sustained)
                                        (apply merge-with + (map #(sorted-map
                                                                    (+ (first %)
                                                                       (* (second %) (if lethal? sustained (inc sustained))))
                                                                    (* (.probability to-hit-distribution (first %))
                                                                       (.probability to-hit-critical-distribution (second %))
                                                                       ))
                                                                 (filter #(>= attacks (+ (first %) (second %)))
                                                                         (combo/cartesian-product attack-range attack-range))))
                                        (reduce #(assoc %1 %2 (.probability to-hit-distribution %2)) (sorted-map) attack-range))
        ]


    (sorted-map
      :average-hits (.getMean to-hit-distribution)
      :lethal-hits-probabilities (if lethal? (reduce #(assoc %1 %2 (.probability to-hit-critical-distribution %2))
                                                     (sorted-map) attack-range)
                                             {0 1.0})
      :to-hit-probability to-hit-probability
      :to-hit-critical-probability to-hit-critically-probability
      :average-critical-hits (.getMean to-hit-critical-distribution)
      :average-extra-hits (* sustained (.getMean to-hit-critical-distribution))
      :average-total-hits (+ (* sustained (.getMean to-hit-critical-distribution))
                             (.getMean to-hit-distribution))
      :to-wound-hits-probability-map to-wound-hits-probability-map
      :expected-wounds-to-hits (apply + (map #(* (key %) (val %)) to-wound-hits-probability-map)))))

(defn get-probabilities-for [& {:keys [attacks skill strength ap damage anti
                                       toughness save invul-save wounds
                                       sustained lethal? devastating? reroll-wound? reroll-attack?
                                       attack-mod wound-mod save-mod]
                                :or   {anti           0
                                       attacks        1
                                       skill          4
                                       strength       4
                                       ap             0
                                       wounds         1
                                       damage         1
                                       toughness      4
                                       save           6
                                       invul-save     0
                                       sustained      0
                                       lethal?        false
                                       devastating?   false
                                       reroll-wound?  false
                                       reroll-attack? false
                                       attack-mod     0
                                       wound-mod      0
                                       save-mod       0
                                       }}]
  (let [separate-critical-wounds? (or (not= anti) devastating?)
        maximal-hits (+ attacks (* sustained attacks))

        hit-range (range 0 (inc maximal-hits))
        wound-probability (get-wound-probability strength toughness :reroll? reroll-wound?
                                                 :wound-mod wound-mod :anti anti :consider-critical? (not separate-critical-wounds?))
        critical-wound-probability (* (- 1 wound-probability)
                                      (if separate-critical-wounds? (get-critical-wound-probability 1 10 :anti anti :reroll? reroll-wound?) 0))

        pass-save-test-probability (get-save-probability save invul-save ap save-mod)

        hit-probability-informations (get-hit-probabilities :attacks attacks :attack-mod attack-mod :skill skill
                                                            :lethal? lethal? :sustained sustained :reroll-attack? reroll-attack?)
        hit-combinations (filter #(>= maximal-hits (apply + %))
                                 (combo/cartesian-product (keys (:to-wound-hits-probability-map hit-probability-informations))
                                                          (keys (:lethal-hits-probabilities hit-probability-informations))))

        wound-probability-distributions (apply merge
                                               (doall (map #(sorted-map % (BinomialDistribution/of % wound-probability))
                                                           hit-range)))

        critical-wound-probability-distributions (apply merge
                                                        (doall (map #(sorted-map % (BinomialDistribution/of % critical-wound-probability))
                                                                    hit-range)))

        wound-probabilities (if lethal?
                              (apply merge-with +
                                     (doall (pmap #(get-lethal-wounds-occurence-probability-map
                                                     (get wound-probability-distributions (first %))
                                                     (range 0 (inc (first %)))
                                                     (second %)
                                                     (* (get-in hit-probability-informations [:to-wound-hits-probability-map (first %)] 0.0)
                                                        (get-in hit-probability-informations [:lethal-hits-probabilities (second %)] 0.0)))
                                                  hit-combinations)))
                              (:to-wound-hits-probability-map hit-probability-informations)
                              )

        critical-wound-probabilities (apply merge-with +
                                            (pmap #(get-lethal-wounds-occurence-probability-map
                                                     (get critical-wound-probability-distributions %)
                                                     (range 0 (inc %))
                                                     0

                                                     (get-in hit-probability-informations [:to-wound-hits-probability-map %] 0.0)
                                                     )
                                                  (range 0 (inc maximal-hits))))

        wounds-by-failed-save-test (merge-with +
                                               (apply merge-with +
                                                      (doall (pmap #(get-passed-wound-probability pass-save-test-probability (key %) (val %))
                                                                   wound-probabilities)))
                                               (if (and (not devastating?) (not= 0 anti))
                                                 (apply merge-with +
                                                        (doall (pmap #(get-passed-wound-probability pass-save-test-probability (key %) (val %))
                                                                     critical-wound-probabilities)))
                                                 {}))

        received-wounds-probability (if devastating?
                                      (apply merge-with +
                                             (pmap #(sorted-map (+ (first %) (second %))
                                                                (*
                                                                  (get wounds-by-failed-save-test (first %) 0.0)
                                                                  (get critical-wound-probabilities (second %) 0.0)))

                                                   (filter #(>= maximal-hits (+ (first %) (second %)))
                                                           (combo/cartesian-product (distinct (map #(apply + %) hit-combinations))
                                                                                    (range 0 (inc maximal-hits))))))
                                      wounds-by-failed-save-test
                                      )
        expected-damage (apply + (map #(* (* damage (key %)) (val %)) received-wounds-probability))
        expected-wounds (apply + (map #(* (key %) (val %)) received-wounds-probability))
        wounds-needed-to-kill-ratio (min 1 (/ damage wounds))]

    (merge hit-probability-informations
           (sorted-map
             :attacks attacks
             :damage damage
             :anti anti
             :strength strength
             :toughness toughness
             :wounds-needed-to-kill wounds-needed-to-kill-ratio
             :hit-combinations hit-combinations
             :maximal-hits maximal-hits
             :wound-probability wound-probability
             :critical-wound-probability critical-wound-probability
             :wound-probabilities wound-probabilities
             :critical-wound-probabilities critical-wound-probabilities
             :pass-save-test-probability pass-save-test-probability
             :wounds-by-failed-save wounds-by-failed-save-test
             :received-wounds-probability received-wounds-probability
             :received-damage-probability (reduce #(assoc %1 (* damage (key %2)) (val %2)) (sorted-map) received-wounds-probability)
             :expected-damage expected-damage
             :expected-wounds expected-wounds
             :expected-kills (* expected-wounds wounds-needed-to-kill-ratio)
             :min-wound-probability (get-ccdf-for received-wounds-probability)
             ))))