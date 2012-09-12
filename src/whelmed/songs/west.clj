(ns whelmed.songs.west
  (:use
    [whelmed.melody]
    [whelmed.scale]
    [whelmed.instrument]
    [overtone.live :only [at ctl midi->hz now stop]]))

(def progression (map seventh [0 (low 4) (low 5) (low 2)]))

(def backing
  (let [render-chord (fn [start notes] (map #(identity {:time start :duration 4 :pitch %}) notes))]
    (->>
      progression
      (map render-chord [0 4 8 12]))))

(def ill-run-away
  (->>
   (phrase
     [1/2 1/4 1/4 1/2]
     [  3   4   3   4])
  (after -1/2)
  (with :part :lead)))


(def ill-get-away (assoc (vec ill-run-away) 2 {:time 1/4 :pitch 6 :duration 1/4}))

(def my-heart-will-go-west-with-the-sun
  (after -1/2
     (phrase
       [1/2 3/4 3/4 2/4 3/4 3/4 1/4 17/4]
       [  3   4   3   2   4   3   2   -1])))

(def west-with-the-west-with-the 
  (let [west-with-the (subvec (vec my-heart-will-go-west-with-the-sun) 1 4)
        wests (times 4 west-with-the)]
     (reduce follow
             [[{:time -1/2 :pitch 3 :duration 1/2}]
              wests
              [{:time 0 :pitch 7 :duration 1/4}]])))

(def a-parting-kiss
  (phrase
    [1/4 1/4 1/4 3/4 10/4]
    [  4   3   4   6    4]))

(def like-fairy-floss (cons {:time -1/4 :pitch 3 :duration 1/4} a-parting-kiss))

(def dissolves-on-the-tip-of-my-tongue
  (->>
    (phrase
      [1/4 3/4 13/4]
      [  4   6    4])
    (after -1/4)))

(def reply
 (->>
   a-parting-kiss
   (follow like-fairy-floss)
   (follow dissolves-on-the-tip-of-my-tongue) 
   (follow dissolves-on-the-tip-of-my-tongue))) 

(def consider-this
  (after -3/2
     (phrase
       [1/2 1/2 1/2 8/2]
       [  4   9   8   7])))

(def consider-that (assoc (vec consider-this) 3 {:time 0 :pitch 6 :duration 4})) 

(def consider-everything
  (->>
    (take 3 consider-this)
    (follow
      (phrase
        [2/2 1/2 2/2 2/2 9/2]
        [  7   8   7   6   4]))))

(def breakdown
 (->>
   consider-this
   (follow consider-that)
   (follow consider-everything)))

(def breakup (skew :pitch low breakdown))
(def break (accompany breakup breakdown))

(def theme
  (->>
    ill-run-away
    (follow (after 3 ill-get-away))
    (follow (after 3 my-heart-will-go-west-with-the-sun))))

(def half-theme
  (->>
    ill-run-away
    (follow (after 3 ill-get-away))))

(def spilling-theme
  (->>
    ill-run-away
    (follow (after 3 ill-get-away))
    (follow (after 3 west-with-the-west-with-the))))

(def accompaniment
  (->>
    (apply concat backing)
    (times 6)
    (follow (after 16 (times 6 (apply concat backing))))
    (with :part :accompaniment)))

(defn cut [start end notes] (->> notes (take end) (drop start)))
(defn except [start end notes] (concat
                                 (take start notes)
                                 (drop end notes)))

(def bass
  (let [vanilla
          (->> 
            (map first backing)
            (times 13))
        lowered (skew :pitch low vanilla)
        seventh (->> vanilla (skew :time inc) (skew :pitch dec) (except 20 28))]
  (->>
    lowered
   (accompany seventh)
   (with :part :bass)))) 

(def west-with-the-sun
  (apply concat
      [
       (->> theme (times 2) (after 32))
       (->> reply (times 2) (after 64))
       (->> break (times 2) (after 96))
       (->> theme (after 128))
       (->> spilling-theme (after 144))
       (->> reply (times 2) (after 160))
       (->> break (after 176))
       (->> half-theme (after 192.5))
       (->> half-theme (after 200.5))
       (->> bass (after 16))
       accompaniment
       ]))

(->>
  west-with-the-sun
  (skew :pitch (comp E minor))
  (skew :time (bpm 90))
  (skew :duration (bpm 90))
  play)

