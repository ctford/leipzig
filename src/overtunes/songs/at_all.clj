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

(def ionian {:i 0, :ii 2, :iii 4, :iv 5, :v 7, :vi 9, :vii 11, :viii 12})
(def I (select-keys ionian [:i :iii :v]))
(def II (select-keys ionian [:ii :iv :vi]))
(def V (select-keys ionian [:v :vii :ii]))

(def progression [I {} II {} II V I V])

(defn rythm-n-bass# [timing chords]
  (do
    (at (timing 0) (chord# (first chords)))
    (rythm-n-bass# (from timing 4) (rest chords))))

; (rythm-n-bass# (bpm 130) (cycle progression))
