(ns leipzig.melody)

(defn bpm
  "Returns a function that translates a beat number into milliseconds.
  e.g. ((bpm 90) 5)" 
  [beats] (fn [beat] (-> beat (/ beats) (* 60) (* 1000))))

(defn- sum-n [series n] (reduce + (take n series)))
(defn- rhythm 
  [durations]
  (let [timings (map (partial sum-n durations) (range))]
    (map #(zipmap [:time :duration] [%1 %2]) timings durations)))

(defn phrase
  "Translates a sequence of durations and pitches into a melody.
  e.g. (phrase [1 1 2] [7 6 4])" 
  [durations pitches]
  (map #(assoc %1 :pitch %2) (rhythm durations) pitches))

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

(defn- from [base] (partial + base))

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

(defn then 
  "Sequences second after first.
  e.g. (->> call (then response))"
  [second first]
    (let [{time :time duration :duration} (last first)
          shifted (after (+ duration time) second)]
      (with first shifted)))

(defn times
  "Repeats notes n times.
  e.g. (->> bassline (times 4))"
  [n notes] (reduce then (repeat n notes)))
