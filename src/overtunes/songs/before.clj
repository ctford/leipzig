(ns overtunes.songs.before
  (:use [overtone.live])
  (:use [overtunes.instruments.organ-cornet]))

(def melody
  "A crude five-note melody that will constantly fall out of time when played
  over chords at a rate of two notes per chord." 
  [:C3 :Bb3 :Eb3 :G3 :Eb3])

(def start
  "In which we establish the key and give the syncopated relationship between
  the melody and the chords time to become apparent." 
  [(chord :Eb3 :major)
   (chord :Eb3 :major)
   (chord :C3  :minor7)
   (chord :C3  :minor7)
   (chord :Eb3 :major)
   (chord :Eb3 :major)
   (chord :C3  :minor7)
   (chord :C3  :minor7)])

(def middle
  "In which we let the melody combine in interesting ways with a developing
  progression that builds and then resolves."
  [(chord :G3  :minor)
   (chord :G3  :minor)
   (chord :Ab3 :major)
   (chord :Bb3 :major)
   (chord :Eb4 :major)
   (chord :Eb4 :major)
   (chord :Bb3 :major)
   (chord :Bb3 :major)])

(def variation
  "In which we elaborate on the middle section."
  [(chord :G3  :minor)
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
   (chord :Bb3 :major)])

(def finish
  "In which we return to root for resolution at the end of the piece."
  [(chord :Eb4 :major)
   (chord :Eb4 :major)])

(defn beat-length
  "Determines the beat length in milliseconds of metro."
  [metro] (- (metro 1) (metro 0))) 

(defn play-note
  "Plays a single tone on instrument for duration, assuming an instrument that
  takes a frequency in Hz and a duration in seconds.
  (play-note :C4 organ-cornet 1.2)"
  [tone instrument duration]
  (let [seconds (/ duration 1000)
        frequency (midi->hz (note tone))]
    (instrument frequency seconds)))

(defn play-chord [tones instrument duration]
  "Plays a seq of tones as a chord on instrument for duration.
  (play-chord (chord :C4 :major) organ-cornet 1.2)"
  (if (not (empty? tones))
    (let [root (first tones)
          bass [(- root 12) (- root 24)]
          with-bass (concat bass tones)]
      (doall (map (fn [tone] (play-note tone instrument duration)) with-bass)))))

(defn metronome-from
  "Returns a metronome that measures beats relative to start."
  ([metro start] (fn
    ([] (metro (+ 0 start)))
    ([beat] (metro (+ beat start))))))

(defn play-progression [progression metro]
  "Plays a seq of chords for two beats each on the cornet.
  Takes a relative metronome in addition to the chord progression.
  (play-progression [root fourth fifth] metro)"
  (let [beats-per-chord 2
        duration (* beats-per-chord (beat-length metro))]
    (when-not (empty? progression)
      (at (metro) (play-chord (first progression) organ-cornet duration))
      (play-progression (rest progression)
        (metronome-from metro beats-per-chord)))))

(defn play-melody [melody metro]
  "Plays a seq of notes on the cornet.
  Takes a relative metronome in addition to the melody.
  (play-melody [:C3 :E3 :G3] metro)"
  (let [duration (beat-length metro)]
    (when-not (empty? melody)
      (at (metro) (play-note (first melody) organ-cornet duration))
      (play-melody (rest melody) (metronome-from metro 1)))))

(defn cycle-n
  "Returns a new seq which is cycled n times.
  (cycle-n 2 [1 2 3]) ;=> [1 2 3 1 2 3]"
  [n s]
  (take (* (count s) n) (cycle s)))

(defn play
  "Play the melody over the chords to the relative metro's time."
  [chords metro]
  (let [repetitions-per-chord (/ (count melody) 2) 
        melody-line (cycle-n (/ (count chords) repetitions-per-chord) melody)] 
  (play-melody melody-line metro)
  (play-progression (concat chords finish) metro)))

(defn full-version []
  "The full version of 'Before', grave."
  (play (concat start middle start middle variation) (metronome 30)))

(defn short-version []
  "A short version of 'Before', adante."
  (play variation (metronome 100)))
