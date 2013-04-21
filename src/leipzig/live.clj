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

(defn- forever [riff]
  (let [{final :time, duration :duration} (last @riff)]
    (concat
      @riff
      (lazy-seq (->> riff forever (after (+ final duration)))))))

(defn jam*
  "Plays riff repeatedly, freshly dereferencing it each time.
  riff must be a var, not an arbitrary expression.
  To terminate the looping, set riff to nil.
  e.g. (jam* (var melody))

       ; Later...
       (def melody nil)"
  [riff]
  (->> riff forever play))

(defmacro jam 
  "Plays riff-symbol repeatedly, freshly dereferencing its var each time.
  To terminate the looping, set riff-symbol's var to nil.
  e.g. (def melody (phrase [1 2] [3 4])) 
       (jam melody)

       ; Later...
       (def melody nil)"
  [riff-symbol] 
  `(jam* (var ~riff-symbol))) 
