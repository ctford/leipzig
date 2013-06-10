(ns leipzig.test.canon
  (:use midje.sweet leipzig.canon)
  (:require [leipzig.melody :as melody]))

(fact "Canons accompany a melody with a functional transformation of itself."
  (->> [{:time 1 :pitch 1}] (canon mirror)) =>
    [{:time 1 :pitch 1} {:time 1 :pitch -1}])

(fact "Canon flavours can compose."
  (->> [{:time 1 :pitch 1}] (canon (comp (simple 3) (interval 4)))) =>
    [{:time 1 :pitch 1} {:time 4 :pitch 5}])

(fact "Crab canons reverse notes based on when they finish."
  (->> (melody/rhythm [1/2 1]) (canon crab)) => 
    [{:duration 1/2 :time 0} {:duration 1 :time 0}
     {:duration 1 :time 1/2} {:duration 1/2 :time 1}])

(fact "Crab preserves the time-order invariate."
  (->> (melody/rhythm [1 1/2]) crab) =>
    [{:duration 1/2 :time 0} {:duration 1 :time 1/2}])

(fact "Canon is lazy."
  (->> (melody/rhythm (repeat 1)) (canon (simple 1/2)) (take 2)) =>
    [{:time 0 :duration 1} {:time 1/2 :duration 1}])
