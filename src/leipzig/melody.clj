(ns leipzig.melody
  (:use
    [overtone.live :only [at ctl midi->hz now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn bpm [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(defn- sum-n [series n] (reduce + (take n series)))
(defn phrase [durations pitches]
  (let [timings (map (partial sum-n durations) (range (count durations)))]
    (map #(zipmap [:time :pitch :duration] [%1 %2 %3]) timings pitches durations)))

(defn where [k f notes] (map #(update-in % [k] f) notes))
(defn after [wait notes] (where :time #(+ wait %) notes))
(defn with [k v notes] (map #(assoc % k v) notes))

(defn then 
  [second first]
    (let [{timing :time duration :duration} (last first)
          shifted (after (+ duration timing) second)]
      (concat first shifted)))

(defn times [n notes] (reduce then (repeat n notes)))

(defn- before? [a b] (<= (:time a) (:time b)))
(defn with [[a & other-as :as as] [b & other-bs :as bs]]
  (if (empty? as)
    bs
    (if (empty? bs)
      as
      (if (before? a b)
        (cons a (with other-as bs))
        (cons b (with as other-bs)))))) 

(defmulti play-note :part)
(defmethod play-note :default [{:keys [pitch time duration]}]
   (let [id (at time (sampled-piano pitch))]
        (at (+ time duration) (ctl id :gate 0))))

(defn- trickle [notes]
  (if-let [{:keys [time] :as note} (first notes)]
    (cons note
      (lazy-seq
        (do
          (Thread/sleep (max 0 (- time (+ 1000 (now))))) 
          (trickle (rest notes)))))))

(defn play [notes] 
  (->>
    notes
    (after (now))
    trickle
    (map (fn [{:keys [time] :as note}] (at time (play-note note))))
    dorun))

(defn cut [start end notes] (->> notes (take end) (drop start)))
(defn except [start end notes]
    (concat
          (take start notes)
          (drop end notes)))
