(ns whelmed.songs.SKA
  (:use
        [whelmed.melody]
        [whelmed.scale]
        [whelmed.instrument]
        [overtone.live :only [ctl at midi->hz now stop]]))

(def wi accompany)
(def then follow)
(def where skew)
(def high raise)

(defn demo
  ([notes] (demo notes major))
  ([notes scale]
    (->> notes
      (where :time (bpm 90))
      (where :duration (bpm 90))
      (where :pitch (comp C scale))
      play)))

(def bass
  (->>
      (phrase
        [3/2 1 1/2 1]
        [0   0   2 4])
    (then
      (phrase
        [3/2 1 1/2 1]
        [5   5   4 2]))
    (where :part (constantly ::bass))
    (where :pitch (comp low low))))

(def fallbass
  (->>
      (take 4 bass)
    (then
      (phrase [4] [(low -3.5)]))))

(def wish-you-were-here-again 
  (->>
      (phrase
        [2/3 1/3 3/3 3/3 2/3 13/3]
        [0 1 0 4 0 0])
    (then
      (phrase
        [2/3 1/3 3/3 3/3 3/3 2/3 1/3 2/3 3/3 4/3]
        [0 1 0 4 0 2 3 2 1 0]))
    (where :pitch high)
    (where :part (constantly ::melody))))

(defn cluster [duration pitches]
  (map
    #(zipmap
       [:time :duration :pitch]
       [0 duration %])
    pitches))

(defn chord [degree duration]
  (->> (triad degree) vals (cluster duration)))

(def rhythm
  (->>
    (->> (chord 0 1)
      (after 1)
      (times 2))
    (wi (->> (chord -2 1)
      (after 1)
      (times 2)
      (after 4)))
    (where :part (constantly ::rhythm))))

(def fallchords
  (->> (take 6 rhythm)
    (then
      (->>
        (-> (triad 3.5) (update-in [:iii] #(+ % 0.5)) vals)
        (cluster 2)
        (after 2)))))

(def falla
  (phrase
    [1/3 1/3 1/3 2/3 1/3]
    [0.5 3.5 6 5 3.5]))

(def fallb
  (phrase
    [1/3 1/3 1/3 1/3 1/3 1/3]
    [6 5 3.5 6 5 3.5]))

(def fallback
  (->> fallbass
    (wi fallchords)
    (wi (after 6 falla))
    (then
      (->> fallbass
        (wi fallchords)
        (wi (after 6 fallb))))
    (then
      (->> fallbass
        (wi fallchords)
        (wi (after 6 falla))))
    (then (take 5 fallbass))
    (then (after -4 (phrase (repeat 6 2/3) [3.5 3 2.5 2 1 0.5])))
    (where :pitch (comp E minor))))

(def suns-on-the-rise 
  (->>
    (->> [(triad 1)] (skew :i #(+ % 1/2)) (skew :v #(+ % 1/2)) (mapcat vals) (cluster 4))
    (then (chord -2 4))
    (then (chord 0 4))
    (where :part (constantly ::rhythm))))

(def oooh
  (->>
    (phrase
      [3 1/3 2/3 3 2/3 1/3 3]
      [3 4 3 2 0 -1 0]) 
    (skew :pitch raise)
    (where :part (constantly ::melody))))

(def and-if-you-lived-here
  (->>
    (chord 0 4)
    (then (chord -3 4))
    (then
      (cluster 4
        (-> (triad 1) (update-in [:iii] #(+ % 1/2)) vals)))
    (then
      (cluster 4
        (-> (triad -2) (update-in [:iii] #(+ % 1/2)) vals)))))

(def youd-be-home-by-now
  (->>
    (phrase
      [1/3 2/3 1 1 1 1 1 10/3 2/3 4]
      [-3 2 2 1 -1 -2 -1 -2 -3 -2])
    (after 5/3)))

(def youd-be-home-right-now
  (->>
    youd-be-home-by-now
    (drop-last 2)
    (then
      (phrase [2/3 4] [1 0.5]))))

(def right-now
  (wi and-if-you-lived-here youd-be-home-by-now)) 

(def mid-section
  (->> and-if-you-lived-here 
    (wi youd-be-home-by-now) 
    (then (->>
      and-if-you-lived-here
      (wi youd-be-home-right-now)))
    (times 2)))

(def first-section
  (->> 
    (->> bass (accompany rhythm) (times 2)
         (accompany wish-you-were-here-again)
         (times 2))
    (then (accompany oooh suns-on-the-rise))
    (then (->> bass (times 2) (after -4)))
    (where :pitch (comp E minor))))

(def intro (->> bass (times 2) (skew :pitch (comp E minor))))

(defn in-time [signature notes]
  (->> notes
    (where :time signature)
    (where :duration signature)))

(def ska
  (->>
    intro
    (then first-section)
    (then intro)
    (then first-section)
    (then (where :pitch (comp low B flat major)  mid-section))
    (then fallback)
    (then (->> first-section (in-time #(* % 4/3))))
    (in-time (bpm 180))))

;(play ska)
