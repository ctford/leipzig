(ns overtunes.songs.before
  (:use [overtone.live])
  (:use [overtunes.core])
  (:use [overtunes.instruments.organ-cornet]))

(def melody
  "A crude five-note melody that will constantly fall out of time when played
  over chords at a rate of two notes per chord." 
  [[:C3 :Bb3 :Eb3 :G3 :Eb3]
   [1/1 1/1  1/1  1/1 1/1]])

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

(defn with-bass
  "Prepends an octave stretch to chord.
  (with-bass (chord :C4 :major))"
  [chord]
  (let [root (first chord)
        bass [(- root 12) (- root 24)]]
    (concat bass chord)))

(defn cycle-n
  "Returns a new seq which is cycled n times.
  (cycle-n 2 [1 2 3]) ;=> [1 2 3 1 2 3]"
  [n s]
  (take (* (count s) n) (cycle s)))

(defn play-chords
  "Plays a seq of chords for two beats each on the cornet.
  Takes a relative metronome in addition to the chord progression.
  (play-chords [root fourth fifth] metro)"
  [chords metro]
  (let [bassed-chords (map with-bass chords)
        weighted-chords [bassed-chords (cycle-n (count chords) [2/1])]] 
    (play-progression weighted-chords organ-cornet metro)))

(defn cycle-melody
  "Returns a new melody which is cycled n times.
  (cycle-n 2 [[:C3 :D3 :E3][1/2 3/2 1]]) ;=>
    [[:C3 :D3 :E3 :C3 :D3 :E3][1/2 3/2 1/1 1/2 3/2 1/1]]"
  [n m]
  [(cycle-n n (first m)) (cycle-n n (second m))])

(defn play
  "Play the melody over the chords to the relative metro's time."
  [chords metro]
  (let [reps-per-chord (/ (count (first melody)) 2) 
        melody-line (cycle-melody (/ (count chords) reps-per-chord) melody)] 
    (play-melody melody-line organ-cornet metro)
    (play-chords (concat chords finish) metro)))

(defn before []
  "The full version of 'Before', grave."
  (play (concat start middle start middle variation) (metronome 30)))

(defn before-short []
  "A short version of 'Before', adante."
  (play variation (metronome 100)))
