(ns whelmed.melody
  (:use
    [overtone.live :only [at ctl midi->hz now]]))

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(defn sum-n [series n] (reduce + (take n series)))
(defn phrase [durations pitches]
  (let [timings (map (partial sum-n durations) (range (count durations)))]
    (map #(zipmap [:time :pitch :duration] [%1 %2 %3]) timings pitches durations)))

(def timing :time)
(def pitch :pitch)
(def duration :duration)
(defn skew [k f] (fn [points] (map #(update-in % [k] f) points)))
(defn shift [{t :time p :pitch}] (fn [points]
                                  (map
                                    (fn [point]
                                      (-> point 
                                       (update-in [:time] #(+ t %))
                                       (update-in [:pitch] #(+ p %))))
                                    points)))

(defn play-on# [instrument# notes]
  (let [play-at# (fn [{timing :time pitch :pitch duration :duration}]
                   (let [id (at timing (instrument# pitch))]
                       (at (+ timing duration) (ctl id :gate 0))))]
    (->> notes (map play-at#) dorun)))

(defn after [wait] (shift {:time wait :pitch 0}))
(defn follow
  ([first gap second] (follow first ((after gap) second)))
  ([first second]
    (let [{timing :time duration :duration} (last first)
          shifted ((after (+ duration timing)) second)]
      (concat first shifted))))

(defn times [phrase n] (reduce follow (repeat n phrase)))

(defn before? [a b] (<= (:time a) (:time b)))
(defn accompany [[a & other-as :as as] [b & other-bs :as bs]]
  (if (empty? as)
    bs
    (if (empty? bs)
      as
      (if (before? a b)
        (cons a (accompany other-as bs))
        (cons b (accompany as other-bs)))))) 
