(ns leipzig.temperament)

(defn- align [tuning] (comp (partial * ( / 440 (tuning 69))) tuning))

(defn equal
  "Converts midi to hertz using equal temperament.
  e.g. (equal 69)"
  [midi]
  ((align #(java.lang.Math/pow 2 (/ % 12))) midi))

(defn- tune 
  [root raw-ratios] 
  (let [ratios (->> raw-ratios
          (cons 1)
          (reductions *)
          (map (fn normalise [r] (if (< r 2) r (normalise (/ r 2))))) 
          sort) 
        scale (fn temper [midi]
                (let [normal (- midi root)]
                  (cond
                    (< normal 0) (* 1/2 (temper (+ midi 12)))
                    (> normal 11) (* 2 (temper (- midi 12)))
                    :otherwise (nth ratios normal))))]
    (align scale)))

(defn pythagorean
  "Returns a function that converts midi to hertz using Pythagorean tuning, measuring
  ratios relative to root. The wolf tone is the fifth from one midi above root.
  e.g. ((pythagorean 61) 69)"
  [root] 
  (let [pure-fifth 3/2 
        wolf 262144/177147
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
