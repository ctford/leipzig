(ns leipzig.melody
  (:use [leipzig.scale]))

(defn bpm
  "Returns a function that translates a beat number into milliseconds.
  e.g. ((bpm 90) 5)" 
  [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(defn rhythm 
  "Translates a sequence of durations into a rhythm.
  e.g. (rhythm [1 1 2])"
  [durations]
  (let [times (reductions + 0 durations)]
    (map #(zipmap [:time :duration] [%1 %2]) times durations)))

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
  (utter [_ _ _] []))

(defn phrase
  "Translates a sequence of durations and pitches into a melody.
  nil pitches signify rests, vectors represent cluster, and maps
  represent chords.
  e.g. (phrase [1/2 1/2 3/2 3/2] [0 1 nil 4])
       (phrase [1 1 2] [4 3 [0 2]])
       (phrase (repeat 4) (map #(-> triad (root %))) [0 3 4 3])" 
  [durations pitches]
  (let [times (reductions + 0 durations)]
    (mapcat utter pitches times durations)))

(def is
  "Synonym for constantly.
  e.g. (->> notes (where :part (is :bass)))" 
  constantly)

(defn- if-applicable [condition? f] (fn [x] (if (condition? x) (f x) x)))
(defn wherever
  "Applies f to the k key of each note wherever condition? returns true.
  e.g. (->> notes (wherever (comp not :part), :part (is :piano))"
  [condition? k f notes]
  (map
    (if-applicable condition? #(update-in % [k] f))
    notes))

(defn where
  "Applies f to the k key of each note in notes.
  e.g. (->> notes (where :time (bpm 90)))"
  [k f notes]
  (wherever (is true), k f notes))

(defn after
  "Delay notes by wait.
  e.g. (->> melody (after 3))"
  [wait notes] (where :time (from wait) notes))

(defn- before? [a b] (<= (:time a) (:time b)))
(defn with
  "Accompanies two melodies with each other.
  e.g. (->> melody (with bass))"
  [[a & other-as :as as] [b & other-bs :as bs]]
  (cond
    (empty? as) bs
    (empty? bs) as
    (before? a b) (cons a (lazy-seq (with other-as bs)))
    :otherwise    (cons b (lazy-seq (with as other-bs)))))

(defn duration [notes]
  (let [{t :time d :duration} (last notes)]
    (+ t d)))

(defn then 
  "Sequences later after earlier, starting from limit if it
  is supplied. 
  e.g. (->> call (then response))"
  ([limit later earlier]
   (->> earlier
        (with (after limit later)))) 
  ([later earlier]
   (then (duration earlier) later earlier)))

(defn times
  "Repeats notes n times. If limit is supplied, it is used
  as the starting time of the next iteration.
  e.g. (->> bassline (times 4))
       (->> bassline (times 4 8))"
  ([n notes]
   (times n (duration notes) notes)) 
  ([n limit notes]
   (->> (repeat n notes)
        (reduce (partial then limit))))) 
