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
  (fn temper [midi]
    (let [ratios
            (->> (iterate (partial * 3/2) 1)
              (take 12)
              (map (fn normalise [r] (if (< r 2) r (normalise (/ r 2))))) 
              sort)
          normal (- midi root 1)]
      (cond
        (< normal 0) (* 1/2 (temper (+ midi 12)))
        (> normal 11) (* 2 (temper (- midi 12)))
        :otherwise (* 440 (nth ratios normal))))))
