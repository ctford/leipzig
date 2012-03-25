(ns overtunes.songs.at-all
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn bpm [beats-per-minute start] 
  (let [ms-per-minute (* 60 1000)
        ms-per-beat (/ ms-per-minute beats-per-minute)]
    #(+ start (* ms-per-beat %))))

(defn after [timing offset] #(timing (+ offset %)))
(defn tempo [timing factor] #(timing (/ % factor)))
(defn ground [note] (+ 60 note))

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
(defn raise [note] (+ note 12))

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
    (let [next (after timing 8)]
      (if chords
        (rhythm-n-bass# next chords)
        next))))

(defn even-melody# [timing [note & notes]]
  (do
    (at (timing 0) (note# note))
    (let [next (after timing 1)]
      (if notes
        (even-melody# next notes)
        next))))

(defn intro# [timing] 
    (even-melody# timing (take 32 (cycle (map ionian [5 4]))))
    (rhythm-n-bass# timing (take 8 (cycle progression))))

(defn first-bit# [timing]
  (-> timing
    (after -1)
    (tempo 2)
    (even-melody# (map ionian [2 4 5 4 4 2 4]))
    (after 9)
    (even-melody# (map ionian [-2 1 2 1 1 -2 1]))
    (after 9)
    (even-melody# (map ionian [-2 1 2 1 1 -2 1 2 3 4]))
    (after 6)
    (even-melody# (map ionian [-1 -2 -3 0 0 -3 0 1 0 -3])))
  (rhythm-n-bass# timing (take 8 (cycle progression))))

(defn variation# [timing]
  (-> timing
    (tempo 2)
    (after 9)
    (even-melody# (map ionian [11 11 12 9 7]))
    (after 11)
    (even-melody# (map ionian [8 8 9 8 3]))
    (after 11)
    (even-melody# (map ionian [8 8 9 6 4]))
    (after 11)
    (even-melody# (map ionian [11 11 12 11 8])))
  (first-bit# timing))

(defn final-chord# [timing]
  (-> timing
    (after -1)
    (tempo 2)
    (even-melody# (map ionian [11 13 14])))
  (at (timing 0)
      (chord# (update-in I [:i] raise))))

(defn play# [] (->
                 (bpm 160 (now))
                 (after 2)
                 intro# intro# 
                 first-bit#
                 (tempo 3/2)
                 first-bit# variation#
                 final-chord#)) 

(play#)
