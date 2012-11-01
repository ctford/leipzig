(ns leipzig.example.row-row-row-your-boat
  (:use
    leipzig.melody
    leipzig.scale
    leipzig.canon
    overtone.inst.sampled-piano))

(defmethod play-note :default
  [{midi :pitch}] (sampled-piano midi))

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
               [  4   3   2   1   0]))))

(defn row-row [speed key]
  (->> melody
    (times 2)
    (canon (simple 4))
    (where :time speed)
    (where :pitch key)
    play))

;(row-row (bpm 120) (comp C flat major))
;(row-row (bpm 90) (comp B flat minor))
