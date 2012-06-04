(ns goldberg.melody)

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(def timing 0)
(def pitch 1)
(def duration 2)
