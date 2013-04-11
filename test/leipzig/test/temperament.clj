(ns leipzig.test.temperament
  (:use midje.sweet [leipzig.temperament :as temperament]))

(defn- ratio-of [tuning base interval] (/ (tuning (+ base interval)) (tuning base)))

(def octave 12)
(def fifth 7)
(def fourth 5)
(def major-third 4)
(def semitone 1)

(def exp #(java.lang.Math/pow %1 %2))
(defn- cent [r] (exp 2 (/ r 1200)))

(fact "Concert A is 440 Hz, regardless of tuning."
  (temperament/equal 69)            => 440.0
  ((temperament/pythagorean 69) 69) => 440
  ((temperament/pythagorean 70) 69) => 440
  ((temperament/meantone 69) 69)    => 440
  ((temperament/meantone 70) 69)    => 440.0) 

(fact "Equal temperament has pure octaves."
  (ratio-of temperament/equal 69 octave)     => 2.0 
  (ratio-of temperament/equal 69 (- octave)) => 0.5)

(fact "An equal temperament perfect fifth is flat by about two cents."
  (* (cent 1.96) (ratio-of temperament/equal 69 fifth))      => (roughly 3/2)
  (* (cent 1.96) (ratio-of temperament/equal 69 (- fourth))) => (roughly 3/4))

(fact "An equal temperament major third is sharp by about 14 cents."
  (/ (ratio-of temperament/equal 69 major-third) (cent 13.69))     => (roughly 5/4)
  (* (ratio-of temperament/equal 69 (- major-third)) (cent 13.69)) => (roughly 4/5))

(fact "An equal temperament semitone is the twelfth root of two."
  (ratio-of temperament/equal 69 semitone) => (roughly (exp 2 1/12))
  (ratio-of temperament/equal 68 semitone) => (roughly (exp 2 1/12)))

(fact "Pythagorean temperament has pure fifths, fourths and octaves."
  (ratio-of (temperament/pythagorean 69) 69 fifth)      => 3/2
  (ratio-of (temperament/pythagorean 69) 69 (- fourth)) => 3/4
  (ratio-of (temperament/pythagorean 69) 69 fourth)     => 4/3
  (ratio-of (temperament/pythagorean 69) 69 (- fifth))  => 2/3
  (ratio-of (temperament/pythagorean 69) 69 octave)     => 2/1
  (ratio-of (temperament/pythagorean 69) 69 (- octave)) => 1/2)

(fact "Meantone temperament has pure major thirds and octaves."
  (ratio-of (temperament/meantone 69) 69 major-third)     => (roughly 5/4) 
  (ratio-of (temperament/meantone 69) 69 (- major-third)) => (roughly 4/5) 
  (ratio-of (temperament/meantone 69) 69 octave)          => 2/1 
  (ratio-of (temperament/meantone 69) 69 (- octave))      => 1/2)
