(ns overtunes.pitch.chords
  (:use [overtone.inst.sampled-piano :only [sampled-piano]]
        [overtone.live :only []]))

(def note! sampled-piano)
(def chord! #(map note! %))

(defmacro defall
  "Define multiple values at once."
  [names values]
  `(do ~@(map
           (fn [name value] `(def ~name ~value))
           names
           (eval values))))

(def semitone 1)
(def tone (* semitone 2))
(def octave1 (* 12 semitone))

(def sharp1 #(+ % semitone))
(def flat1 #(- % semitone))
(def raise #(+ % octave1))

(defn scale1 
  ([] '(0))
  ([interval & intervals]
   (cons 0 (map #(+ interval %) (apply scale1 intervals)))))

(def major-scale (zipmap
                   [:i :ii :iii :iv :v :vi :vii :viii]
                   (scale1 tone tone semitone tone tone tone semitone)))

(defn grounding [offset]
  "Takes an offset from root and produces a function for rendering chords."
  (fn
    ([octave-number]
     (+ offset (* octave-number octave1)))
    ([octave-number chord]
     (map #(+ offset % (* octave-number octave1)) (vals chord)))))

; Name notes
(defall [C D E F G A B]
        (map
          grounding
          (sort (vals  major-scale))))

; Qualities
(def major (select-keys major-scale [:i :iii :v]))
(def minor (update-in major [:iii] flat1))
(def power (select-keys major-scale [:i :v :viii]))

; Modifications
(def augmented #(update-in % [:v] sharp1))
(def diminished #(update-in % [:v] flat1))
(def suspended-second #(assoc % :iii (:ii major-scale))) 
(def suspended-fourth #(assoc % :iii (:iv major-scale)))
(def sixth1 #(assoc % :vi (:vi major-scale)))
(def seventh #(assoc % :vii (+ (:v %) (:iii %))))
(def dominant-seventh #(assoc % :vii (flat1 (:vii major-scale))))
(def ninth #(assoc (seventh %) :ix (raise (:ii major-scale))))
(def eleventh #(assoc (ninth %) :xi (raise (:iv major-scale))))
(def thirteenth #(assoc (eleventh %) :xi (raise (:vi major-scale))))
