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

(defn- forever
  "Lazily loop riff forever. riff must start with a positive :time, otherwise there
  will be a glitch as a new copy of riff is sequenced."
  [riff]
  (let [once-through @riff]
    (concat
      once-through
      (lazy-seq (->> riff
                     forever
                     (after (duration once-through)))))))

(defn jam
  "Plays riff repeatedly, freshly dereferencing it each time
  (riff must be a ref). To terminate the looping, set riff
  to nil.
  e.g. (jam (var melody))

       ; Later...
       (def melody nil)"
  [riff]
  (->> riff forever play))

(defmacro help
  "Print the docs for all public vars in namespace.
  Will require namespace if it is not already loaded."
  [namespace]
  (require namespace)
  `(do
     ~@(map
         (fn [name]
           (let [sym (symbol (str namespace "/" name))]
             `(clojure.repl/doc ~sym)))
         (-> namespace ns-publics keys sort))))
