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

(def scale 56)
(defn ground [note] (+ scale note))

(def note# (comp sampled-piano ground))
(defn chord# [chord] (doseq [note (vals chord)] (note# note))) 

(def ionian (cycle [0 2 4 5 7 9 11]))
(defn triad [scale root]
  (zipmap [:i :iii :v]
          [(nth scale root)
           (nth scale (+ root 2))
           (nth scale (+ root 4))])) 

(defn lower [note] (- note 12))
(defn with-base [chord]
  (assoc chord :base
         (lower (:i chord))))

(def I (with-base (triad ionian 0)))
(def II (with-base (triad ionian 1)))
(def V (with-base (triad ionian 4)))

(def progression [I I II II II V I (update-in V [:base] lower)])

(defn rythm-n-bass# [timing [chord1 chord2 & chords]]
  (do
    (at (timing 0) (note# (:base chord1)))
    (at (timing 2) (chord# (dissoc chord1 :base)))
    (at (timing 3) (note# (:base chord1)))
    (at (timing 4) (note# (:base chord2)))
    (at (timing 6) (chord# (dissoc chord2 :base)))
    (if chords
      (rythm-n-bass# (from timing 8) chords))))


(defn even-melody# [timing [note & notes]]
  (do
    (at (timing 0) (note# note))
    (if notes
      (even-melody# (from timing 1) notes))))

(defn play []
  (let [timing (from (bpm 130) 4)]
    (even-melody# timing (take 64 (cycle [9 7])))
    (rythm-n-bass# timing (take 16 (cycle progression)))))

; (play)
