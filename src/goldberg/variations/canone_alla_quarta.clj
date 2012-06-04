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
    [overtone.live :exclude [scale bpm run pitch shift sharp flat]]))

(defn even-melody# [pitches]
  (let [times (reductions + (cons (now) (repeat 400)))
        notes (map vector times pitches (repeat 150))]
    (play-on# (comp harps# midi->hz) notes)))

;(harps# (midi->hz 55))
;(even-melody# (range 60 73))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Abstractions                                 ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run [[from & tos]]
  (if-let [to (first tos)]
    (let [up-or-down (if (<= from to)
                       (range from to)
                       (reverse (range (inc to) (inc from))))]
      (concat up-or-down (run tos)))
    [from]))

;(even-melody# (map (comp G major)
;            (run [0 4 -1 0 1 0])
;            ))

(defn accumulate [series] (reductions + (cons 0 series))) 
(def repeats (partial mapcat #(apply repeat %)))
(def runs (partial mapcat run))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Melody                                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def melody 
  (let [theme
          [(repeats [[2 1/4] [1 1/2] [14 1/4] [1 3/2]])
          (runs [[0 -1 3 0] [4] [1 8]])]
        response
          [(repeats [[10 1/4] [1 1/2] [2 1/4] [1 9/4]])
          (runs [[7 -1 0] [0 -3]])]
        development
          [(repeats [[1 1] [11 1/4] [1 1/2] [1 1] [1 3/4] [11 1/4] [1 13/4]])
          (runs [[4] [2 -3] [-1 -2] [0] [3 5] [1] [1 2] [-1 1 -1] [5 0]])]
        interlude 
          [(repeats [[15 1/4] [1 10/4]])
          (runs [[-1 4] [6 -3]])]
        finale 
          [(repeats [[1 3/4] [7 1/4] [1 1/2] [2 1/4] [1 5/4] [11 1/4] [1 6/4] [5 1/2]
                     [1 6/4] [1 1/2] [2 1/4] [1 1] [3 1/4] [1 1/2] [1 1/4] [1 1]])
          (runs [[3 1 7] [0 -1 0] [2 -2 0 -1] [1 -2] [4 1] [6] [0 -2] [1 -2 -1] [4 3 4]])]
        [durations pitches] (map concat theme response development interlude finale)
        timings (map (partial + 1/2) (accumulate durations))]
    (map vector timings pitches durations)))

(def bass
  (let [triples (partial mapcat #(repeat 3 %))
        crotchets-a
          [(repeats [[8 1] [1 10/4]])
          (triples (runs [[-7 -9]]))]
        twiddle 
          [(repeats [[2 1/4] [2 1/2]])
          (runs [[-11 -13] [-11]])]
        crotchets-b
          [(repeats [[9 1]])
          (triples (runs [[-12 -10]]))]
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
    (map vector (accumulate durations) pitches durations)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Accidentals                                  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def accidentals 
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
(def canone-alla-quarta (canon (comp (interval -3) mirror (truncate 6) (simple 3))))

(defn canon# [start tempo scale instrument#]
  (let [in-time (comp (shift [start 0 0]) (skew timing tempo) (skew duration tempo))
        in-key (with-accidentals scale accidentals)
        play-now# (comp (partial play-on# instrument#) in-time in-key)]

   (-> bass play-now#)
   (-> melody canone-alla-quarta play-now#)))

;(canon# (now) (bpm 100) (comp B major) (comp harps# midi->hz))
;(canon# (now) (bpm 80) (comp E flat major) (comp sawish# midi->hz))
;(canon# (now) (bpm 90) (comp G major) piano#)
