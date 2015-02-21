(ns leipzig.melody
  (:require [leipzig.scale :as scale]))

(defn bpm
  "Returns a function that translates a beat number into seconds.
  e.g. ((bpm 90) 5)" 
  [beats] (fn [beat] (-> beat (/ beats) (* 60))))

(defn having
  "Zips an arbitrary quality onto a melody.
  e.g. (->> (rhythm [1 1/2]) (having :drum [:kick :snare]))"
  [k values notes]
  (map #(assoc %1 k %2) notes values))

(defprotocol Utterable (utter [thing time pitch]))

(extend-protocol Utterable
  Object
  (utter [pitch time duration]
    [{:pitch pitch :time time :duration duration}])
  
  clojure.lang.Sequential
  (utter [cluster time duration]
    (mapcat #(utter % time duration) cluster))

  clojure.lang.MapEquivalence
  (utter [chord time duration]
    (utter (-> chord vals sort) time duration))

  nil
  (utter [pitch time duration]
    [{:time time :duration duration}]))

(defn phrase
  "Translates a sequence of durations and pitches into a melody.
  nil pitches signify rests, vectors represent clusters, and maps
  represent chords. Vector durations represent repeated notes.
  e.g. (phrase [1/2 1/2 3/2 3/2] [0 1 nil 4])
       (phrase [1 1 2] [4 3 [0 2]])
       (phrase [1 [1 2]] [4 3])
       (phrase (repeat 4) (map #(-> triad (root %))) [0 3 4 3])" 
  [durations pitches]
  (let [wrap (fn [x] (if (sequential? x) x [x]))
        counts (map (comp count wrap) durations)
        normalised-pitches (mapcat repeat counts pitches)
        normalised-durations (mapcat wrap durations)
        times (reductions + 0 normalised-durations)]
    (mapcat utter normalised-pitches times normalised-durations)))

(defn rhythm
  "Translates a sequence of durations into a rhythm.
  e.g. (rhythm [1 1 2])"
  [durations]
  (phrase durations (repeat nil)))

(def is
  "Synonym for constantly.
  e.g. (->> notes (wherever (comp not :part), :part (is :bass)))"
  constantly)

(defn- if-applicable [applies? f] (fn [x] (if (applies? x) (f x) x)))
(defn wherever
  "Applies f to the k key of each note wherever condition? returns true.
  e.g. (->> notes (wherever (comp not :part), :part (is :piano))"
  [applies? k f notes]
  (map
    (if-applicable applies? #(update-in % [k] f))
    notes))

(defn where
  "Applies f to the k key of each note in notes, ignoring missing keys.
  e.g. (->> notes (where :time (bpm 90)))"
  [k f notes]
  (wherever #(contains? % k), k f notes))

(defn all
  "Sets a constant value for each note of a melody.
  e.g. (->> notes (all :part :drum))"
  [k v notes]
  (wherever (is true), k (is v) notes))

(defn after
  "Delay notes by wait.
  e.g. (->> melody (after 3))"
  [wait notes] (where :time (scale/from wait) notes))

(defn- before? [a b] (<= (:time a) (:time b)))
(defn with
  "Blends melodies.
  e.g. (->> melody (with bass drums))"
  ([[a & other-as :as as] [b & other-bs :as bs]]
   (cond
     (empty? as) bs
     (empty? bs) as
     (before? a b) (cons a (lazy-seq (with other-as bs)))
     :otherwise    (cons b (lazy-seq (with as other-bs)))))
  ([as bs & others] (reduce with (cons as (cons bs others)))))

(defn duration
  "Returns the total duration of notes.
  e.g. (->> melody duration)"
  [notes]
  (reduce (fn [so-far {t :time d :duration}] (max so-far (+ t d))) 0 notes))

(defn then 
  "Sequences later after earlier.
  e.g. (->> call (then response))"
  [later earlier]
  (->> later
       (after (duration earlier))
       (with earlier)))

(defn mapthen [f & melodies]
  "Apply f to each melody, then join them together.
  e.g. (mapthen drop-last [bassline vocals])"
  (->> melodies
       (apply map f)
       (reduce #(then %2 %1))))

(defn times
  "Repeats notes n times.
  e.g. (->> bassline (times 4))"
  [n notes]
  (->> notes
       (repeat n)
       (mapthen identity)))
