(ns overtunes.songs.before
  (:use [overtunes.pitch])
  (:use [overtunes.core])
  (:use [overtone.core :only [metronome]])
  (:use [overtunes.instruments.organ-cornet]))

(def melody
  "A crude five-note melody that will constantly fall out of time when played
  over chords at a rate of two notes per chord." 
  [[(C 4) (Bb 4) (Eb 4) (G 4) (Eb 4)]
   [ 1/1   1/1    1/1    1/1   1/1]])

(def start
  "In which we establish the key and give the syncopated relationship between
  the melody and the chords time to become apparent." 
  [(Eb 5 major)
   (Eb 5 major)
   (C 5 minor seventh)
   (C 5 minor seventh)
   (Eb 5 major)
   (Eb 5 major)
   (C 5 minor seventh)
   (C 5 minor seventh)])

(def middle
  "In which we let the melody combine in interesting ways with a developing
  progression that builds and then resolves."
  [(G 5 minor)
   (G 5 minor)
   (Ab 5 major)
   (Bb 5 major)
   (Eb 6 major)
   (Eb 6 major)
   (Bb 5 major)
   (Bb 5 major)])

(def variation
  "In which we elaborate on the middle section."
  [(G 5 minor)
   (G 5 minor)
   (Ab 5 major)
   (Ab 5 major)
   (G 5 minor)
   (G 5 minor)
   (Ab 5 major)
   (Ab 5 major)
   (G 5 minor)
   (G 5 minor)
   (Ab 5 major)
   (Bb 5 major)
   (Eb 6 major)
   (Eb 6 major)
   (Bb 5 major)
   (Bb 5 major)])

(def finish
  "In which we return to root for resolution at the end of the piece."
  [(Eb 6 major)
   (Eb 6 minor seventh)
   (Eb 6 major)])

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
  (let [bassed-chords chords
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
  (play (concat start middle start middle variation)
        (metronome-from (metronome 30) 1)))

(defn before-short []
  "A short version of 'Before', adante."
  (play variation (metronome 100)))

(before)
