(ns whelmed.melody
  (:use
    [overtone.live :only [at ctl midi->hz now]]))

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(def timing 0)
(def pitch 1)
(def duration 2)
(defn skew [k f] (fn [points] (map #(update-in % [k] f) points)))
(defn shift [point] (fn [points] (map #(->> % (map + point) vec) points)))

(defn play-on# [instrument# notes]
  (let [play-at# (fn [[timing pitch duration]]
                   (let [id (at timing (instrument# pitch))]
                       (at (+ timing duration) (ctl id :gate 0))))]
    (->> notes (map play-at#) dorun)))

(defn after [wait] (shift [wait 0 0]))
(defn follow
    ([first second] (follow first 0 second))
    ([first gap second]
         (let [[timing _ duration] (last first)
                                 shifted ((after (+ duration gap timing)) second)]
                     (concat first shifted))))
(defn times [phrase n] (reduce follow (repeat n phrase)))

