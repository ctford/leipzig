(ns goldberg.melody
  (:use
    [overtone.live :only [at ctl]]))

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(def timing 0)
(def pitch 1)
(def duration 2)

(defn play-on# [instrument# notes]
  (let [play-at# (fn [[timing pitch duration]]
                   (let [id (at timing (instrument# pitch))]
                     (at (+ timing duration) (ctl id :gate 0))))]
    (->> notes (map play-at#) dorun)))
