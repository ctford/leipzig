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
  (map (comp F flat chromatic) (range 0 12)) =>
  (range 64 76))

(fact
  (map (comp G sharp pentatonic) (range 0 6)) =>
  [68 71 73 75 78 80])

(fact
  (map (comp A dorian low) (range 0 8)) =>
  [57 59 60 62 64 66 67 69])

(fact
  (map (comp B mixolydian high) (range 0 8)) =>
  [83 85 87 88 90 92 93 95])
