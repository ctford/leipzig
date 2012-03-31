(ns overtunes.songs.row-row-row-your-boat
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn sum-n [series n] (reduce + (take n series)))
(def major-scale (partial sum-n (cycle [2 2 1 2 2 2 1])))
(def g-major #(-> % major-scale (+ 67))) 

(defn bpm [beats start] #(-> % (/ beats) (* 60) (* 1000) (+ start)))
(defn after [timing beats] #(-> % (+ beats) timing))
(defn syncopate [timing durations] #(-> (sum-n durations %) timing))

(def pitches [0 0 0 1 2, 2 1 2 3 4, 7 7 7 4 4 4 2 2 2 0 0 0, 4 3 2 1 0])
(def durations [1 1 2/3 1/3 1, 2/3 1/3 2/3 1/3 2, 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3 1/3, 2/3 1/3 2/3 1/3 2])

(defn melody# [timing notes] 
  (let [note# #(at (timing %1) (sampled-piano (g-major %2)))]
    (dorun (map-indexed note# notes)))) 

(defn play# []
  (let [timing (bpm 150 (now))
        rhythm-from #(syncopate (after timing %) durations)]
    (melody# (rhythm-from 0)  pitches)
    (melody# (rhythm-from 16) pitches)
    (melody# (rhythm-from 20) pitches)
    (melody# (rhythm-from 24) pitches)
    (melody# (rhythm-from 28) pitches)
    ))

(play#)
