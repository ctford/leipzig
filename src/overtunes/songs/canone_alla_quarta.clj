(ns overtunes.songs.canone-alla-quarta
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano#}]))

(defn => [val & fs] (reduce #(apply %2 [%1]) val fs))
(defn sum-n [series n] (reduce + (take n series)))
(defn sums [series] (map (partial sum-n series) (range (count series))))
(defn scale [intervals]
  #(if (not (neg? %))
     (sum-n (cycle intervals) %)
     (=> % - (scale (reverse intervals)) -)))

(def t 0)
(def p 1)
(defn map-in [m k f] (map #(update-in % [k] f) m)) 
(defn transform [k f] #(map-in % k f))
(defn add [offset] (partial + offset))

(def major (scale [2 2 1 2 2 2 1]))
(def g-major (comp (add 74) major)) 

(defn bpm [per-minute] #(-> % (/ per-minute) (* 60) (* 1000)))
(defn run [[a & bs]] 
  (let [up-or-down #(if (<= %1 %2) (range %1 %2) (reverse (range (inc %2) (inc %1))))]
    (if bs
      (concat (up-or-down a (first bs)) (run bs))
      [a])))

(def melody 
  (let [repeats (partial mapcat (partial apply repeat))
        runs (partial mapcat run)
        call
          {:length (repeats [[2 1/4] [1 1/2] [14 1/4] [1 3/2]])
           :pitch (runs [[0 -1 3 0] [4] [1 8]])}
        response
          {:length (repeats [[10 1/4] [1 1/2] [2 1/4] [1 9/4]])
           :pitch (runs [[7 -1 0] [0 -3]])}
        development
          {:length (repeats [[1 3/4] [12 1/4] [1 1/2] [1 1] [1 1/2] [12 1/4] [1 3]])
           :pitch (runs [[4] [4] [2 -3] [-1 -2] [0] [3 5] [1] [1] [1 2] [-1 1 -1] [5 0]])}
        line
          (merge-with concat call response development)]
    (map vector (sums (:length line)) (:pitch line))))

(def bassline
  (let [triples (partial mapcat (partial repeat 3))
        repeats (partial mapcat (partial apply repeat))
        runs (partial mapcat run)]
    (map vector
       (sums (repeats [[21 1] [12 1/4]]))
       (concat (triples (runs [[0 -3] [-5 -3]])) (run [12 0])))))

(defn play# [notes] 
  (let [play-at# #(at (% 0) (piano# (% 1)))]
    (->> notes (map play-at#) dorun)))

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

(canone-alla-quarta#)
