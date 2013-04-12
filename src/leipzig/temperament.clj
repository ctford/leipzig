(ns leipzig.temperament)

(defn- align-concert-a [tuning] (fn [midi] (-> midi tuning (* (/ 440 (tuning 69))))))
(def ^{:private true} geometric-progression (partial reductions * 1))
(def ^{:private true} pythagorean-comma 531441/524288)

(defn- tune 
  [root incremental-ratios] 
  (let [ratios (->>
                 (geometric-progression incremental-ratios) 
                 (map (fn normalise [ratio] (if (< ratio 2) ratio (normalise (/ ratio 2))))) 
                 sort) 
        tuning (fn temper [midi]
                (let [normal (- midi root)]
                  (cond
                    (< normal 0) (* 1/2 (temper (+ midi 12)))
                    (> normal 11) (* 2 (temper (- midi 12)))
                    :otherwise (nth ratios normal))))]
    (align-concert-a tuning)))

(def equal
  "Converts midi to hertz using equal temperament.
  e.g. (equal 69)"
  (tune 69 (repeat 11 (java.lang.Math/pow 2 1/12))))

(defn pythagorean
  "Returns a function that converts midi to hertz using Pythagorean tuning, measuring
  ratios relative to root. The wolf tone is the fifth from one midi above root.
  e.g. ((pythagorean 61) 69)"
  [root] 
  (let [pure-fifth 3/2 
        wolf (/ pure-fifth pythagorean-comma)
        ratios (mapcat repeat [7 1 3] [pure-fifth wolf pure-fifth])]
    (tune root ratios)))

(defn meantone 
  "Returns a function that converts midi to hertz using quarter-comma meantone tuning,
  measuring ratios relative to root. The major third is a pure 5/4 ratio, but there are
  many wolf tones.
  e.g. ((meantone 61) 69)"
  [root] 
  (let [impure-fifth (java.lang.Math/pow 5 1/4)
        wolf (* impure-fifth 128/125)
        ratios (mapcat repeat [7 1 3] [impure-fifth wolf impure-fifth])]
    (tune root ratios)))

(defn werckmeister-i
  "Returns a function that converts midi to hertz using Werckmeister's well-temperament
  based on 1/4 comma divisions, measuring ratios relative to root.
  e.g. ((werckmeister-i 61) 69)"
  [root] 
  (let [pure-fifth 3/2
        narrow-fifth (/ pure-fifth (java.lang.Math/pow pythagorean-comma 1/4)) 
        ratios (mapcat repeat [3 2 1 5] [narrow-fifth pure-fifth narrow-fifth pure-fifth])]
    (tune root ratios)))

(defn werckmeister-ii
  "Returns a function that converts midi to hertz using Werckmeister's well-temperament
  based on 1/3 comma divisions, measuring ratios relative to root.
  e.g. ((werckmeister-ii 61) 69)"
  [root] 
  (let [pure-fifth 3/2
        narrow-fifth (/ pure-fifth (java.lang.Math/pow pythagorean-comma 1/3)) 
        wide-fifth (* pure-fifth (java.lang.Math/pow pythagorean-comma 1/3)) 
        ratios (mapcat repeat [1 1 1 1 1 1 1 1 2 1] [narrow-fifth pure-fifth
                                                     narrow-fifth pure-fifth
                                                     narrow-fifth pure-fifth
                                                     narrow-fifth pure-fifth
                                                     wide-fifth narrow-fifth])]
    (tune root ratios)))
