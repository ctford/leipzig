(ns leipzig.temperament)

(defn equal [midi]
  (*
    440 ; concert A 
    (java.lang.Math/pow 2 (* 1/12 (- midi 69)))))

(defn pythagorean [midi] 
  (let [ratios
          (->> (iterate (partial * 3/2) 1)
            (take 11)
            (map (fn normalise [r] (if (< r 2) r (normalise (/ r 2))))) 
            sort)
        concert (- midi (dec 69))]
    (cond
      (< concert 0) (* 1/2 (pythagorean (+ midi 11)))
      (> concert 10) (* 2 (pythagorean (- midi 11)))
      :otherwise (* (/ 440 (nth ratios 1)) (nth ratios concert)))))
