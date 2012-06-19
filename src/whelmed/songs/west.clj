(ns whelmed.songs.west
  (:use
    [whelmed.scale]
    [whelmed.melody]
    [whelmed.instrument]
    [overtone.live :only [midi->hz now stop]]))

(defn => [value & fs] (reduce #(%2 %1) value fs))
(defn lower [f] (comp #(- % 7) f))
(def progression (map #(map % seventh) [i (lower v) (lower vi) (lower iii)]))

(def backing
    (map
      #(map (partial vector %1) %2 (repeat 4))
      [0 4 8 12]
      progression))

(def ill-run-away [[-1/2 3 1/2] [0 4 1/4] [1/4 3 1/4] [1/2 4 1/2]])
(def ill-get-away (assoc ill-run-away 2 [1/4 6 1/4]))
(def my-heart-will-go-west-with-the-sun
  [[-1/2 3 1/2]
   [0 4 3/4] [3/4 3 3/4] [3/2 2 1/4]
   [8/4 4 3/4] [11/4 3 1/4] [14/4 2 1/4]
   [15/4 -1 17/4]
   ])

(def a-parting-kiss [[0 4 1/4] [1/4 3 1/4] [1/2 4 1/4] [3/4 6 1/4] [6/4 4 10/4]])
(def like-fairy-floss (cons [-1/4 3 1/4] a-parting-kiss))
(def dissolves-on-the-tip-of-my-tongue [[-1/4 4 1/4] [0 6 1/2] [3/4 4 13/4]])
(def reply (reduce follow
   [a-parting-kiss,
    like-fairy-floss,
    dissolves-on-the-tip-of-my-tongue,
    dissolves-on-the-tip-of-my-tongue]))


(def consider-this [[-3/2 4 1/2] [-1 9 1/2] [-1/2 8 1/2] [0 7 4]])
(def consider-that (assoc consider-this 3 [0 6 4])) 
(def consider-everything (concat (take 3 consider-this) [[0 7 1/2] [4/4 8 1/4] [6/4 7 1/2] [10/4 6 1/4] [14/4 4 9/2]]))
(def breakdown (reduce follow
   [consider-this, consider-that, consider-everything]))
(def breakup (=> breakdown (shift [0 -7 0])))
(def break (concat breakup breakdown))

(defn west#
  [tempo scale parts]
  (let [start (+ (now) 500)
        in-time (comp (skew timing tempo) (skew duration tempo))
        in-key (skew pitch scale)
        midify (fn [instrument#] (comp instrument# midi->hz))
        play-now# #(=> %2 in-time (after start) in-key (partial play-on# (midify %1)))]
    (dorun (map (partial apply play-now#) parts))))

(def theme (-> ill-run-away
             (follow 3 ill-get-away)
             (follow 3 my-heart-will-go-west-with-the-sun)))

(def melody (follow (times theme 2) (times reply 2)))
(def accompaniment (follow (times (apply concat backing) 6) 16 (apply concat backing)))
(def bass
  (let [vanilla (times (map first backing) 8)
        low (=> vanilla (shift [0 -7 0]))
        cut (fn [start end] #(concat (take start %) (drop end %)))
        seventh (=> vanilla (shift [1 -1 0]) (cut 20 28))]
  (concat low seventh)))

(comment
  (west# (bpm 80) (comp E aeolian)
      {sawish# (=> (times theme 2) (after 32))
       sinish# (concat ((after 64) (times reply 2)) ((after 96) (times break 2)))
       groan# (=> bass (after 16))
       shudder# accompaniment})
)
