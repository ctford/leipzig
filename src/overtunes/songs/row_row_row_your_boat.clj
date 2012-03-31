(ns overtunes.songs.row-row-row-your-boat
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn sum-up-to [series n] (apply + (take n series)))
(def major-scale #(sum-up-to (cycle [2 2 1 2 2 2 1]) %))
(def g-major #(-> % major-scale (+ 67))) 

(defn bpm [beats start] #(-> % (/ beats) (* 60) (* 1000) (+ start)))
(defn after [timing beats] #(timing (+ beats %)))
(defn syncopate [timing lengths] #(timing (sum-up-to lengths %)))

(def pitches [0 0 0 1 2, 2 1 2 3 4, 7 7 7 4 4 4 2 2 2 0 0 0, 4 3 2 1 0])
(def durations [1 1 2/3 1/3 1, 2/3 1/3 2/3 1/3 2, 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3, 2/3 1/3 2/3 1/3 2])

(defn melody# [timing notes] 
  (let [note# #(at (timing %1) (sampled-piano (g-major %2)))]
    (dorun (map-indexed note# notes)))) 

(defn play# []
  (let [timing (bpm 150 (now))
        rhythm #(syncopate % durations)]
    (melody# (rhythm (after timing 0)) pitches)
    (melody# (rhythm (after timing 16)) pitches)
    (melody# (rhythm (after timing 20)) pitches)
    (melody# (rhythm (after timing 24)) pitches)
    (melody# (rhythm (after timing 28)) pitches)
    ))

(play#)
