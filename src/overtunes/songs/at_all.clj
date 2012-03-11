(ns overtunes.songs.at-all
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn bpm [beats-per-minute] 
  (let [start (now)
        ms-per-minute (* 60 1000)
        ms-per-beat (/ ms-per-minute beats-per-minute)]
    #(+ start (* ms-per-beat %))))

(defn from [timing offset] #(timing (+ offset %)))
(defn speed-up [timing factor] #(timing (/ % factor)))

(def scale 56)
(defn ground [note] (+ scale note))

(def note# (comp sampled-piano ground))
(defn chord# [chord] (doseq [note (vals chord)] (note# note))) 

(def ionian #(let [interval (mod % 7)
                  note ([0 2 4 5 7 9 11] interval)
                  octave (quot (- % interval) 7)]
               (+ (* 12 octave) note)))

(defn triad [scale root]
  (zipmap [:i :iii :v]
          [(scale root)
           (scale (+ root 2))
           (scale (+ root 4))])) 

(defn lower [note] (- note 12))
(defn with-base [chord]
  (assoc chord :base
         (lower (:i chord))))

(def I (with-base (triad ionian 0)))
(def II (with-base (triad ionian 1)))
(def V (with-base (triad ionian 4)))

(def progression [I I II II II V I (update-in V [:base] lower)])

(defn rhythm-n-bass# [timing [chord1 chord2 & chords]]
  (do
    (at (timing 0) (note# (:base chord1)))
    (at (timing 2) (chord# (dissoc chord1 :base)))
    (at (timing 3) (note# (:base chord1)))
    (at (timing 4) (note# (:base chord2)))
    (at (timing 6) (chord# (dissoc chord2 :base)))
    (if chords
      (rhythm-n-bass# (from timing 8) chords))))

(defn even-melody# [timing [note & notes]]
  (do
    (at (timing 0) (note# note))
    (if notes
      (even-melody# (from timing 1) notes))))

(defn intro# [timing] 
    (rhythm-n-bass# timing (take 8 (cycle progression)))
    (even-melody# timing (take 32 (cycle [9 7]))))

(defn first-bit# [timing]
    (rhythm-n-bass# timing (take 8 (cycle progression)))
    (even-melody# (speed-up (from timing -1) 2) (map ionian [2 4 5 4 4 2 4]))
    (even-melody# (speed-up (from timing 7) 2) (map ionian [-2 1 2 1 1 -2 1]))
    (even-melody# (speed-up (from timing 15) 2) (map ionian [-2 1 2 1 1 -2 1 2 3 4]))
    (even-melody# (speed-up (from timing 23) 2) (map ionian [-1 -2 -3 0 0 -3 0 1 0 -3])))

(defn play# []
  (let [timing (from (bpm 120) 2)] ; Lead in with two beats so that everything starts at once.
    (intro# timing)
    (first-bit# (from timing 32))
    (first-bit# (from timing 64))))

; (play#)
