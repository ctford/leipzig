(ns leipzig.test.canon
  (:use midje.sweet leipzig.canon)
  (:require [leipzig.melody :as melody]))

(fact
  (->> [{:time 1 :pitch 1}] (canon (comp (simple 3) (interval 4)))) =>
    [{:time 1 :pitch 1} {:time 4 :pitch 5}])

(fact
  (->> [{:time 0 :pitch 1 :duration 1}] (canon (comp crab mirror))) =>
    [{:time -1 :pitch -1 :duration 1} {:time 0 :pitch 1 :duration 1}])

(fact
  (->> [{:time 0 :pitch 1 :duration 1}] (canon table)) =>
    [{:time -1 :pitch -1 :duration 1} {:time 0 :pitch 1 :duration 1}])

(fact "Crab canons reverse notes based on when they finish."
  (->> (melody/rhythm [1/2 1]) (canon crab)) => 
    [{:duration 1 :time -3/2} {:duration 1/2 :time -1/2}
     {:duration 1/2 :time 0} {:duration 1 :time 1/2}])
