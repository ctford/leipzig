(ns leipzig.test.scale
  (:use midje.sweet leipzig.canon))

(fact
  (->> [{:time 1 :pitch 1}] (canon (comp (simple 3) (interval 4)))) =>
    [{:time 1 :pitch 1} {:time 4 :pitch 5}])

(fact
  (->> [{:time 1 :pitch 1}] (canon (comp crab mirror))) =>
    [{:time -1 :pitch -1} {:time 1 :pitch 1}])

(fact
  (->> [{:time 1 :pitch 1}] (canon table)) =>
    [{:time -1 :pitch -1} {:time 1 :pitch 1}])

