(ns whelmed.songs.SKA
  (:use
        [whelmed.melody]
        [whelmed.scale]
        [whelmed.instrument]
        [overtone.live :only [ctl at midi->hz now stop]]))

(def then follow)

(defn demo [notes]
  (->> notes
    (skew :time (bpm 90))
    (skew :duration (bpm 90))
    (skew :pitch (comp C minor))
    play))

(def bass
  (->>
      (phrase
        [3/2 1 1/2 1]
        [0   0   2 4])
    (then
      (phrase
        [3/2 1 1/2 1]
        [5   5   4 2]))
    (with :part ::bass)
    (skew :pitch (comp lower lower))))

(def wish-you-were-here-again 
  (->>
      (phrase
        [2/3 1/3 3/3 3/3 2/3 13/3]
        [0 1 0 4 0 0])
    (then
      (phrase
        [2/3 1/3 3/3 3/3 3/3 2/3 1/3 2/3 3/3 4/3]
        [0 1 0 4 0 2 3 2 1 0]))
    (skew :pitch raise)
    (with :part ::melody)))

(defn chord [degree duration]
  (->> (triad degree)
    (map #(zipmap
            [:time :duration :pitch]
            [0 duration %]))))

(def rhythm
  (->>
    (->> (chord 0 1)
      (after 1)
      (times 2))
    (accompany (->> (chord -2 1)
      (after 1)
      (times 2)
      (after 4)))
    (with :part ::rhythm)))

(def suns-on-the-rise 
  (->>
    (map #(zipmap [:time :duration :pitch] [0 4 %]) [0.5 3 5]) 
    (then (chord -2 4))
    (then (chord 0 4))
    (with :part ::rhythm)))

(def oooh
  (->>
    (phrase
      [3 1/3 2/3 3 2/3 1/3 3]
      [3 4 3 2 0 -1 0]) 
    (skew :pitch raise)
    (with :part ::melody)))

(def ska
  (->>
    (->> bass
      (times 2)
      (then (->> bass (accompany rhythm) (times 2)
            (accompany wish-you-were-here-again)
            (times 2)))
      (then (accompany oooh suns-on-the-rise))
      (then (->> bass (times 2) (after -4)))
      (skew :pitch (comp E minor)))
    (then (->> []
      (skew :pitch (comp E minor))))
    (skew :time (bpm 150))
    (skew :duration (bpm 200))))

;(play ska)
