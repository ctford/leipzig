(ns leipzig.temperament)

(defn equal [midi]
  (*
    8.1757989156 ; midi zero
    (java.lang.Math/pow 2 (* 1/12 midi))))
