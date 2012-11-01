(ns leipzig.test.chord
  (:use midje.sweet leipzig.chord))

(fact
  (triad 3) =>
  {:i 3, :iii 5, :v 7})

(fact
  (seventh 4) =>
  {:i 4, :iii 6, :v 8, :vii 10})
