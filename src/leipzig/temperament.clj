(ns leipzig.temperament)

(def pythagorean-comma
  "The difference between an octave constructed out of pure fifths, and one constructed
  from a pure 2/1 ratio."
  531441/524288)

(defn- align-concert-a [tuning] (fn [midi] (-> midi tuning (* (/ 440 (tuning 69))))))
(defn- temper [midi ratios]
  (cond
    (< midi 0) (* 1/2 (temper (+ midi 12) ratios))
    (> midi 11) (* 2 (temper (- midi 12) ratios))
    :otherwise (nth ratios midi)))

(defn- tune 
  [root incremental-ratios] 
  (let [geometric-progression (partial reductions * 1)
        ratios (->>
                 (geometric-progression incremental-ratios) 
                 (map (fn normalise [ratio] (if (< ratio 2) ratio (normalise (/ ratio 2))))) 
                 sort) 
        tuning (fn [midi] (-> midi (- root) (temper ratios)))]
    (align-concert-a tuning)))

(def equal
  "Converts midi to hertz using equal temperament.
  e.g. (equal 69)"
  (tune 69 (repeat 11 (java.lang.Math/pow 2 1/12))))

(defn pythagorean 
  "Returns a function that converts midi to hertz using Pythagorean temperament, measuring
  ratios relative to root. The wolf tone is the fifth from one midi above root.
  e.g. ((pythagorean 61) 69)"
  [root] 
  (let [pure 3/2 
        wolf (/ pure pythagorean-comma)
        fifths (mapcat repeat [7 1 3] [pure wolf pure])]
    (tune root fifths)))

(defn just [root]
  "Returns a function that converts midi to hertz using just intonation, measuring ratios
  relative to root. Specifically, this is Ptolemy's intense diatonic scale, but has a
  wolf tone from the major second to the fourth, which makes it unusable for aeolian mode.
  e.g. ((just 61) 69)"
  (align-concert-a
    (fn [midi]
      (temper (- midi root) [1/1 135/128 9/8 6/5 5/4 4/3 45/32 3/2 8/5 27/16 9/5 15/8]))))

(defn meantone 
  "Returns a function that converts midi to hertz using quarter-comma meantone tuning,
  measuring ratios relative to root. The major third is a pure 5/4 ratio, but there are
  many wolf tones.
  e.g. ((meantone 61) 69)"
  [root] 
  (let [narrow (java.lang.Math/pow 5 1/4)
        wolf (* narrow 128/125)
        fifths (mapcat repeat [7 1 3] [narrow wolf narrow])]
    (tune root fifths)))

(defn werckmeister-i
  "Returns a function that converts midi to hertz using Werckmeister's well-temperament
  based on 1/4 comma divisions (Werkmeister I). Ratios are relative to root.
  e.g. ((werckmeister-i 61) 69)"
  [root] 
  (let [pure 3/2
        narrow (/ pure (java.lang.Math/pow pythagorean-comma 1/4)) 
        fifths [narrow narrow narrow pure pure narrow pure pure pure pure pure]]
    (tune root fifths)))

(defn werckmeister-ii
  "Returns a function that converts midi to hertz using Werckmeister's well-temperament
  based on 1/3 comma divisions (Werckmeister II). Ratios are relative to root.
  e.g. ((werckmeister-ii 61) 69)"
  [root] 
  (let [pure 3/2
        narrow (/ pure (java.lang.Math/pow pythagorean-comma 1/3)) 
        wide (* pure (java.lang.Math/pow pythagorean-comma 1/3)) 
        fifths [narrow pure narrow pure narrow pure narrow pure wide wide narrow]]
    (tune root fifths)))

(defn werckmeister-iii 
  "Returns a function that converts midi to hertz using Werckmeister's well-temperament
  based on 1/4 comma divisions (Werckmeister III). Ratios are relative to root.
  e.g. ((werckmeister-iii 61) 69)"
  [root] 
   (let [pure 3/2
         narrow (/ pure (java.lang.Math/pow pythagorean-comma 1/4)) 
         wide (* pure (java.lang.Math/pow pythagorean-comma 1/4)) 
         fifths [pure pure narrow narrow pure pure narrow narrow wide pure pure]] 
     (tune root fifths)))  

(def well
  "Returns a function that converts midi to hertz using Werckmeister I well-temperament.
  See werckmeister-i.
  e.g. ((well 61) 69)" 
  werckmeister-i)
