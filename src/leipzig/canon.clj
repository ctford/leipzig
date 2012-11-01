(ns leipzig.canon
  (:use [leipzig.melody]))

(defn canon
  "Accompany notes with a melody created by apply f to notes.
  e.g. (->> leader (canon (simple 4)))"
  [f notes] (with notes (f notes)))

(defn- from [base] (partial + base))

(defn simple
  "Returns a transformation of delaying a melody by wait."
  [wait] (partial where :time (from wait)))

(defn interval
  "Returns a transformation of raising a melody by interval."
  [interval] (partial where :pitch (from interval)))

(def mirror
  "Returns a transformation of reflecting a melody by pitch."
  (fn [notes] (->> notes (where :pitch -))))

(def crab
  "Returns a transformation of reflecting a melody by time."
  (fn [notes] (->> notes (where :time -))))

(def table
  "Returns a transformation reflecting a melody by time and pitch."
  (comp mirror crab))
