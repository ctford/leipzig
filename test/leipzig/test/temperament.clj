(ns leipzig.test.temperament
  (:use midje.sweet [leipzig.temperament :as temperament]))

(fact "Raising an octave doubles the frequency."
  (temperament/equal 69)        => (roughly 440)
  (temperament/equal (+ 69 12)) => (roughly 880) 
  (temperament/equal (- 69 12)) => (roughly 220))

(fact "A perfect fifth is flat by about two cents."
  (let [cent (java.lang.Math/pow 2 1/1200)]
    (/ (* 3/2 (temperament/equal 69)) (temperament/equal (+ 69 7)))
      => (roughly (java.lang.Math/pow cent 1.96))
    (/ (temperament/equal (- 69 7)) (* 2/3 (temperament/equal 69)))
      => (roughly (java.lang.Math/pow cent 1.96))))

(fact "A major third is sharp by about 14 cents."
  (let [cent (java.lang.Math/pow 2 1/1200)]
    (/ (temperament/equal (+ 69 4)) (* 5/4 (temperament/equal 69)))
      => (roughly (java.lang.Math/pow cent 13.69))
    (/ (* 4/5  (temperament/equal 69)) (temperament/equal (- 69 4)))
      => (roughly (java.lang.Math/pow cent 13.69))))

(fact "A semitone is the twelfth root of two."
  (/ (temperament/equal 70) (temperament/equal 69)) =>
    (roughly (java.lang.Math/pow 2 1/12))
  (/ (temperament/equal 69) (temperament/equal 68)) =>
    (roughly (java.lang.Math/pow 2 1/12)))
