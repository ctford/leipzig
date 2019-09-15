(ns leipzig.test.melody
  (:require [leipzig.chord :as chord]
            [leipzig.scale :as scale]
            [midje.sweet :refer :all :exclude [after]]
            [leipzig.melody :refer :all]))

(fact "Beats-per-minute is rendered in seconds."
  (->> [{:time 90 :duration 90}] (tempo (bpm 90))) =>
    [{:time 60 :duration 60}])

(fact "wherever applies a function to selected notes."
  (->> [{:time 0 :duration 0} {:time 1 :duration 3}]
    (wherever (comp even? :time), :part (is :bass))) =>
    [{:time 0 :duration 0 :part :bass}
     {:time 1 :duration 3}])

(fact "cut truncates or pads a melody to fit."
  (->> (phrase [1 2 3] [0 1 2]) (cut 3)) =>
    [{:time 0, :duration 1, :pitch 0}
     {:time 1, :duration 2, :pitch 1}]

  (->> (phrase [1 2 3] [0 1 2]) (cut 2)) =>
    [{:time 0, :duration 1, :pitch 0}
     {:time 1, :duration 1, :pitch 1}]

  (->> (phrase [1 2 3] [0 1 2]) (cut 7)) =>
    [{:time 0, :duration 1, :pitch 0}
     {:time 1, :duration 2, :pitch 1}
     {:time 3, :duration 3, :pitch 2}
     {:time 6, :duration 1, :rest? true}])

(fact "but substitutes part of a melody for another."
   (->> (phrase (repeat 1) [0 1 2 3])
     (but 2 4 (phrase (repeat 1/4) [8 7 6 5]))) =>
      [{:time 0 :duration 1 :pitch 0}
       {:time 1 :duration 1 :pitch 1}
       {:time 2 :duration 1/4 :pitch 8}
       {:time 9/4 :duration 1/4 :pitch 7}
       {:time 10/4 :duration 1/4 :pitch 6}
       {:time 11/4 :duration 1/4 :pitch 5}])

(fact "but truncates notes that fall in the exlusion interval."
   (->> (phrase (repeat 4) [0 1])
     (but 6 8 (phrase (repeat 1) [2 3]))) =>
      [{:time 0 :duration 4 :pitch 0}
       {:time 4 :duration 2 :pitch 1}
       {:time 6 :duration 1 :pitch 2}
       {:time 7 :duration 1 :pitch 3}])

(fact "where applies a function to all notes with the specified key."
  (->> [{:time 0 :duration 0 :volume 2} {:time 1 :duration 3 :volume 3} {:time 2 :duration 2}]
    (where :volume inc)) =>
    [{:time 0 :duration 0 :volume 3}
     {:time 1 :duration 3 :volume 4}
     {:time 2 :duration 2}])

(fact "rhythm takes sequential durations and produces a rhythm."
  (rhythm [1 2]) =>
    [{:time 0 :duration 1}
     {:time 1 :duration 2}])

(fact "having zips arbitrary attributes onto a melody."
  (->> (rhythm [1 2]) (having :drum [:kick :snare]))
    [{:time 0 :duration 1 :drum :kick}
     {:time 1 :duration 2 :drum :snare}])

(fact "all can be used to set a constant value across a melody."
  (->> (rhythm [1 2]) (all :part :drum)) =>
    [{:time 0 :duration 1 :part :drum}
     {:time 1 :duration 2 :part :drum}])

(fact "phrase builds a (potentially polyphonic) melody."
  (phrase [1 2] [3 4]) =>
    [{:time 0 :duration 1 :pitch 3}
     {:time 1 :duration 2 :pitch 4}]

  (phrase [1 1 2] [3 nil 4]) =>
    [{:time 0 :duration 1 :pitch 3}
     {:time 1 :duration 1 :rest? true}
     {:time 2 :duration 2 :pitch 4}]

  (phrase [1 2] [0 [2 4]]) =>
    [{:time 0 :duration 1 :pitch 0}
     {:time 1 :duration 2 :pitch 2}
     {:time 1 :duration 2 :pitch 4}]

  (phrase [1 2] [0 (map inc [-1 1 3])]) =>
    [{:time 0 :duration 1 :pitch 0}
     {:time 1 :duration 2 :pitch 0}
     {:time 1 :duration 2 :pitch 2}
     {:time 1 :duration 2 :pitch 4}]

  (phrase [1 2] [0 (-> chord/triad (chord/root 3))]) =>
    [{:time 0 :duration 1 :pitch 0}
     {:time 1 :duration 2 :pitch 3}
     {:time 1 :duration 2 :pitch 5}
     {:time 1 :duration 2 :pitch 7}]

  (phrase [1 2] [2 (-> chord/triad (dissoc :iii))]) =>
    [{:time 0 :duration 1 :pitch 2}
     {:time 1 :duration 2 :pitch 0}
     {:time 1 :duration 2 :pitch 4}]

  (phrase [1 2] [2 3] [0.3 0.4]) =>
    [{:time 0 :duration 1 :pitch 2 :velocity 0.3}
     {:time 1 :duration 2 :pitch 3 :velocity 0.4}]

  (phrase [1 [1/2 1/2]] [2 4]) =>
    [{:time 0 :duration 1 :pitch 2}
     {:time 1 :duration 1/2 :pitch 4}
     {:time 3/2 :duration 1/2 :pitch 4}]

  (phrase [1 [1/2 1/2]] [2 [0 4]]) =>
    [{:time 0 :duration 1 :pitch 2}
     {:time 1 :duration 1/2 :pitch 0}
     {:time 1 :duration 1/2 :pitch 4}
     {:time 3/2 :duration 1/2 :pitch 0}
     {:time 3/2 :duration 1/2 :pitch 4}])

(fact "The duration of notes is determined by the note that finishes last."
  (->> (rhythm []) duration) => 0
  (->> (rhythm [1 2 3]) duration) => 6
  (->> (rhythm [1 2 3]) (with (rhythm [100])) duration) => 100)

(fact "then puts one melody after another."
  (->> [] (then (phrase [3] [4]))) =>
    [{:time 0 :duration 3 :pitch 4}]

  (->> (phrase [1] [2]) (then (phrase [3] [4]))) =>
    [{:time 0 :duration 1 :pitch 2}
     {:time 1 :duration 3 :pitch 4}]

  (->> (phrase [1] [2]) (then (after -2 (phrase [3 1] [4 5])))) =>
    [{:time -1 :duration 3 :pitch 4}
     {:time 0 :duration 1 :pitch 2}
     {:time 2 :duration 1 :pitch 5}])

(fact "mapthen transforms several melodies then joins them up."
  (mapthen drop-last
           [1 2]
           [(phrase [1 1] [2 2])
           (phrase [3 3 3] [4 4 4])]) =>
  [{:time 0 :duration 1 :pitch 2}
   {:time 1 :duration 3 :pitch 4}])

(fact "times repeats a melody."
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

(fact "with is variadic."
  (with (rhythm [1]) (rhythm [2]) (rhythm [3])) => 
    [{:time 0 :duration 1} {:time 0 :duration 2} {:time 0 :duration 3}]

  (with (rhythm [1])) =>
    [{:time 0 :duration 1}]

  (with) =>
    [])

(fact "tempo transforms both time and duration."
      (->> [{:time 0 :duration 1/2} {:time 1/2 :duration 1/2}]
           (tempo (comp (scale/scale [2/3 1/3]) (partial * 2)))) =>
      [{:time 0 :duration 2/3} {:time 2/3 :duration 1/3}]) 

(fact "An accelerando linearly interpolates between two relatively different rates."
      (->> (phrase [1 1 1] [0 0 0]) (tempo (accelerando 0 2 1))) =>
      (phrase [1 1 1] [0 0 0])

      (->> (phrase [1 1 1] [0 0 0]) (tempo (accelerando 0 2 2))) =>
      (phrase [5/4 7/4 2] [0 0 0])

      (->> (phrase [1 1 1] [0 0 0]) (tempo (accelerando 0 2 1/2))) =>
      (phrase [7/8 5/8 1/2] [0 0 0]))

(future-fact "interpolate linearly interpolates between the supplied coordinates."
  ((interpolate [[0 0] [1 1]]) 1/2) => 1/2
  ((interpolate [[0 0] [1 1] [2 2]]) 3/2) => 3/2)

(future-fact "interpolate returns 1 outside the supplied coordinates."
  ((interpolate []) 1) => 1
  ((interpolate [[2 2]]) 0) => 1
  ((interpolate [[2 2]]) 3) => 1)
