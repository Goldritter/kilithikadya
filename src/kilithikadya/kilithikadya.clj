(ns kilithikadya.kilithikadya
  (:require [clojure.math.combinatorics :as combo])
  (:import [org.apache.commons.statistics.distribution BinomialDistribution]))

(def critical-prob 1/6)

(defn get-ccdf-for [probability-map]
  (loop [ccdf-map   (sorted-map)
         sorted-kvs (sort-by key probability-map)]
    (if (empty? sorted-kvs)
      ccdf-map
      (recur (assoc ccdf-map (key (first sorted-kvs)) (apply + (map val sorted-kvs)))
             (rest sorted-kvs)))))

(defn get-roll-probability [min-roll]
  (if (= 0 min-roll) 0
      (- 1
         (/  (dec min-roll) 6))))

(defn get-save-probability [save invul-save ap save-mod]
  (let [min-roll          (max 2 (+ save (max ap (* -1 ap)) (min (max -1 (* -1 save-mod)) (* -1 save-mod))))
        invul-probability (get-roll-probability invul-save)]
    (if (< 6 min-roll)
      invul-probability
      (max invul-probability (get-roll-probability min-roll)))))

(defn get-wound-probability
  [strength toughness & {:keys [wound-mod anti reroll? consider-critical?]
                         :or   {wound-mod          0
                                anti               0
                                reroll?            false
                                consider-critical? true}}]
  (let [base-prob
        (- (->>
            (cond
              (<= (* 2 toughness) strength) 2
              (<  toughness strength) 3
              (= toughness strength) 4
              (<= strength (* 1/2 toughness)) 6
              :default 5)
            (+ (max -1 (min 1 wound-mod) wound-mod))
            (min 6)
            (max 2)
            get-roll-probability
            (max (get-roll-probability anti)))
           (if consider-critical? 0 critical-prob))]
    (if reroll?
      (+ base-prob (* (- 1 base-prob) base-prob))
      base-prob)))

(defn get-passed-wound-probability [save-probability wounds wound-probability]
  (if (= 0 wounds) (sorted-map 0 (* 1.0 wound-probability))
      (let [fail-save-test-probability-density (BinomialDistribution/of wounds (- 1.0 save-probability))]
        (reduce #(assoc %1 %2 
                        (* wound-probability (.probability fail-save-test-probability-density %2))) 
                (sorted-map) (range 0 (inc wounds))))))

(defn get-to-hit-probability [skill skill-mod reroll? consider-critical?]
  (let [base-prob (- (->> (- skill (max -1 (min 1 skill-mod)))
                          (min 6)
                          (max 2)
                          get-roll-probability)
                     (if consider-critical? 0 critical-prob))]
    (if reroll? (+ base-prob  (* base-prob (- 1 base-prob)))
        base-prob)))

(defn get-binominal-distribution-for-minimum-roll [dice-count min-roll]
  (BinomialDistribution/of dice-count (/ 1 (inc (- 6 min-roll)))))

(defn get-hit-probabilities [& {:keys [attacks skill
                                       sustained lethal? reroll-attack?
                                       attack-mod]
                                :or   {attacks        1
                                       skill          4
                                       sustained      0
                                       lethal?        false
                                       reroll-attack? false
                                       attack-mod     0}}]
  (let [separate-critical-hits?       (or lethal? (not= 0 sustained))
        attack-range                  (range 0 (inc attacks))
        to-hit-probability            (get-to-hit-probability skill attack-mod reroll-attack? (not separate-critical-hits?))
        to-hit-critically-probability (if separate-critical-hits? (get-to-hit-probability 6 0 reroll-attack? true) 0)
        

        to-hit-distribution           (BinomialDistribution/of attacks to-hit-probability)
        to-hit-critical-distribution  (BinomialDistribution/of attacks to-hit-critically-probability)

        hits-probability-map          (apply merge-with + (map #(sorted-map (+ (first %) (* sustained (second %)))
                                                                            (* (.probability to-hit-distribution (first %))
                                                                               (.probability to-hit-critical-distribution (second %))))
                                                               (combo/cartesian-product attack-range (if (= 0 sustained) [0] attack-range))))]


    (sorted-map 
     :average-hits          (.getMean to-hit-distribution)
     :hit-probabilities               (reduce #(assoc %1 %2 (.probability to-hit-distribution %2)) (sorted-map) attack-range)
     :extra-hit-probabilites       (reduce #(assoc %1 (* sustained %2) (.probability to-hit-critical-distribution %2))
                                           (sorted-map) attack-range)
     :lethal-hits-probabilities     (if lethal? (reduce #(assoc %1 %2 (.probability to-hit-critical-distribution %2))
                                                        (sorted-map) attack-range)
                                        {0 1.0})
     :to-hit-probability            to-hit-probability 
     :to-hit-critical-probability   to-hit-critically-probability 
     :average-critical-hits (.getMean to-hit-critical-distribution)
     :average-extra-hits    (* sustained (.getMean to-hit-critical-distribution))
     :average-total-hits (+ (* sustained (.getMean to-hit-critical-distribution))
                            (.getMean to-hit-distribution))
     :hits-probability-map hits-probability-map)))


(defn get-occurence-probability-map [distribution range lethal-hits occurence-probability]
  (reduce #(assoc %1 (+ lethal-hits %2) (* occurence-probability (.probability distribution %2))) (sorted-map) range))

(defn get-probabilities-for [& {:keys [attacks skill strength ap damage
                                       toughness save invul-save wounds
                                       sustained lethal? devastating? reroll-wound? reroll-attack?
                                       attack-mod wound-mod save-mod anti]
                                :or   {attacks        1
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
                                       anti           0}}]
  (let [separate-critical-hits?                  (or lethal? (not= 0 sustained))
        separate-critical-wounds?                devastating?
        maximal-hits                             (+ attacks (* sustained attacks))

        hit-range                                (range 0 (inc maximal-hits))
        wound-probability                        (get-wound-probability strength toughness :reroll? reroll-wound?
                                                                        :wound-mod wound-mod :anit anti :consider-critical? (not separate-critical-wounds?))
        critical-wound-probability               (get-wound-probability 1 10 :consider-critical? true :reroll? reroll-wound?)
        pass-save-test-probability               (get-save-probability save invul-save ap save-mod)

        hit-probability-informations             (get-hit-probabilities :attacks attacks :attack-mod attack-mod :skill skill
                                                                        :lethal? lethal? :sustained sustained :reroll-attack? reroll-attack?)
        hit-combinations                         (filter #(>= maximal-hits (apply + %))
                                                         (combo/cartesian-product (keys (:hits-probability-map hit-probability-informations))
                                                                                  (keys (:lethal-hits-probabilities hit-probability-informations))))

        wound-probability-distributions          (apply merge
                                                        (map #(sorted-map  % (BinomialDistribution/of  % wound-probability))
                                                             hit-range))

        critical-wound-probability-distributions (apply merge
                                                        (map #(sorted-map  % (BinomialDistribution/of  % critical-wound-probability))
                                                             hit-range))


        wound-probabilities                      (apply merge-with +
                                                        (pmap #(get-occurence-probability-map
                                                                (get wound-probability-distributions (first %))
                                                                (range 0 (inc (first %)))
                                                                (second %)
                                                                (* (get-in hit-probability-informations [:hits-probability-map (first %)] 0.0)
                                                                   (get-in hit-probability-informations [:lethal-hits-probabilities (second %)] 0.0)))

                                                              hit-combinations))

        critical-wound-probabilities             (apply merge-with +
                                                        (pmap #(get-occurence-probability-map
                                                                (get critical-wound-probability-distributions %)
                                                                (range 0 (inc %))
                                                                0
                                                                (get-in hit-probability-informations [:hits-probability-map %])
                                                                )

                                                              (range 0 (inc maximal-hits))))
        
        wounds-by-failed-save-test               (apply merge-with + 
                                                        (pmap #(get-passed-wound-probability pass-save-test-probability (key %) (val %))
                                                              wound-probabilities))
        received-wounds-probability              (if separate-critical-wounds? 
                                                   (apply merge-with + 
                                                          (pmap #(sorted-map (apply + %)
                                                                             (* (get wounds-by-failed-save-test (first %) 0.0) 
                                                                                (get critical-wound-probabilities (second %) 0.0))
                                                                             )hit-combinations)) 
                                                   wounds-by-failed-save-test
                                                   )
        average-damage                           (apply + (map #(* (* damage (key %)) (val %)) received-wounds-probability))]

    (merge hit-probability-informations
           (sorted-map 
            :maximal-wounds                 maximal-hits
            :wound-probability    wound-probability
            :critical-wound-probability     critical-wound-probability
            :wound-probabilities                    wound-probabilities
            :critical-wound-probabilities          critical-wound-probabilities
            :not-pass-save-test-probability pass-save-test-probability
            :wounds-by-failed-save          wounds-by-failed-save-test
            :received-wounds-probability received-wounds-probability
            :received-damage-probability  (reduce #(assoc %1 (* damage (key %2)) (val %2)) (sorted-map) received-wounds-probability)
            :average-damage average-damage
            :average-wounds (apply + (map #(* (key %) (val %)) received-wounds-probability))
            :average-kills (/ average-damage wounds)
            :min-wound-probability (get-ccdf-for received-wounds-probability)
            ))))