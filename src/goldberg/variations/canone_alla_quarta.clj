;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canon Fodder - Chris Ford (ThoughtWorks)     ;;
;;                                              ;;
;; http://github.com/ctford/goldberg            ;;
;; http://github.com/overtone/overtone          ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns goldberg.variations.canone-alla-quarta
  (:use [overtone.live :exclude [scale bpm run pitch shift]]))

(defn play-on# [instrument# notes] 
  (let [play-at# (fn [[ms midi]] (at ms (instrument# midi)))]
    (->> notes (map play-at#) dorun)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Synth                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(definst sawish# [freq 440 depth 10]
  (let [envelope (env-gen (perc 0.1 0.4) (lf-pulse:kr 2) :action FREE)]
    (*
      envelope
      (sin-osc freq)
      (saw (+ freq (* depth (lf-saw:kr (lf-pulse:kr 0.1 0.2))))))))

(definst harps1 [freq 440]
  (let [duration 1]
    (* (line:kr 1 1 duration FREE)
       (pluck (* (white-noise) (env-gen (perc 0.001 5) :action FREE)) 1 1 (/ 1 freq) (* duration 2) 0.25))))

(defn synth# [midi-note] (-> midi-note midi->hz harps1))
(def play# (partial play-on# synth#))

(defn even-melody# [pitches]
  (let [times (reductions + (cons (now) (repeat 400)))
        notes (map vector times pitches)]
    (play# notes)))

;(synth# 55)
;(even-melody# (range 60 73))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Scale                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn sum-n [series n] (reduce + (take n series)))

(defn scale [intervals]
  #(if-not (neg? %)
     (sum-n (cycle intervals) %)
     ((comp - (scale (reverse intervals)) -) %)))

(def major (scale [2 2 1 2 2 2 1]))
(def blues (scale [3 2 1 1 3 2]))

(defmacro defs [names values]
  `(do ~@(map
           (fn [name value] `(def ~name ~value))
           names (eval values))))

(defn start-from [base] (partial + base))
(defs [C D E F G A B] (map start-from (range 63 70)))

;(even-melody# (map (comp A blues) (range 13)))
;(even-melody# (map (comp G major) (range 15)))
;(G 2)
;(major 2)
;((comp G major) 2) 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Modes                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn modulate [scale mode]
  (comp
    #(- % (scale mode))
    scale
    (start-from mode)))

(defs
  [ionian dorian phrygian lydian mixolydian aeolian locrian]
  (map (partial modulate major) (range)))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Melody                                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def melody 
  (let [call
          [(repeats [[2 1/4] [1 1/2] [14 1/4] [1 3/2]])
          (runs [[0 -1 3 0] [4] [1 8]])]
        response
          [(repeats [[10 1/4] [1 1/2] [2 1/4] [1 9/4]])
          (runs [[7 -1 0] [0 -3]])]
        development
          [(repeats [[1 3/4] [12 1/4] [1 1/2] [1 1] [1 1/2] [12 1/4] [1 3]])
          (runs [[4] [4] [2 -3] [-1 -2] [0] [3 5] [1] [1] [1 2] [-1 1 -1] [5 0]])]
        [durations pitches] (map concat call response development)
        timings (map (partial + 1/2) (accumulate durations))]
    (map vector timings pitches)))

(def bass
  (let [triples (partial mapcat #(repeat 3 %))]
    (map vector
       (accumulate (repeats [[21 1] [13 1/4]]))
       (concat (triples (runs [[-7 -10] [-12 -10]])) (run [5 -7])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canone alla quarta - Johann Sebastian Bach   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn canon [f] (fn [notes] (concat notes (f notes))))

(def timing 0)
(def pitch 1)
(defn skew [k f] (fn [points] (map #(update-in % [k] f) points))) 
(defn shift [point] (fn [points] (map #(->> % (map + point) vec) points)))

(defn simple [wait] (shift [wait 0]))
(defn interval [interval] (shift [0 interval]))
(def mirror (skew pitch -))
(def crab (skew timing -))
(def table (comp mirror crab))

(def canone-alla-quarta (canon (comp (interval -3) mirror (simple 3))))

(defn canon# [start tempo scale]
  (let [in-time (comp (shift [start 0]) (skew timing tempo))
        in-key (skew pitch scale)
        play-now# (comp play# in-key in-time)]

    (-> bass play-now#)
    (-> melody canone-alla-quarta play-now#)))

;(canon# (now) (bpm 90) (comp G ionian))
;(canon# (now) (bpm 80) (comp B aeolian))
