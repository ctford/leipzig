(ns whelmed.scale
    (:use
       [clojure.math.numeric-tower :only [floor]]
       [overtone.live :only [at ctl midi->hz now]]))

(defmacro defs {:private true} [names values]
  `(do ~@(map
     (fn [name value] `(def ~name ~value))
     names (eval values))))

(defn- sum-n [series n] (reduce + (take n series)))

(defn- number-class [number]
  (cond 
    (not= number (floor number)) :fraction
    (< number 0)                 :negative
    :otherwise                   :natural))

(defmulti scale-of (fn [_ degree] (number-class degree)))
(defn scale [intervals] (partial scale-of intervals))

(defmethod scale-of :natural [intervals degree]
  (sum-n (cycle intervals) degree))
(defmethod scale-of :negative [intervals degree]
  (->> degree - (scale-of (reverse intervals)) -))
(defmethod scale-of :fraction [intervals degree]
  (->> degree floor (scale-of intervals) inc))

(def major (scale [2 2 1 2 2 2 1]))
(def blues (scale [3 2 1 1 3 2]))
(def pentatonic (scale [3 2 2 3 2]))
(def chromatic (scale [1]))

(defn- start-from [base] (partial + base))

(defs [sharp flat] [inc dec])
(defs [C D E F G A B]
  (map
    (comp start-from (start-from 60) major)
    (range)))

(defn mode [scale n] (comp #(- % (scale n)) scale (start-from n)))

(defs
  [ionian dorian phrygian lydian mixolydian aeolian locrian]
  (map (partial mode major) (range)))

(def minor aeolian)

(defn jazz
  ([] (map #(* 2 %) (range)))
  ([root] (map #(+ root %) (jazz))))

(def triad
  (comp
    (partial take 3)
    jazz))

(def seventh 
  (comp
    (partial take 4)
    jazz))

(def low #(- % 7))
(def lower low)
(def raise #(+ % 7))
