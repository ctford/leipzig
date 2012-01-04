(ns overtunes.songs.before
  (:use [overtone.live])
  (:use [overtunes.instruments.organ-cornet])
)

(def melody
  [:C4 :Bb4 :Eb4 :G4]
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
  (chord :Bb3 :major)
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

(def metro (metronome 60))
(def bar 4)
(defn beat-length [metro] (- (metro 1) (metro 0))) 
(defn bar-length [metro] (* (beat-length metro) bar))

(defn play-progression [progression metro start] ( do
  (if (not (empty? progression)) (do
    (at (metro (+ start 0)) (play-chord (nth progression 0) (bar-length metro)))
    (play-progression (rest progression) metro (+ start bar))
  ))
))

(defn play-melody [melody metro start] ( do
  (def duration (/ (* 2 (beat-length metro)) 1000))
  (defn play-note [note] (organ-cornet note duration))
  (if (not (empty? melody)) (do
    (at (metro start) (play-note (midi->hz (note (first melody)))))
    (play-melody (rest melody) metro (+ start 2))
  ))
))

(defn n-times [items n] 
  (flatten (repeat n items))
)

(defn play [] ( do
  (def chords
    (concat
      starting-section
      middle-section
      starting-section
      middle-section
      variation-section
    )
  )
  (play-melody (concat (n-times melody (/ (count chords) 2)) [:Eb4]) metro (metro))
  (play-progression
    chords
    metro
    (metro)
  )
))
