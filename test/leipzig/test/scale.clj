(ns leipzig.test.scale
  (:use midje.sweet leipzig.scale))

(fact
  (map (comp C major) (range 0 8)) =>
  [60 62 64 65 67 69 71 72])

(fact
  (map (comp D minor) (range 0 8)) =>
  [62 64 65 67 69 70 72 74])

(fact
  (map (comp E blues) (range 0 7)) =>
  [64 67 69 70 71 74 76])

(fact
  (map (comp G pentatonic) (range 0 6)) =>
  [67 70 72 74 77 79])
