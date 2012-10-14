(ns whelmed.scale
    (:use
          [overtone.live :only [at ctl midi->hz now]]))

(defmacro defs {:private true} [names values]
  `(do ~@(map
     (fn [name value] `(def ~name ~value))
     names (eval values))))

(defn- sum-n [series n] (reduce + (take n series)))

(defn scale [intervals]
   #(if-not (neg? %)
     (sum-n (cycle intervals) %)
     ((comp - (scale (reverse intervals)) -) %)))

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
