(ns overtunes.pitch.chords)

(defmacro defall
  "Define multiple values at once."
  [names values]
  `(do ~@(map
           (fn [name value] `(def ~name ~value))
           names
           (eval values))))

(def tone 2)
(def semitone (/ tone 2))
(def octave (* 12 semitone))
(def sharp #(+ semitone %))
(def flat #(- semitone %))

(defn scale 
  ([] '(0))
  ([interval & intervals]
   (cons 0 (map #(+ interval %) (apply scale intervals)))))

(def major-scale (zipmap
                   [:i :ii :iii :iv :v :vi :vii :viii]
                   (scale tone tone semitone tone tone tone semitone)))

(defn grounding [offset]
  (fn
    ([octave-number]
     (+ offset (* octave-number octave)))
    ([octave-number chord]
     (map #(+ offset % (* octave-number octave)) (vals chord)))))

; Name notes
(defall [C D E F G A B]
        (map
          grounding
          (sort (vals  major-scale))))

; Qualities
(def major (select-keys major-scale [:i :iii :v]))
(def minor (update-in major [:iii] flat))
(def power (select-keys major-scale [:i :v :viii]))

; Modifications
(def augmented #(update-in % [:v] sharp))
(def diminished #(update-in % [:v] flat))
(def suspended-second #(update-in % [:iii] 2)) 
(def suspended-fourth #(update-in % [:iii] 5))
(def sixth #(assoc % :vi 9))
(def seventh #(assoc % :vii (+ (:v %) (:iii %))))
(def dominant-seventh #(assoc % :vii 10))
(def ninth #(assoc (seventh %) :ix 14))
(def eleventh #(assoc (ninth %) :xi 17))
(def thirteenth #(assoc (eleventh %) :xi 21))
