(ns overtunes.pitch
  (:use 
    [clojure.set :only [difference]]
    [overtone.live :exclude [scale octave sharp flat sixth unison]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

; Basic intervals
(def unison 0)
(def semitone 1)
(def tone (* semitone 2))
(def octave (* 12 semitone))

; Plumbing
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

(defn grounding [offset]
  "Takes an offset and produces a function for producing concrete sounds."
  (fn
    ([octave-number]
     (+ offset (* octave-number octave)))
    ([octave-number chord & transformations]
     (let [octave-offset (* octave-number octave)
          transform (apply comp (reverse transformations))
          transformed-chord (transform chord)]
       (map #(+ offset octave-offset %) (vals transformed-chord))))))

; Define a major scale. We could easily define other modes,
; but they aren't needed at present.
(defn scale 
  "Define a scale as a cumulative sum of intervals."
  ([] [])
  ([interval & intervals]
   (cons interval (map #(+ interval %) (apply scale intervals)))))

(def major-scale (zipmap
                   [:i :ii :iii :iv :v :vi :vii :viii]
                   (scale unison tone tone semitone tone tone tone semitone)))

; Name notes
(defall [C D E F G A B]
        (map
          grounding
          (sort (vals major-scale))))

; Operations on intervals
(def sharp #(+ % semitone))
(def flat #(- % semitone))
(def raise #(+ % octave))
(def lower #(- % octave))

; Transformations on chords
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

; Paramatised transformations on chords
(defn flattened [key] #(update-in % [key] flat))
(defn sharpened [key] #(update-in % [key] sharp))
(defn raised [key] #(update-in % [key] raise))
(defn lowered [key] #(update-in % [key] lower))
(defn add [key] #(assoc % key (key major-scale))) 
(defn omit [key] #(dissoc % key)) 
(defn bass [key] (comp (lowered :bass) #(assoc % :bass (key major-scale)))) 
(def bassed (bass :i))

; Qualities
(def major (select-keys major-scale [:i :iii :v]))
(def minor ((flattened :iii) major)) 
(def augmented ((sharpened :v) major)) 
(def diminished ((flattened :v) minor)) 
(def power (select-keys major-scale [:i :v :viii]))

; Let's play!
(def note# sampled-piano)
(def chord# #(map note# %))

; (chord# (C 5 minor ninth bassed (omit :iii)))
; (chord# (G 5 major seventh second-inversion bassed (omit :v)))
