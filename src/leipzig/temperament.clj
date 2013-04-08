(ns leipzig.temperament)

(defn equal [midi]
  (*
    440 ; concert A 
    (java.lang.Math/pow 2 (* 1/12 (- midi 69)))))
