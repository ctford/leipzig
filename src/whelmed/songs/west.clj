(ns whelmed.songs.west
  (:use
    [whelmed.melody]
    [whelmed.scale]
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

(def ill-run-away
  ((after -1/2)
     (phrase
       [1/2 1/4 1/4 1/2]
       [  3   4   3   4])))
(def ill-get-away (assoc (vec ill-run-away) 2 [1/4 6 1/4]))

(def my-heart-will-go-west-with-the-sun
  ((after -1/2)
     (phrase
       [1/2 3/4 3/4 2/4 3/4 3/4 1/4 17/4]
       [  3   4   3   2   4   3   2   -1])))

(def west-with-the-west-with-the 
  (let [west-with-the (subvec (vec my-heart-will-go-west-with-the-sun) 1 4)
        wests (times west-with-the 4)]
     (reduce follow [[[-1/2 3 1/2]] wests [[0 7 1/4]]])))

west-with-the-west-with-the



(def a-parting-kiss
  (phrase
    [1/4 1/4 1/4 3/4 10/4]
    [  4   3   4   6    4]))
(def like-fairy-floss (cons [-1/4 3 1/4] a-parting-kiss))

(def dissolves-on-the-tip-of-my-tongue
  (phrase
    [1/4 3/4 12/4]
    [  4   6    4]))

(def reply (reduce follow
   [a-parting-kiss,
    like-fairy-floss,
    dissolves-on-the-tip-of-my-tongue,
    dissolves-on-the-tip-of-my-tongue]))

(def consider-this
  ((after -3/2)
     (phrase
       [1/2 1/2 1/2 8/2]
       [  4   9   8   7])))
(def consider-that (assoc (vec consider-this) 3 [0 6 4])) 

(def consider-everything
  (follow
    (take 3 consider-this)
    (phrase
      [2/2 1/2 2/2 2/2 9/2]
      [  7   8   7   6   4])))

(def breakdown (reduce follow [consider-this, consider-that, consider-everything]))
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

(def half-theme (-> ill-run-away (follow 3 ill-get-away)))

(def spilling-theme (-> ill-run-away
             (follow 3 ill-get-away)
             (follow 3 west-with-the-west-with-the)))

(def accompaniment (follow (times (apply concat backing) 6) 16 (times (apply concat backing) 6)))
(def bass
  (let [vanilla (times (map first backing) 13)
        low (=> vanilla (shift [0 -7 0]))
        cut (fn [start end] #(concat (take start %) (drop end %)))
        seventh (=> vanilla (shift [1 -1 0]) (cut 20 28))]
  (concat low seventh)))

(defn west-with-the-sun# []
  (west# (bpm 80) (comp E aeolian)
      [[sawish# (=> (times theme 2) (after 32))]
       [sinish# ((after 64) (times reply 2))]
       [sinish# ((after 96) (times break 2))]
       [sawish# (=> (times theme 1) (after 128))]
       [sawish# (=> (times spilling-theme 1) (after 144))]
       [sinish# ((after 160) (times reply 2))]
       [sinish# ((after 176) (times break 1))]
       [sawish# (=> half-theme (after 192.5))]
       [sawish# (=> half-theme (after 200.5))]
       [groan# (=> bass (after 16))]
       [shudder# accompaniment]]))
