(ns leipzig.temperament)

(def ^{:private true} concert-a 440)

(defn equal
  "Converts midi to hertz using equal temperament.
  e.g. (equal 69)"
  [midi]
  (*
    concert-a
    (java.lang.Math/pow 2 (* 1/12 (- midi 69)))))

(defn pythagorean
  "Returns a function that converts midi to hertz using Pythagorean tuning, measuring
  ratios relative to root. The wolf tone is the fifth from one midi above root.
  e.g. ((pythagorean 61) 69)"
  [root] 
  (let [wolf 262144/177147
        perfect-fifth 3/2 
        ratios (->>
          (mapcat repeat [7 1 3] [perfect-fifth wolf perfect-fifth])
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
    (comp (partial * ( / 440 (scale 69))) scale)))

(defn meantone 
  "Returns a function that converts midi to hertz using quarter-comma meantone tuning,
  measuring ratios relative to root. The major third is a pure 5/4 ratio, but there are
  many wolf tones.
  e.g. ((meantone 61) 69)"
  [root] 
  (let [imperfect-fifth (java.lang.Math/pow 5 1/4) 
        wolf (* imperfect-fifth 128/125) 
        ratios (->>
          (mapcat repeat [7 1 3] [imperfect-fifth wolf imperfect-fifth])
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
    (comp (partial * (/ 440 (scale 69))) scale)))
