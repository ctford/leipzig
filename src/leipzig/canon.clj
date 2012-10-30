(ns leipzig.canon
  (:use [leipzig.melody]))

(defn canon [f notes] (concat notes (f notes)))

(defn- from [base] (partial + base))

(defn simple [wait] (partial where :time (from wait)))
(defn interval [interval] (partial where :pitch (from interval)))
(def mirror (fn [notes] (->> notes (where :pitch -))))
(def crab (fn [notes] (->> notes (where :time -))))
(def table (comp mirror crab))
