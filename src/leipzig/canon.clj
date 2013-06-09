(ns leipzig.canon
  (:use [leipzig.melody]))

(defn canon
  "Accompanies notes with a melody created by applying f to notes.
  e.g. (->> leader (canon (simple 4)))"
  [f notes] (->> (f notes) (sort-by :time) (with notes)))

(defn- from [base] (partial + base))

(defn simple
  "Returns a transformation that delays a melody by wait."
  [wait] (partial where :time (from wait)))

(defn interval
  "Returns a transformation that raises a melody by interval."
  [interval] (partial where :pitch (from interval)))

(def mirror
  "A transformation that reflects a melody over pitch."
  (fn [notes] (->> notes (where :pitch -))))

(def crab
  "A transformation that reflects a melody over time."
  (fn [notes] (map
                (fn [{start :time length :duration :as note}]
                  (assoc note :time (- (+ start length))))
                notes)))

(def table
  "A transformation that reflects a melody over time and pitch."
  (comp mirror crab))
