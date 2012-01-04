(ns overtunes.songs.before
  (:use [overtone.live])
  (:use [overtunes.instruments.organ-cornet])
)

(def melody
  [:C3 :Bb3 :Eb3 :G3 :Eb3]
)

(def start [
  (chord :Eb3 :major)
  (chord :Eb3 :major)
  (chord :C3  :minor7)
  (chord :C3  :minor7)
  (chord :Eb3 :major)
  (chord :Eb3 :major)
  (chord :C3  :minor7)
  (chord :C3  :minor7)
])

(def middle [
  (chord :G3  :minor)
  (chord :G3  :minor)
  (chord :Ab3 :major)
  (chord :Bb3 :major)
  (chord :Eb4 :major)
  (chord :Eb4 :major)
  (chord :Bb3 :major)
  (chord :Bb3 :major)
])

(def variation [
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

(def finish [
  (chord :Eb4 :major)
  (chord :Eb4 :major)
])

(defn play-chord [notes duration] ( do 
  (defn play-note [note] (organ-cornet note (/ duration 1000) 0.7))
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
    (at (metro (+ start 0)) (play-chord (first progression) (bar-length metro)))
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

(defn n-times [n items] 
  (flatten (repeat n items))
)

(defn play [] ( do
  (def chords
    (concat
      start
      middle
      start
      middle
      variation
    )
  )
  (play-melody (n-times (/ (count chords) 2.5) melody) metro (metro))
  (play-progression
    (concat chords finish)
    metro
    (metro)
  )
))
