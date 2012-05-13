;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canon Fodder - Chris Ford                    ;;
;;                                              ;;
;; http://github.com/ctford/goldberg            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns goldberg.variations.canone-alla-quarta
  (:use
    [overtone.live :only [at now stop]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano#}]))

(defn play# [notes] 
  (let [play-at# (fn [[ms midi]] (at ms (piano# midi)))]
    (->> notes (map play-at#) dorun)
    notes))

(defn even-melody# [pitches]
  (let [times (reductions + (cons (now) (repeat 300)))
        notes (map vector times pitches)]
    (play# notes)))

;(piano# 50)
;(even-melody# (range 60 73))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Scale                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn sum-n [series n] (reduce + (take n series)))
(defn => [value & fs] (reduce #(%2 %1) value fs))

(defn scale [intervals]
  #(if-not (neg? %)
     (sum-n (cycle intervals) %)
     (=> % - (scale (reverse intervals)) -)))

(def major (scale [2 2 1 2 2 2 1]))
(def minor (scale [2 1 2 2 1 2 2]))
(def g-major (comp (partial + 67) major)) 

;(major 2)
;(minor 2)
;(even-melody# (let [key (comp (partial + 67) major), rest -100]
;         (map key [0 1 2 0 0 1 2 0 2 3 4 rest 2 3 4 rest])))






;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Structure                                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run [[from to & tos]] 
    (if to
      (concat
        (if (<= from to)
          (range from to)
          (reverse (range (inc to) (inc from))))
        (run (cons to tos)))
      [from]))

;(even-melody# (map g-major
;            (run [0 7 0])
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
        [durations pitches] (map concat call response development)]
    (map vector (accumulate durations) pitches)))

(def bassline
  (let [triples (partial mapcat #(repeat 3 %))]
    (map vector
       (accumulate (repeats [[21 1] [12 1/4]]))
       (concat (triples (runs [[0 -3] [-5 -3]])) (run [12 0])))))

;melody


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canone alla quarta - Johann Sebastian Bach   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))
(defn skew [k f] (fn [points] (map #(update-in % [k] f) points))) 
(defn shift [point] (fn [points] (map #(->> % (map + point) vec) points)))

(defn canon# []
  (let [[timing pitch] [0 1]
        [tempo start] [(bpm 90) (now)]
        in-time #(=> % (skew timing tempo) (shift [start 0]))
        in-key (skew pitch g-major)
        play-now# #(=> % in-key in-time play#)]

    (=> bassline (shift [0 -7]) play-now#)
    (=> melody (shift [1/2 0]) play-now#)
    (=> melody (skew pitch -) (shift [7/2 -3]) play-now#)))

;((bpm 120) 3)
;(canon#)




;(stop)
