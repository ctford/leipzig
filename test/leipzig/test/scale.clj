(ns leipzig.test.scale
  (:use midje.sweet leipzig.scale))

(fact
  (map (comp C major) (range 0 8)) =>
  [60 62 64 65 67 69 71 72])
