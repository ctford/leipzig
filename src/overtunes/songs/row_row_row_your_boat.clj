(ns overtunes.songs.row-row-row-your-boat
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(def major-scale #(apply + (take % (cycle [2 2 1 2 2 2 1]))))
(def g-major #(+ 67 (major-scale %))) 

(defn ms-per-beat [ms start] #(+ start (* % ms)))
(defn after [timing beats] #(timing (+ beats %)))

(def row-row-row-your-boat [0 0 0 1 2])
(def gently-down-the-stream [2 1 2 3 4])
(def merrily-merrily-merrily-merrily [7 7 7 4 4 4 2 2 2 0 0 0])
(def life-is-but-a-dream [4 3 2 1 0])

(def melody
  (concat
    row-row-row-your-boat
    gently-down-the-stream
    merrily-merrily-merrily-merrily
    life-is-but-a-dream))

(defn melody# [timing notes] 
  (let [note# #(at (timing %1) (sampled-piano (g-major %2)))]
    (dorun (map-indexed note# notes)))) 

(defn play# []
  (let [timing (ms-per-beat 700 (now))]
    (melody# timing melody)))

(play#)
