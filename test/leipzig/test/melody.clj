(ns leipzig.test.melody
  (:use [midje.sweet :exclude [after]]
        leipzig.melody))

(fact
  (->> [{:time 2}] (where :time (bpm 60))) =>
    [{:time 2000}])

(fact
  (->> [{}] (where :part (is :bass))) =>
    [{:part :bass}])

(fact "wherever can be used to provide default values to keys."
  (->> [{:time 0} {:time 1, :part :piano}]
    (wherever (comp not :part), :part (is :bass))) =>
    [{:time 0, :part :bass}
     {:time 1, :part :piano}])

(fact "rhythm takes sequential durations and produces a rhythm."
  (rhythm [1 2]) =>
    [{:time 0 :duration 1}
     {:time 1 :duration 2}])

(fact
  (phrase [1 2] [3 4]) =>
    [{:time 0 :duration 1 :pitch 3}
     {:time 1 :duration 2 :pitch 4}])

(fact
  (->> (phrase [1] [2]) (then (phrase [3] [4]))) =>
    [{:time 0 :duration 1 :pitch 2}
     {:time 1 :duration 3 :pitch 4}])

(fact
  (->> (phrase [1] [2]) (then (after -2 (phrase [3 1] [4 5])))) =>
    [{:time -1 :duration 3 :pitch 4}
     {:time 0 :duration 1 :pitch 2}
     {:time 2 :duration 1 :pitch 5}])

(fact
  (->> (phrase [2] [1]) (times 2)) =>
    [{:time 0 :duration 2 :pitch 1}
     {:time 2 :duration 2 :pitch 1}])

(fact
  (->> (phrase [1 2] [2 3]) (after 1) (with (phrase [2] [1]))) =>
    [{:time 0 :duration 2 :pitch 1}
     {:time 1 :duration 1 :pitch 2}
     {:time 2 :duration 2 :pitch 3}])

(fact "phrase is lazy."
  (->> (phrase (repeat 2) (repeat 1)) (take 1)) =>
    [{:time 0 :duration 2 :pitch 1}])

(fact "with is lazy."
  (take 2 (with (repeat {:time 1}) (repeat {:time 2}))) =>
    [{:time 1}, {:time 1}])
