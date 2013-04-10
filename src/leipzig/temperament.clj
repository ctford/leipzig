(ns leipzig.temperament)

(def concert-a 440)

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
          (mapcat repeat [7 1 4] [perfect-fifth wolf perfect-fifth])
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
