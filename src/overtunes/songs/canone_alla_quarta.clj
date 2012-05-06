(ns overtunes.songs.canone-alla-quarta
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano#}]))

(defn => [val & fs] (reduce #(apply %2 [%1]) val fs))
(defn sum-n [series n] (reduce + (take n series)))
(defn sums [series] (cons 0 (reductions + series)))
(defn scale [intervals]
  #(if (neg? %)
     (let [downward-scale (comp - (scale (reverse intervals)))]
       (-> % - downward-scale))
     (sum-n (cycle intervals) %)))

(defn translate [point deltas] (map + point deltas))
(defn map-in [m k f] (map #(update-in % [k] f) m)) 
(defn transform [key f] #(update-in % [key] f))
(defn transform-y [f] #(map-in % 1 f))
(defn transform-x [f] #(map-in % 0 f))

(def major (scale [2 2 1 2 2 2 1]))
(defn add [x] (partial + x))
(def g-major (comp (add 74) major)) 


(defn bpm [per-minute] #(-> % (/ per-minute) (* 60) (* 1000)))
(defn run [a & bs] 
  (let [up-or-down #(if (<= %1 %2) (range %1 %2) (reverse (range (inc %2) (inc %1))))]
    (if bs
      (concat (up-or-down a (first bs)) (apply run bs))
      [a])))

(def melody 
  (let [call
          {:time (mapcat repeat [2 1 14 1] [1/4 1/2 1/4 3/2])
           :pitch (concat (run 0 -1 3 0) [4] (run 1 8))}
        response
          {:time (mapcat repeat [10 1 2 1]  [1/4 1/2 1/4 9/4])
           :pitch (concat (run 7 -1 0) (run 0 -3))}
        development
          {:time (mapcat repeat [1 12 1 1 1 12 1] [3/4 1/4 1/2 1 1/2 1/4 3])
           :pitch (concat [4 4] (run 2 -3) [-1 -2 0] (run 3 5) (repeat 3 1) [2] (run -1 1 -1) (run 5 0))}
        line
          (merge-with concat call response development)]
    (=> line #(update-in % [:time] sums) #(map vector (:time %) (:pitch %)))))

(defn play# [notes] 
  (let [play-at# #(at (% 0) (piano# (% 1)))]
    (->> notes (map play-at#) dorun)))

(defn canone-alla-quarta# []
  (let [in #(transform-y %)
        after #(transform-x (add %))
        from-now (after (now))
        tempo #(transform-x %) 
        mirror (transform-y -)
        transpose #(transform-y (add %)) 
        leader #(=> % (after 1/2) (in g-major) (tempo (bpm 120)) from-now)
        follower #(=> % mirror (transpose -3) (after 3) leader)]
    (=> melody leader play#)
    (=> melody follower play#)
    ))

(canone-alla-quarta#)
