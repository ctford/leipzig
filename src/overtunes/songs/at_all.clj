(ns overtunes.songs.at-all
  (:use
    [overtone.live :exclude [scale octave sharp flat sixth unison play]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(def scale 66)
(defn ground [note] (+ scale note))

(def note# (comp sampled-piano ground))
(defn chord# [chord] (doseq [note (vals chord)] (note# note))) 

(def ionian {:i 0, :ii 2, :iii 4, :iv 5, :v 7, :vi 9, :vii 11, :viii 12})
(def i (select-keys ionian [:i :iii :v]))
(def ii (select-keys ionian [:ii :iv :vi]))

(chord# ii)


















