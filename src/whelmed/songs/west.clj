(ns whelmed.songs.west
  (:use
    [whelmed.scale]
    [whelmed.canon]
    [whelmed.melody]
    [whelmed.instrument]
    [overtone.live :only [midi->hz now stop]]))

(defn => [value & fs] (reduce #(%2 %1) value fs))
(def progression (map #(map % triad) [i (lower v) (lower vi) (lower iii)]))

(defn lower [f] (comp #(- % 7) f))

(def backing
    (map
      #(map (partial vector %1) %2)
      [0 4 8 12]
      progression))

(defn after [wait] (shift [wait 0 0])) 

(defn west#
  [tempo scale instrument#]
  (let [start (now)
        in-time (skew timing tempo)
        in-key (skew pitch scale)
        play# (partial play-on# instrument#)]
  (=> (apply concat backing) in-time (after start) in-key play#)))

;(west# (bpm 90) (comp A aeolian) (comp recorder# midi->hz))
