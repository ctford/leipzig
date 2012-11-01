(ns leipzig.test.melody
  (:use [midje.sweet :exclude [after]]
        leipzig.melody))

(fact
  (->> [{:time 2}] (where :time (bpm 60))) =>
    [{:time 2000}])

(fact
  (->> [{}] (where :part (is :bass))) =>
    [{:part :bass}])

(fact
  (phrase [1 2] [3 4]) =>
    [{:time 0 :duration 1 :pitch 3}
     {:time 1 :duration 2 :pitch 4}])

(fact
  (->> (phrase [1] [2]) (then (phrase [3] [4]))) =>
    [{:time 0 :duration 1 :pitch 2}
     {:time 1 :duration 3 :pitch 4}])

(fact
  (->> (phrase [2] [1]) (times 2)) =>
    [{:time 0 :duration 2 :pitch 1}
     {:time 2 :duration 2 :pitch 1}])

(fact
  (->> (phrase [1 2] [2 3]) (after 1) (with (phrase [2] [1]))) =>
    [{:time 0 :duration 2 :pitch 1}
     {:time 1 :duration 1 :pitch 2}
     {:time 2 :duration 2 :pitch 3}])
