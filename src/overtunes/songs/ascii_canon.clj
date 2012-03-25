(ns overtunes.songs.ascii-canon
  (:use
    [clojure.string :only [blank?]]
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn bpm [beats-per-minute start] 
  (let [ms-per-minute (* 60 1000)
        ms-per-beat (/ ms-per-minute beats-per-minute)]
    #(+ start (* ms-per-beat %))))

(def lower #(- % 12))
(defn after [timing offset] #(timing (+ offset %)))
(defn tempo [timing factor] #(timing (/ % factor)))
(defn note# [note]
  (if-not (= \  note)
    (-> note int lower lower lower lower sampled-piano)))


(defn even-melody# [timing [note & notes]]
  (do
    (at (timing 0) (note# note))
    (let [next (after timing 1)]
      (if notes
        (even-melody# next notes)
        next))))


(defn play# []
      (let
        [timing (bpm 120 (now))
         melody "clojuring clojuring clojuring clojuring"]
        (even-melody# timing melody) 
        (even-melody# (after timing 1/2) melody)
        (even-melody# (after timing 1) melody)
        (even-melody# (after timing 3/2) melody)))

(play#)



