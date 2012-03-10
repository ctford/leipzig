(ns overtunes.songs.at-all
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn bpm [beats-per-minute] 
  (let [start (now)
        ms-per-minute (* 60 1000)
        ms-per-beat (/ ms-per-minute beats-per-minute)]
    #(+ start (* ms-per-beat %))))

(defn from [timing offset] #(timing (+ offset %)))

(def scale 56)
(defn ground [note] (+ scale note))

(def note# (comp sampled-piano ground))
(defn chord# [chord] (doseq [note (vals chord)] (note# note))) 

(def ionian (cycle [0 2 4 5 7 9 11]))
(defn triad [scale root]
  (zipmap [:i :iii :v]
          [(nth scale root)
           (nth scale (+ root 2))
           (nth scale (+ root 4))])) 

(def I (triad ionian 0))
(def II (triad ionian 1))
(def V (triad ionian 4))

(def progression [I {} II {} II V I V])

(defn rythm-n-bass# [timing chords]
  (do
    (at (timing 0) (chord# (first chords)))
    (rythm-n-bass# (from timing 4) (rest chords))))

(rythm-n-bass# (bpm 130) (cycle progression))
