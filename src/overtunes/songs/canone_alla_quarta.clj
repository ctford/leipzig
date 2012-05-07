;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canon Fodder - Chris Ford                    ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns overtunes.songs.canone-alla-quarta
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano#}]))

(defn => [val & fs] (reduce #(apply %2 [%1]) val fs))

(defn play# [notes] 
  (let [play-at# (fn [[ms midi]] (at ms (piano# midi)))]
    (->> notes (map play-at#) dorun)))

(defn demo# [pitches]
  (let [start (now)
        note-length 300
        end (+ start (* note-length (count pitches)))
        notes (map vector (range start end note-length) pitches)]
    (play# notes)))

;(demo# (range 60 73))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Scale                                        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn sum-n [series n] (reduce + (take n series)))
(defn scale [intervals]
  #(if (not (neg? %))
     (sum-n (cycle intervals) %)
     (=> % - (scale (reverse intervals)) -)))

(def major (scale [2 2 1 2 2 2 1]))
(def minor (scale [2 1 2 2 1 2 2]))
(def g-major (comp (partial + 74) major)) 

;(major 2)
;(minor 2)
;(demo# (let [key (comp (partial + 67) major), rest -100]
;         (map key [0 1 2 0 0 1 2 0 2 3 4 rest 2 3 4 rest])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Melody                                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn run [[a & bs]] 
  (let [up-or-down
          #(if (<= %1 %2)
            (range %1 %2)
            (reverse (range (inc %2) (inc %1))))]
    (if bs
      (concat
        (up-or-down a (first bs))
        (run bs))
      [a])))

(defn sums [series] (map (partial sum-n series) (range (count series))))
(def repeats (partial mapcat (partial apply repeat)))
(def runs (partial mapcat run))

;(demo# (map g-major
;            (run [0 3 1 3 -1 0])
;            ))

(def melody 
  (let [call (vector
          (repeats [[2 1/4] [1 1/2] [14 1/4] [1 3/2]])
          (runs [[0 -1 3 0] [4] [1 8]]))
        response (vector
          (repeats [[10 1/4] [1 1/2] [2 1/4] [1 9/4]])
          (runs [[7 -1 0] [0 -3]]))
        development (vector
          (repeats [[1 3/4] [12 1/4] [1 1/2] [1 1] [1 1/2] [12 1/4] [1 3]])
          (runs [[4] [4] [2 -3] [-1 -2] [0] [3 5] [1] [1] [1 2] [-1 1 -1] [5 0]]))
        line
          (map concat call response development)]
    (map vector (sums (nth line 0)) (nth line 1))))

;melody

(def bassline
  (let [triples (partial mapcat (partial repeat 3))]
    (map vector
       (sums (repeats [[21 1] [12 1/4]]))
       (concat (triples (runs [[0 -3] [-5 -3]])) (run [12 0])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Canone alla quarta - Johann Sebastian Bach   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn bpm [per-minute] #(-> % (/ per-minute) (* 60) (* 1000)))

(def t 0)
(def p 1)
(defn map-in [m k f] (map #(update-in % [k] f) m)) 
(defn transform [k f] #(map-in % k f))
(defn add [offset] (partial + offset))

(defn canone-alla-quarta# []
  (let [in #(transform p %)
        after #(transform t (add %))
        from-now (after (now))
        tempo #(transform t %) 
        mirror (transform p -)
        transpose #(transform p (add %)) 
        leader #(=> % (after 1/2) (in g-major) (tempo (bpm 120)) from-now)
        follower #(=> % mirror (transpose -3) (after 3) leader)
        bass #(=> % (transpose -7) (in g-major) (tempo (bpm 120)) from-now)]
    (=> bassline bass play#)
    (=> melody leader play#)
    (=> melody follower play#)))

;(canone-alla-quarta#)
