(ns leipzig.test.scale
  (:use midje.sweet leipzig.scale))

(fact
  (map (comp B lydian low) (range 0 8)) =>
  [59 61 63 65 66 68 70 71])

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
  (map (comp A dorian) (range 0 8)) =>
  [69 71 72 74 76 78 79 81])

(fact
  (map (comp B mixolydian) (range 0 -8 -1)) =>
  [71 69 68 66 64 63 61 59])

(fact "Fractional degrees represent accidentals."
  (map (comp C phrygian high) [0 1 3/2 2 5/2 3 7/2 4 5 11/2 6 13/2 7]) =>
  (range 72 85))

(fact "Fractional degrees are linearly interpolated."
  (map blues [0 1/3 2/3 1 3/2 2 3 4 13/3 14/3 5 11/2 6]) =>
  (range 13))
