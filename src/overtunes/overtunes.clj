(ns before.core
  (:use [overtone.live])
  (:use [overtunes.organ-cornet])
)

(def middle-section [
	(chord :G3  :minor)
	(chord :G3  :minor)
	(chord :Ab3 :major)
	(chord :Bb3 :major)
	(chord :Eb4 :major)
	(chord :Eb4 :major)
	(chord :Bb3 :major)
	(chord :Bb3 :major)
])

(def starting-section [
	(chord :Eb3 :major)
	(chord :Eb3 :major)
	(chord :C3 	:minor)
	(chord :C3 	:minor)
])

(def variation-section [
	(chord :G3  :minor)
	(chord :G3  :minor)
	(chord :Ab3 :major)
	(chord :Ab3 :major)
])

(defn play-chord [notes] 
  (mix 
    (map organ-cornet (map midi->hz notes))
  )
)

(def metro (metronome 240))

(defn play-progression [progression m start] ( do
	(at (m (+ start 0))  (play-chord (nth progression 0)) )
	(at (m (+ start 4))  (play-chord (nth progression 1)) )
	(at (m (+ start 8))  (play-chord (nth progression 2)) )
	(at (m (+ start 12)) (play-chord (nth progression 3)) )
	(at (m (+ start 16)) (play-chord (nth progression 4)) )
	(at (m (+ start 20)) (play-chord (nth progression 5)) )
	(at (m (+ start 24)) (play-chord (nth progression 6)) )
	(at (m (+ start 28)) (play-chord (nth progression 7)) )
))

(defn ad-infinitum [start] 
  (play-progression middle-section metro start)
  (apply-at (+ start 32) (ad-infinitum (+ start 32)))
)
