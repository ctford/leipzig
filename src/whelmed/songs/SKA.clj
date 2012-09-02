(ns whelmed.songs.SKA
  (:use
        [whelmed.melody]
        [whelmed.scale]
        [whelmed.instrument]
        [overtone.live :only [midi->hz now stop]]))

(defn => [value & fs] (reduce #(%2 %1) value fs))

(defn arrange [notes part] (map #(assoc % :part part) notes))

(def bass
  (arrange
    (follow
      (phrase
        [3/2 1 1/2 1]
        [0   0   2 4])
      (phrase
        [3/2 1 1/2 1]
        [5   5   4 2]))
  :bass))

(def rhythm
  (arrange
      (phrase
        [2 6]
        [14 18])
    :rhythm))


(defn triad-from [degree] (map #(+ degree %) triad))
(def chords (map triad-from [0 5]))

(defmulti play :part)
(defmethod play :melody [note] (play-on# (comp sinish# midi->hz) [note]))
(defmethod play :rhythm [note] (play-on# (comp #(groan# % 10) midi->hz) [note]))
(defmethod play :bass [note] (play-on# (comp sinish# midi->hz) [note]))

(defn in-key [k] (skew pitch k))
(defn in-time [t] (skew timing t))
(def lower #(- % 7))

(defn all [f] #(dorun (map f %)))

(=> (times (concat bass rhythm) 4)
  (in-key (comp E minor lower lower))
  (in-time (bpm 140))
  (in-time #(+ % (now)))
  (all play))
