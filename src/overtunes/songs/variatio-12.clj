(ns overtunes.songs.variatio-12
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano}]))

(defn sum-n [series n] (reduce + (take n series)))
(defn scale [intervals]
  #(if (neg? %)
     (- ((scale (reverse intervals)) (- %)))
     (sum-n (cycle intervals) %)))

(def major (scale [2 2 1 2 2 2 1]))
(def g-major #(-> % major (+ 67))) 

(defn bpm [per-minute] #(-> % (/ per-minute) (* 60) (* 1000)))
(defn translate [f x y] #(-> % (+ x) f (+ y)))
(def from #(translate %1 0 %2))
(def after #(translate %1 %2 0))
(defn syncopate [timing durations] #(->> % (sum-n durations) timing))


(def pitches (flatten [-7 0 (range -1 4) (range 2 -1 -1)]))
(def durations (flatten [1/2 (repeat 2 1/4) 1/2 (repeat 6 1/4)])) 

(defn melody# [timing notes] 
  (let [note# #(at (timing %1) (piano (g-major %2)))]
    (dorun (map-indexed note# notes)))) 

(defn play# []
  (let [timing (-> (bpm 120) (from (now)))
        rhythm-from #(syncopate (after timing %) durations)]
    (melody# (rhythm-from 0)  pitches)
    ))

(play#)
