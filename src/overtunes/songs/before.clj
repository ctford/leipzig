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

(defn beat-length [metro] (- (metro 1) (metro 0))) 

(defn play-note [tone instrument duration]
  (let [
    seconds (/ duration 1000)
    frequency (midi->hz (note tone))
  ]
    (instrument frequency seconds)
  )
)

(defn play-chord [tones instrument duration]
  (if (not (empty? tones))
    (let [
      root (first tones)
      bass [(- root 12) (- root 24)]
      with-bass (concat bass tones)
    ]
      (mix (map (fn [tone] (play-note tone instrument duration)) with-bass))
    )
  )
)

(defn play-progression [progression metro start]
  (let [
    beats-per-chord 4
    duration (* beats-per-chord (beat-length metro))
  ]
    (if (not (empty? progression)) ( do
      (at (metro start) (play-chord (first progression) organ-cornet duration))
      (play-progression (rest progression) metro (+ start beats-per-chord))
    ))
  )
)

(defn play-melody [melody metro start]
  (let [
    beats-per-note 2
    duration (* beats-per-note (beat-length metro))
  ]
    (if (not (empty? melody)) ( do
      (at (metro start) (play-note (first melody) organ-cornet duration))
      (play-melody (rest melody) metro (+ start beats-per-note))
    ))
  )
)

(defn n-times [n items] (flatten (repeat n items)))

(defn play [chords metro] ( do 
  (play-melody (n-times (/ (count chords) 2.5) melody) metro (metro))
  (play-progression (concat chords finish) metro (metro))
))

(defn full-version [] (play (concat start middle start middle variation) (metronome 60)))
(defn short-version [] (play variation (metronome 200)))
