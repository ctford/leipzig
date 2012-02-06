(ns overtunes.pitch
  (:use 
    [clojure.set :only [difference]]
    [overtone.live :exclude [scale octave sharp flat sixth]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(def note! sampled-piano)
(def chord! #(map note! %))

(defmacro defall
  "Define multiple values at once."
  [names values]
  `(do ~@(map
           (fn [name value] `(def ~name ~value))
           names
           (eval values))))

(defn update-values 
  "Apply f to all the values of m corresponding to keys in ks."
  [m [& ks] f] (if ks
                 (update-values
                   (update-in m [(first ks)] f)
                   (rest ks)
                   f)
                 m)) 

(defn keys-except
  "Gets the keys from m, excluding any that are present in ks."
  [m ks] (difference (set (keys m)) ks))

; Basic intervals
(def semitone 1)
(def tone (* semitone 2))
(def octave (* 12 semitone))

; Operations on intervals
(def sharp #(+ % semitone))
(def flat #(- % semitone))
(def raise #(+ % octave))
(def lower #(- % octave))

(defn scale 
  "Define a scale as a cumulative sum of intervals."
  ([] '(0))
  ([interval & intervals]
   (cons 0 (map #(+ interval %) (apply scale intervals)))))

(def major-scale (zipmap
                   [:i :ii :iii :iv :v :vi :vii :viii]
                   (scale tone tone semitone tone tone tone semitone)))

(defn grounding [offset]
  "Takes an offset from root and produces a function for rendering chords."
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
(def augmented (update-in major [:v] sharp))
(def diminished (update-in minor [:v] flat))
(def power (select-keys major-scale [:i :v :viii]))

; Transformations 
(def suspended-second #(assoc % :iii (:ii major-scale))) 
(def suspended-fourth #(assoc % :iii (:iv major-scale)))
(def sixth #(assoc % :vi (:vi major-scale)))
(def seventh #(assoc % :vii (+ (:v %) (:iii %))))
(def dominant-seventh #(assoc % :vii (flat (:vii major-scale))))
(def ninth #(assoc (seventh %) :ix (raise (:ii major-scale))))
(def eleventh #(assoc (ninth %) :xi (raise (:iv major-scale))))
(def thirteenth #(assoc (eleventh %) :xi (raise (:vi major-scale))))
(def first-inversion #(update-values % (keys-except % [:i]) lower))
(def second-inversion #(update-values % (keys-except % [:i :iii]) lower))
