(ns leipzig.scale
  (:require [clojure.math.numeric-tower :as math]))

(defmacro defs {:private true} [names values]
  `(do ~@(map
     (fn [name value] `(def ~name ~value))
     names (eval values))))

(defn- sum-n [series n] (apply + (take n series)))

(defmulti scale-of
  (fn [intervals degree]
    (cond 
      (not= degree (math/floor degree)) :fraction
      (neg? degree)                     :negative
      :otherwise                        :natural)))

(defn scale [intervals] (partial scale-of intervals))

(defmethod scale-of :natural [intervals degree]
  (sum-n (cycle intervals) degree))
(defmethod scale-of :negative [intervals degree]
  (->> degree - (scale-of (reverse intervals)) -))
(defmethod scale-of :fraction [intervals degree]
  (let [lower (scale-of intervals (math/floor degree))
        upper (scale-of intervals (math/ceil degree))
        fraction (- degree (math/floor degree))]
  (+ lower (* fraction (- upper lower)))))

(def major (scale [2 2 1 2 2 2 1]))
(def blues (scale [3 2 1 1 3 2]))
(def pentatonic (scale [3 2 2 3 2]))
(def chromatic (scale [1]))

(defn- from [base] (partial + base))

(defs [sharp flat] [inc dec])
(defs [C D E F G A B]
  (map
    (comp from (from 60) major)
    (range)))

(defn mode [scale n] (comp #(- % (scale n)) scale (from n)))

(defs
  [ionian dorian phrygian lydian mixolydian aeolian locrian]
  (map (partial mode major) (range)))

(def minor aeolian)

(defn low [midi] ((from -12) midi))
(defn high [midi] ((from 12) midi))
