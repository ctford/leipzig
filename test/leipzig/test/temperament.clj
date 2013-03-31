(ns leipzig.test.temperament
  (:use midje.sweet [leipzig.temperament :as temperament]))

(fact "Raising an octave doubles the frequency."
  (temperament/equal 69)        => (roughly 440)
  (temperament/equal (+ 69 12)) => (roughly 880) 
  (temperament/equal (- 69 12)) => (roughly 220))

(fact "A perfect fifth is flat by about two cents."
  (let [cent (java.lang.Math/pow 2 1/1200)]
    (/ (* 3/2 (temperament/equal 69)) (temperament/equal (+ 69 7)))
      => (roughly (* cent cent))
    (/ (temperament/equal (- 69 7)) (* 2/3 (temperament/equal 69)))
      => (roughly (* cent cent))))

(fact "A semitone is the twelfth root of two."
  (/ (temperament/equal 70) (temperament/equal 69)) =>
    (roughly (java.lang.Math/pow 2 1/12))
  (/ (temperament/equal 69) (temperament/equal 68)) =>
    (roughly (java.lang.Math/pow 2 1/12)))
