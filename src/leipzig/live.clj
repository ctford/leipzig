(ns leipzig.live
  (:use [leipzig.melody])
  (:require [overtone.live :as overtone]))

(defmulti play-note
  "Plays a note according to its :part.
  e.g. (play-note {:part :bass :time _})"
  :part)

(defn- trickle [notes]
  (if-let [{epoch :time :as note} (first notes)]
    (do
      (Thread/sleep (max 0 (- epoch (+ 100 (overtone/now))))) 
      (cons note 
        (lazy-seq (trickle (rest notes)))))))

(defn play
  "Plays notes now.
  e.g. (->> melody play)"
  [notes] 
  (future
    (->>
      notes
      (after (overtone/now))
      trickle
      (map (fn [{epoch :time :as note}] (->> note play-note (overtone/at epoch))))
      dorun)))

(defn- from [base] (partial + base))

(defn- forever [riff]
  (let [{final :time, duration :duration} (last @riff)]
    (concat
      @riff
      (lazy-seq (->> (forever riff) (where :time (from (+ final duration))))))))

(defn jam*
  "Plays riff repeatedly, freshly dereferencing it each time.
  riff must be a var, not an arbitrary expression.
  e.g. (jam* (var riff))"
  [riff]
  (->> riff forever play))

(defmacro jam 
  "Plays riff-symbol repeatedly, freshly dereferencing its var each time.
  e.g.
    (def riff (phrase [1 2] [3 4])) 
    (jam riff)"
  [riff-symbol] 
  `(jam* (var ~riff-symbol))) 
