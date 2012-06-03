;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canon Fodder - Chris Ford (ThoughtWorks)     ;;
;;                                              ;;
;; http://github.com/ctford/goldberg            ;;
;; http://github.com/overtone/overtone          ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; WARNING - This will cause the 200MB sampled  ;;
;; piano to be downloaded and cached locally.   ;;
;;                                              ;;
;; If you don't want to download this, try the  ;;
;; branch called "synth".                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns goldberg.variations.canone-alla-quarta
  (:use
    [overtone.live :only [at now ctl stop]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano#}]))

(defn play# [notes] 
  (let [play-at# (fn [[ms midi]]
                   (at ms (piano# midi))
                   (at (+ ms 150) (ctl piano# :gate 0)))]
    (->> notes (map play-at#) dorun)))

(defn even-melody# [pitches]
  (let [times (reductions + (cons (now) (repeat 400)))
        notes (map vector times pitches)]
    (play# notes)))

;(piano# 55)
;(even-melody# (range 60 67))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Scale                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn sum-n [series n] (reduce + (take n series)))

(defn negatable-scale [intervals]
  #(if-not (neg? %)
     (sum-n (cycle intervals) %)
     ((comp - (scale (reverse intervals)) -) %)))

(defn scale [intervals]
  #(if-not (integer? %)
    (if (neg? %)
       (dec ((negatable-scale intervals) (int %)))
       (inc ((negatable-scale intervals) (int %))))
    ((negatable-scale intervals) %)))

(def major (scale [2 2 1 2 2 2 1]))
(def blues (scale [3 2 1 1 3 2]))
(def pentatonic (scale [3 2 2 3 2]))
(def diatonic (scale [1]))

(defmacro defs [names values]
  `(do ~@(map
           (fn [name value] `(def ~name ~value))
           names (eval values))))

(defn start-from [base] (partial + base))
(defs [sharp flat] [inc dec])
(defs [C D E F G A B]
  (map
    (comp start-from (start-from 60) major)
    (range)))

;(even-melody# (map (comp A blues) (range 13)))
;(even-melody# (map (comp E flat pentatonic) (range 11)))
;(even-melody# (map (comp G major) (range 15)))
;(G 2)
;(major 2)
;((comp G major) 3/2) 
;((comp G major) 1) 
;((comp G sharp dorian) 2) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Modes                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn mode [n]
  (comp
    #(- % (major n))
    major 
    (start-from n)))

(defs
  [ionian dorian phrygian lydian mixolydian aeolian locrian]
  (map mode (range)))

(def minor aeolian)

;(even-melody#
;  (let [_ -100]
;    (map (comp D major) [0 1 2 0, 0 1 2 0, 2 3 4 _, 2 3 4 _]))
;)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Abstractions                                 ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))
;((bpm 120) 3)

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

(defn accumulate [series] (map (partial sum-n series) (range (count series))))
(def repeats (partial mapcat #(apply repeat %)))
(def runs (partial mapcat run))

(defn accidentals [f [k & ks] integers]
  (if k
    (sharpen ks (update-in (vec integers) [k] f))
    integers))
(def sharps (partial accidentals #(+ % 1/2)))
(def flats (partial accidentals #(- % 1/2)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Melody                                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def melody 
  (let [call
          [(repeats [[2 1/4] [1 1/2] [14 1/4] [1 3/2]])
          (sharps [12] (runs [[0 -1 3 0] [4] [1 8]]))]
        response
          [(repeats [[10 1/4] [1 1/2] [2 1/4] [1 9/4]])
          (sharps [4] (runs [[7 -1 0] [0 -3]]))]
        development
          [(repeats [[1 1] [11 1/4] [1 1/2] [1 1] [1 3/4] [11 1/4] [1 13/4]])
          (flats [4] (runs [[4] [2 -3] [-1 -2] [0] [3 5] [1] [1 2] [-1 1 -1] [5 0]]))]
        reprise 
          [(repeats [[15 1/4] [1 10/4] [1 3/4] [7 1/4] [1 1/2] [2 1/4] [1 5/4] [11 1/4] [1 6/4]])
          (sharps [4 16] (runs [[-1 4] [6 -3] [3 1 7] [0 -1 0] [2 -2 0 -1] [1 -2]]))]
        finale 
          [(repeats [[5 1/2] [1 6/4] [1 1/2] [2 1/4] [1 1] [3 1/4] [1 1/2] [1 1/4] [1 1]])
          (sharps [1 14] (runs [[4 1] [6] [0 -2] [1 -2 -1] [4 3 4]]))]
        [durations pitches] (map concat call response development reprise finale)
        timings (map (partial + 1/2) (accumulate durations))]
    (map vector timings pitches)))

(def bass
  (let [triples (partial mapcat #(repeat 3 %))
        crotchets-a
          [(repeats [[8 1] [1 10/4]])
          (sharps [8] (triples (runs [[-7 -9]])))]
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
        buildup 
          [(repeats [[7 1/4] [1 1/2] [1 3/4] [23 1/4] [2 1/2] [1 3/4]])
          (runs [[-10 -6 -8 -7] [-14] [-9 -6] [-8 -10] [-5] [-12] [-9 -11] [-13] [-10] [-7 -6] [-9] [-11] [-13] [-10 -9 -11 -10] [-13] [-17]])]
        end
          [[ 1/4 0  0  1/4 1/4  0   0  1/4 1/4 0  0  1]
           [-1   0 -3 -7  -2   -6  -3 -1   1   0 -3 -7]]
        [durations pitches] (map concat crotchets-a twiddle crotchets-b
                                        elaboration busy buildup end)]
    (map vector (accumulate durations) pitches)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Accidentals                                  ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def accidentals {[15/4 3] sharp, [30/4 3] sharp, [32/4 -9] sharp, [56/4 -1] flat})

(defn refine [scale targets [timing pitch :as note]]
  (if-let [refinement (targets note)] 
    [timing (-> pitch scale refinement)]
    [timing (-> pitch scale)]))

(defn with-accidentals [scale accidentals] (partial map (partial refine scale accidentals)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canone alla quarta - Johann Sebastian Bach   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn canon [f] (fn [notes] (concat notes (f notes))))

(defs [timing pitch] [0 1])
(defn skew [k f] (fn [points] (map #(update-in % [k] f) points))) 
(defn shift [point] (fn [points] (map #(->> % (map + point) vec) points)))

(defn simple [wait] (shift [wait 0]))
(defn interval [interval] (shift [0 interval]))
(def mirror (skew pitch -))
(def crab (skew timing -))
(def table (comp mirror crab))

(defn truncate [n] (partial drop-last n))

(def canone-alla-quarta (canon (comp (interval -3) mirror (truncate 6) (simple 3))))

(defn canon# [start tempo scale]
  (let [in-time (comp (shift [start 0]) (skew timing tempo))
        ;in-key (with-accidentals scale accidentals)
        in-key (skew pitch scale)
        play-now# (comp play# in-time in-key)]

   (-> bass play-now#)
   (-> melody canone-alla-quarta play-now#)))

;(canon# (now) (bpm 90) (comp G major))
;(canon# (now) (bpm 80) (comp G minor))
