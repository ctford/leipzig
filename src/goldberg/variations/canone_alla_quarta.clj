;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canon Fodder - Chris Ford (ThoughtWorks)     ;;
;;                                              ;;
;; http://github.com/ctford/goldberg            ;;
;; http://github.com/overtone/overtone          ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns goldberg.variations.canone-alla-quarta
  (:use
    [goldberg.scale]
    [goldberg.canon]
    [goldberg.melody]
    [goldberg.instrument]
    [overtone.live :only [midi->hz now stop]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Abstractions                                 ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn => [value & fs] (reduce #(%2 %1) value fs))

(defn run [[from & tos]]
  (if-let [to (first tos)]
    (let [up-or-down (if (<= from to)
                       (range from to)
                       (reverse (range (inc to) (inc from))))]
      (concat up-or-down (run tos)))
    [from]))

;(even-melody# (range 60 73))
;(even-melody# (map (comp G major)
;            (run [0 4 -1 0 1 0])
;            ))

(defn accumulate [series] (reductions + 0 series)) 
(def repeats (partial mapcat #(apply repeat %)))
(def runs (partial mapcat run))

(defn follow [first second]
  (let [[timing _ duration] (last first)
        shifted ((shift [(+ duration timing) 0 0]) second)]
    (concat first shifted))) 

(defn insert [value n values] (concat (take n values) [value] (drop n values)))
(defn subtract [n values] (concat (take n values) (drop (inc n) values)))
(defn override [value n values] (concat (take n values) [value] (drop (inc n) values)))

(defn minus [n] (partial subtract n))
(defn plus [value n] (partial insert value n))
(defn push [value n] (partial override value n))

(def triples (partial mapcat #(repeat 3 %)))
(defn rollup [pitches durations]
  (map vector (accumulate durations) pitches durations))

;(defn even-melody [duration] #(map vector (repeat duration) %))
;(def theme (=> (run [0 -1 3 0 8]) (even-melody 1/4)
;               (push [1/2 0] 2) (plus [1/4 4] 9) (push [3/2 8] 17)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Melody                                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def melody1 
  (let [theme
          [(=> (repeat 17 1/4) (push 1/2 2) (plus 3/2 17))
           (=> (run [0 -1 3 0 8]) (plus 4 9))]
        response
          [(=> (repeat 13 1/4) (plus 9/4 13) (push 1/2 10))
           (=> (run [7 -1 0 -3]) (plus 0 9))]
        dip
          [(concat [1] (repeat 11 1/4))
          (=> (runs [[4 -3] [-1 -2] [0] [3 5]]) (minus 1))]
        development
          [(concat [1/2 1 3/4] (repeat 11 1/4) [13/4])
          (=> (runs [[1] [1 2] [-1 1 -1] [5 0]]))]
        interlude 
          [(=> (repeat 15 1/4) (plus 10/4 15))
          (=> (run [-1 6 -3]) (minus 6))]
        buildup 
          [(repeats [[1 3/4] [7 1/4] [1 1/2] [2 1/4] [1 5/4] [11 1/4] [1 6/4] [5 1/2]])
          (runs [[3 1 7] [0 -1 0] [2 -2 0 -1] [1 -2] [4 1]])]
        finale 
          [(repeats [[1 6/4] [1 1/2] [2 1/4] [1 1] [3 1/4] [1 1/2] [1 1/4] [1 1]])
          (runs [[6] [0 -2] [1 -2 -1] [4 3 4]])]
        [durations pitches] (map concat theme response dip development interlude buildup finale)
        timings (map (partial + 1/2) (accumulate durations))]
    (map vector timings pitches durations)))

(def melody2
  (let [theme
          [(=> (repeats [[2 1/4] [1 1/2] [6 1/4] [1 5/4] [5 1/4] [1 1/2] [1 3/4] [3 1/4] [1 1/2] [1 1]]))
           (=> (runs [[-3 -2 -6 -3] [-10 -9] [-11 -9 -10] [-8 -9 -8 -10 -9] [-4]]))]
        response
          [(repeats [[1 1/2] [12 1/4]])
           (runs [[-9 -10 -9 -11 -2]])]
        complicated 
          [(repeats [[1 7/2] [2 1/4] [3 1/2] [2 1/4] [1 2]])
           (runs [[-2 -3 -2] [-2] [-2 -4 -3]])]
        then 
          [(repeats [[1 1] [11 1/4] [1 13/4]])
           (runs [[-1 -3] [-1 -4] [-4 -8 -7]])]
        blah 
          [(repeats [[11 1/4] [1 7/2] [3 1/2] [4 1/4] [4 1/2] [1 3/4] [1 1/4]])
           (runs [[1 -2 -1 -4 -3 -6 -5] [0 -3] [-5 -4] [-6 -4 -8]])]
        finale 
          [(repeats [[1 5/4] [11 1/4] [1 1/2] [1 3/4] [1 1/4] [1 1/2] [1 1]])
           (runs [[-7] [-5 -12] [-10] [-7] [-5] [-3] [-7 -6] [-8 -7]])]
        [durations pitches] (map concat theme response complicated then blah finale)
        timings (map (partial + 1/2) (accumulate durations))]
    (map vector timings pitches durations)))

(def bass1
  (let [crotchets-a
          [(repeat 9 1)
          (=> (run [-7 -9]) triples)]
        twiddle 
          [(repeats [[1 1/4] [1 5/4] [2 1/4] [2 1/2]])
          (runs [[-10] [-17] [-11 -13] [-11]])]
        crotchets-b
          [(repeat 9 1)
          (=> (run [-12 -10]) triples)]
        elaboration
          [(repeats [[1 3/4] [9 1/4] [1 1/2] [1 1] [2 1/4] [3 1/2] [1 1]])
          (runs [[-7] [-12] [-9 -11] [-9 -13 -12] [-14] [-7 -8 -7] [-9 -8] [-5]])]
        busy 
          [(repeats [[2 1/4] [2 1/2] [4 1/4] [4 1/2] [4 1/4] [3 1/2] [1 7/4]])
          (runs [[-12 -10] [-12] [-9 -7 -9 -8 -11 -9 -11] [-9] [-11] [-13]])]
        finale 
          [(repeats [[7 1/4] [1 1/2] [1 3/4] [23 1/4] [2 1/2] [1 3/4]])
          (runs [[-10 -6 -8 -7] [-14] [-9 -6] [-8 -10] [-5] [-12] [-9 -11] [-13]
                 [-10] [-7 -6] [-9] [-11] [-13] [-10 -9 -11 -10] [-13] [-17]])]
        [durations pitches] (map concat crotchets-a twiddle crotchets-b elaboration busy finale)]
    (rollup pitches durations)))

(def bass2
  (let [intro
          [(repeats [[3 1] [5 1/2] [2 1/4] [1 1/2] [1 1] [4 1/2] [1 7/2]])
          (runs [[-10] [-10 -12 -11 -14 -11 -12 -11] [-9] [-13] [-11 -12]])]
        development 
          [(repeats [[5 1/2] [24 1/4]])
          (runs [[-9 -3 -5 -4 -7 -6] [-8 -4 -6 -5] [-8] [-10] [-8] [-12] [-10 -12]])]
        up-n-down 
          [(repeats [[8 1/4] [3 1/2] [1 3/4] [7 1/4] [1 1/2] [1 3/4] [7 1/4] [1 1/2] [1 3/4]])
          (runs [[-9] [-11 -14] [-12] [-9 -10 -9 -11] [-4] [-9 -11 -10 -13 -12] [-5] [-10 -12 -11 -14 -13] [-6]])]
        down 
          [(repeats [[27 1/4] [1 5/4] [3 1/4] [1 3/2]])
          (runs [[-5 -7 -6 -9 -8 -11 -10 -13 -12 -15 -14] [-6 -8 -7 -10 -9 -11] [-9 -10]])]
        finale 
          [(repeats [[1 2] [3 1/2] [1 5/4] [3 1/4] [1 1]])
          (runs [[-13] [-9] [-11 -10] [-14] [-12] [-10] [-8 -7]])]
        [durations pitches] (map concat intro development up-n-down down finale)]
    (rollup pitches durations)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Accidentals                                  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def accidentals1 
  (let [leader
         {[(+ 3 3/4) 3] sharp, [(+ 7 1/2) 3] sharp, [14 -1] flat, [(+ 25 1/4) 3] sharp,
          [(+ 30 1/2) 3] sharp, [40 3] sharp, [(+ 46 3/4) 3] sharp}
        follower
         {[(+ 27 3/4) -4] sharp, [30 -4] sharp, [(+ 34 1/2) -4] sharp, [(+ 38 1/2) -4] sharp,
          [(+ 40 1/4) -4] sharp, [44 -4] sharp, [(+ 47 1/4) -4] sharp}
        bass
         {[8 -9] sharp, [(+ 28 3/4) -11] sharp, [33 -11] sharp, [43 -11] sharp,
          [(+ 45 3/4) -11] sharp}]
    (merge bass leader follower)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canone alla quarta - Johann Sebastian Bach   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn truncate [n] (partial drop-last n))
;(def canone-alla-quarta (canon (comp (interval -3) mirror (truncate 6) (simple 3))))
(def canone-alla-quarta (canon (comp (interval -3) mirror (truncate 4) (simple 3))))

(defn canon# [start tempo scale instrument#]
  (let [in-time (comp (shift [start 0 0]) (skew timing tempo) (skew duration tempo))
        ;in-key (with-accidentals scale accidentals1)
        in-key (skew pitch scale) 
        play-now# (comp (partial play-on# instrument#) in-time in-key)]

   (-> bass2 play-now#)
   (-> melody2 canone-alla-quarta play-now#)))
   ;(-> melody2 play-now#)))

;(canon# (now) (bpm 100) (comp B major) (comp harps# midi->hz))
;(canon# (now) (bpm 80) (comp E flat major) (comp sawish# midi->hz))
;(canon# (now) (bpm 90) (comp G major) piano#)
