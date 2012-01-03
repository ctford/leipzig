(ns overtunes.songs.before
  (:use [overtone.live])
  (:use [overtunes.instruments.organ-cornet])
)

(def middle-section [
  (chord :G3  :minor)
  (chord :G3  :minor)
  (chord :Ab3 :major)
  (chord :Bb3 :major)
  (chord :Eb4 :major)
  (chord :Eb4 :major)
  (chord :Bb3 :major)
  (chord :Bb3 :major)
])

(def starting-section [
  (chord :Eb3 :major)
  (chord :Eb3 :major)
  (chord :C3  :minor)
  (chord :C3  :minor)
  (chord :Eb3 :major)
  (chord :Eb3 :major)
  (chord :C3  :minor)
  (chord :C3  :minor)
])

(def variation-section [
  (chord :G3  :minor)
  (chord :G3  :minor)
  (chord :Ab3 :major)
  (chord :Ab3 :major)
  (chord :G3  :minor)
  (chord :G3  :minor)
  (chord :Ab3 :major)
  (chord :Ab3 :major)
  (chord :G3  :minor)
  (chord :G3  :minor)
  (chord :Ab3 :major)
  (chord :Bb3 :major)
  (chord :Eb4 :major)
  (chord :Eb4 :major)
  (chord :Bb3 :major)
  (chord :Bb3 :major)
])

(defn play-chord [notes duration] ( do 
  (defn play-note [note] (organ-cornet note (/ duration 1000)))
  (mix 
    (map play-note (map midi->hz notes))
  )
))

(def metro (metronome 200))
(defn beat-length [metro] (- (metro 1) (metro 0))) 

(defn play-progression [progression metro start] ( do
  (if (> (count progression) 0) (do
    (at (metro (+ start 0)) (play-chord (nth progression 0) (* 4 (beat-length metro))))
    (play-progression (rest progression) metro (+ start 4))
  ))
))

(defn play [] ( 
  (play-progression (concat
    starting-section
    middle-section
    variation-section
  )
  metro (metro))
))
