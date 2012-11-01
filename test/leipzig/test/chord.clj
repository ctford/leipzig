(ns leipzig.test.chord
  (:use midje.sweet leipzig.chord))

(fact
  (triad 3) =>
  {:i 3, :iii 5, :v 7})

(fact
  (seventh 4) =>
  {:i 4, :iii 6, :v 8, :vii 10})

(fact
  (ninth 5) =>
  {:i 5, :iii 7, :v 9, :vii 11, :ix 13})
