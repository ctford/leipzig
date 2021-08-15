(ns leipzig.test.temperament
  (:require [leipzig.temperament :as temperament]
            [midje.sweet :refer :all]))

(defn- ratio-of [tuning base interval]
  (/ (tuning (+ base interval)) (tuning base)))

(defn- ratio-of-sum [tuning base interval-1 interval-2]
  (*
   (/ (tuning (+ base interval-1)) (tuning base))
   (/ (tuning (+ base interval-2)) (tuning base))))

(defmacro def- [sym init] `(def ^:private ~sym ~init))

(def- octave 12)
(def- minor-seventh 10)
(def- major-sixth 9)
(def- minor-sixth 8)
(def- fifth 7)
(def- augmented-fourth 6)
(def- fourth 5)
(def- major-third 4)
(def- minor-third 3)
(def- tone 2)
(def- semitone 1)
(def- unison 0)

(def- exp #(java.lang.Math/pow %1 %2))
(defn- cent [r] (exp 2 (/ r 1200)))

(fact "Concert A is 440 hertz, regardless of tuning."
  (temperament/equal 69)                 => 440
  ((temperament/pythagorean 69) 69)      => 440
  ((temperament/pythagorean 70) 69)      => 440
  ((temperament/just 69) 69)             => 440
  ((temperament/just 70) 69)             => 440
  ((temperament/seven-limit-just 69) 69) => 440
  ((temperament/seven-limit-just 70) 69) => 440
  ((temperament/meantone 69) 69)         => 440
  ((temperament/meantone 70) 69)         => 440.0
  ((temperament/werckmeister-i 69) 69)   => 440
  ((temperament/werckmeister-i 70) 69)   => 440.0
  ((temperament/werckmeister-ii 69) 69)  => 440
  ((temperament/werckmeister-ii 70) 69)  => 440.0
  ((temperament/werckmeister-iii 69) 69) => 440
  ((temperament/werckmeister-iii 70) 69) => 440.0) 

(fact "Equal temperament has pure octaves."
  (ratio-of temperament/equal 69 octave)     => 2/1 
  (ratio-of temperament/equal 69 (- octave)) => 1/2)

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

(fact "Five-limit just intonation has pure sixths, fifths, fourths, thirds and octaves."
  (ratio-of (temperament/just 69) 69 fifth)                       => 3/2
  (ratio-of-sum (temperament/just 69) 69 fifth fourth)            => 2/1
  (ratio-of (temperament/just 69) 69 major-third)                 => 5/4
  (ratio-of-sum (temperament/just 69) 69 major-third minor-sixth) => 2/1
  (ratio-of (temperament/just 69) 69 major-sixth)                 => 5/3
  (ratio-of-sum (temperament/just 69) 69 major-sixth minor-third) => 2/1
  (ratio-of (temperament/just 69) 69 octave)                      => 2/1
  (ratio-of-sum (temperament/just 69) 69 octave unison)           => 2/1)

(fact "Seven-limit just intonation has more consonant major seconds, augmented fourths
      and minor sevenths."
  (ratio-of (temperament/seven-limit-just 69) 69 tone)             => 8/7
  (ratio-of (temperament/seven-limit-just 69) 69 augmented-fourth) => 7/5
  (ratio-of (temperament/seven-limit-just 69) 69 minor-seventh)    => 7/4)

(fact "Meantone temperament has pure major thirds and octaves."
  (ratio-of (temperament/meantone 69) 69 major-third)     => (roughly 5/4) 
  (ratio-of (temperament/meantone 69) 69 (- major-third)) => (roughly 4/5) 
  (ratio-of (temperament/meantone 69) 69 octave)          => 2/1 
  (ratio-of (temperament/meantone 69) 69 (- octave))      => 1/2)

(fact "Werckmeister I has pure fourths and dominant sevenths."
  (ratio-of (temperament/werckmeister-i 69) 69 fourth)        => (roughly 4/3) 
  (ratio-of (temperament/werckmeister-i 69) 69 minor-third)   => (roughly 32/27) 
  (ratio-of (temperament/werckmeister-i 69) 69 minor-seventh) => (roughly 16/9))

(fact "Werckmeister II has pure fourths." 
  (ratio-of (temperament/werckmeister-ii 69) 69 minor-third)  => (roughly 32/27) 
  (ratio-of (temperament/werckmeister-ii 69) 69 fourth)       => (roughly 4/3))

(fact "Werckmeister III has pure fifths and seconds." 
  (ratio-of (temperament/werckmeister-iii 69) 69 tone)  => (roughly 9/8) 
  (ratio-of (temperament/werckmeister-iii 69) 69 fifth) => (roughly 3/2))
