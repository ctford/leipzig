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

(def rhythm
  (let [chord (fn [degree] (map #(zipmap [:time :duration :pitch] [0 2 %]) (triad-from degree)))]
  ((after 1) (follow (times (chord 14) 2) (times (chord 12) 2)))))

(defmulti play :part)
;(defmethod play :melody [note] (play-on# (comp sinish# midi->hz) [note]))
;(defmethod play :rhythm [note] (play-on# (comp #(groan# % 10) midi->hz) [note]))
;(defmethod play :bass [note] (play-on# (comp sinish# midi->hz) [note]))
(defmethod play :default [note] (play-on# piano# [note]))

(def lower #(- % 7))

(defn all [f] #(dorun (map f %)))

(=>
  (times (concat rhythm bass) 4)
  (skew :pitch (comp E minor lower lower))
  (skew :time (bpm 150))
  (skew :duration (bpm 150))
  (skew :time #(+ % (now)))
  (all play))
