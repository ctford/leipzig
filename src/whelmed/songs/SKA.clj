(ns whelmed.songs.SKA
  (:use
        [whelmed.melody]
        [whelmed.scale]
        [whelmed.instrument]
        [overtone.live :only [ctl at midi->hz now stop]]))

(defn with [k v notes] (map #(assoc % k v) notes))

(def bass
  (->>
    (phrase
      [3/2 1 1/2 1]
      [0   0   2 4])
    (follow
      (phrase
        [3/2 1 1/2 1]
        [5   5   4 2]))
    (with :part :bass)))

(def rhythm
  (->>
    (phrase
      [2 6]
      [14 18])
    (with :part :rhythm)))

(def chords (map triad [0 5]))

(def rhythm
  (let [chord (fn [degree] (map #(zipmap [:time :duration :pitch] [0 2 %]) (triad degree)))]
    (->>
      (times 2 (chord 14))
      (after 1)
      (follow (times 2 (chord 12))))))

(def variation 
  (let [chord (fn [degree] (map #(zipmap [:time :duration :pitch] [0 4 %]) (triad degree)))]
  (follow (chord 15) (chord 12))))

(defmulti play-note :part)
;(defmethod play :melody [note] (play-on# (comp sinish# midi->hz) [note]))
;(defmethod play :rhythm [note] (play-on# (comp #(groan# % 10) midi->hz) [note]))
;(defmethod play :bass [note] (play-on# (comp sinish# midi->hz) [note]))
(defmethod play-note :default [{:keys [pitch time duration]}]
 (let [id (at time (piano# pitch))]
   (at (+ time duration) (ctl id :gate 0))))

(def low #(- % 7))

(def ska
  (->>
    bass
    (accompany rhythm)
    (times 4)
    (skew :pitch (comp low low low low E minor))
    (skew :time (bpm 150))
    (skew :duration (bpm 200))))

(defn play [notes] 
    (->>
      notes
      (after (now))
      (map (fn [{:keys [time] :as note}] (at time (play-note note))))
      dorun))

(play ska)
