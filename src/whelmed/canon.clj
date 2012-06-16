(ns whelmed.canon 
  (:use [whelmed.melody]))

(defn canon [f] (fn [notes] (concat notes (f notes))))

(defn skew [k f] (fn [points] (map #(update-in % [k] f) points)))
(defn shift [point] (fn [points] (map #(->> % (map + point) vec) points)))

(defn simple [wait] (shift [wait 0 0]))
(defn interval [interval] (shift [0 interval 0]))
(def mirror (skew pitch -))
(def crab (skew timing -))
(def table (comp mirror crab))
