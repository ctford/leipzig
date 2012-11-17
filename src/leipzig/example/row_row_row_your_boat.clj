(ns leipzig.example.row-row-row-your-boat
  (:use
    leipzig.melody
    leipzig.scale
    leipzig.canon)
  (:require [overtone.live :as o]))

(definst beep [frequency 440 duration 1]                       
  (let [envelope (o/line 1 0 duration :action o/FREE)]
    (* envelope (o/sin-osc frequency))))

(defmethod play-note :leader
  [{midi :pitch}] (-> midi o/midi->hz beep))
(defmethod play-note :follower
  [{midi :pitch}] (-> midi (+ 12) o/midi->hz beep))
(defmethod play-note :bass
  [{midi :pitch}] (-> midi (- 12) o/midi->hz beep))

(def melody
  (->> (phrase [3/3 3/3 2/3 1/3 3/3]
               [  0   0   0   1   2])
    (then
       (phrase [2/3 1/3 2/3 1/3 6/3]
               [  2   1   2   3   4]))
    (then
       (phrase (repeat 12 1/3) 
               (mapcat (partial repeat 3) [7 4 2 0])))
    (then
       (phrase [2/3 1/3 2/3 1/3 6/3] 
               [  4   3   2   1   0]))
    (where :part (is :leader))))

(def bass
  (->> (phrase [1  1 2]
               [0 -3 0])
     (where :part (is :bass))
     (times 4)))

(defn row-row [speed key]
  (->> melody
    (with bass)
    (times 2)
    (canon (comp (simple 4)
                 (partial where :part (is :follower))))
    (where :time speed)
    (where :pitch key)
    play))

;(row-row (bpm 120) (comp C flat major))
;(row-row (bpm 90) (comp B flat minor))
