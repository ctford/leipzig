(ns overtunes.songs.sidhe
  (:use [overtone.live])
  (:use [overtunes.core])
  (:use [overtunes.instruments.organ-cornet]))

(defn zip [progression basses] (map cons basses progression))
(defn with-bass [chord] (cons (- (first chord) 12) chord))

(def descending-C-minor-scale [:C3 :Bb2 :Ab2 :G2 :F2 :Eb2 :D2 :C2])

(def A-young-man-once (zip
  [(chord :C4 :minor)
   (chord :G3 :minor)
   (chord :F3 :minor)
   (chord :G3 :major)
   (chord :F3 :minor)
   (chord :Eb3 :major)
   (chord :G3 :minor)
   (chord :C3 :minor)]
  
   descending-C-minor-scale))

(def Viola-instrumental-break (map with-bass
  [(chord :C4 :minor)
   (chord :G3 :minor)
   (chord :F3 :minor)
   (chord :F3 :minor)
   (chord :Bb3 :major)
   (chord :A3 :major)
   (chord :D4 :minor)
   (chord :D4 :minor)]))

(def At-last-he-reached (map with-bass
  [(chord :Bb3 :major)
   (chord :A3 :major)
   (chord :D4 :minor)
   (chord :D4 :minor)
   (chord :Bb3 :major)
   (chord :A3 :major)
   (chord :D3 :minor)
   (chord :D3 :minor)]))

(defn sidhe [] (let
  [m (metronome 120)
   play-bars (fn [progression metro] (play-progression
     [progression (take (count progression) (cycle [4/1]))]
     organ-cornet
     metro))]
  (play-bars A-young-man-once (metronome-from m (* 0 4)))
  (play-bars A-young-man-once (metronome-from m (* 8 4)))
  (play-bars Viola-instrumental-break (metronome-from m (* 16 4)))
  (play-bars At-last-he-reached (metronome-from m (* 24 4)))))
