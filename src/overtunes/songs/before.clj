(ns overtunes.songs.before
  (:use [overtone.live])
  (:use [overtunes.instruments.organ-cornet])
)

(def starting-section [
  (chord :Eb3 :major)
  (chord :Eb3 :major)
  (chord :C3  :minor7)
  (chord :C3  :minor7)
  (chord :Eb3 :major)
  (chord :Eb3 :major)
  (chord :C3  :minor7)
  (chord :C3  :minor7)
])

(def middle-section [
  (chord :G3  :minor)
  (chord :G3  :minor)
  (chord :Ab3 :major)
  (chord :Bb3 :major)
  (chord :Eb4 :major)
  (chord :Eb4 :major)
  (chord :Bb3 :major)
  ()
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
  ()
])

(defn play-chord [notes duration] ( do 
  (defn play-note [note] (organ-cornet note (/ duration 1000)))
  (if (not (empty? notes))
    (let [
      root (first notes)
      bass [(- root 12) (- root 24)]
      with-bass (concat bass notes)
    ]
      (mix (map play-note (map midi->hz with-bass)))
    )
  )
))

(def metro (metronome 90))
(defn beat-length [metro] (- (metro 1) (metro 0))) 

(defn play-progression [progression metro start] ( do
  (if (not (empty? progression)) (do
    (at (metro (+ start 0)) (play-chord (nth progression 0) (* 4 (beat-length metro))))
    (play-progression (rest progression) metro (+ start 4))
  ))
))

(defn play [] ( 
  (play-progression
    (concat
      starting-section
      middle-section
      starting-section
      middle-section
      variation-section
    )
    metro
    (metro)
  )
))
