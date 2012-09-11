(ns whelmed.melody
  (:use
    [overtone.live :only [at ctl midi->hz now]]))

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(defn- sum-n [series n] (reduce + (take n series)))
(defn phrase [durations pitches]
  (let [timings (map (partial sum-n durations) (range (count durations)))]
    (map #(zipmap [:time :pitch :duration] [%1 %2 %3]) timings pitches durations)))

(defn skew [k f notes] (map #(update-in % [k] f) notes))
(defn after [wait notes] (skew :time #(+ wait %) notes))

(defn follow
  [second first]
    (let [{timing :time duration :duration} (last first)
          shifted (after (+ duration timing) second)]
      (concat first shifted)))

(defn times [n notes] (reduce follow (repeat n notes)))

(defn before? [a b] (<= (:time a) (:time b)))
(defn accompany [[a & other-as :as as] [b & other-bs :as bs]]
  (if (empty? as)
    bs
    (if (empty? bs)
      as
      (if (before? a b)
        (cons a (accompany other-as bs))
        (cons b (accompany as other-bs)))))) 

;(defmulti play-note :part)
;(defn play [notes] 
;  (->>
;    notes
;    (after (now))
;    (map (fn [{:keys [time] :as note}] (at time (play-note note))))
;    dorun))
