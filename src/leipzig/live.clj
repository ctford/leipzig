(ns leipzig.live
  (:require [overtone.live :as overtone]
            [leipzig.melody :as melody]))

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

(def channels (atom []))
(defn- register [channel]
  (swap! channels #(conj % channel))
  channel)
(defn stop
  "Kills all running melodies.
  e.g. (->> melody play)
  
       ; Later
       (stop)"
  []
  (doseq [channel @channels] (future-cancel channel))
  (overtone/stop)
  (reset! channels []))

(defn play
  "Plays notes now.
  e.g. (->> melody play)"
  [notes] 
  (->>
    notes
    (melody/after (overtone/now))
    trickle
    (map (fn [{epoch :time :as note}] (->> note play-note (overtone/at epoch))))
    dorun
    future
    register))

(defn- forever
  "Lazily loop riff forever. riff must start with a positive :time, otherwise there
  will be a glitch as a new copy of riff is sequenced."
  [riff]
  (let [once-through @riff]
    (concat
      once-through
      (lazy-seq (->> riff
                     forever
                     (melody/after (melody/duration once-through)))))))

(defn jam
  "Plays riff repeatedly, freshly dereferencing it each time
  (riff must be a ref). To terminate the looping, set riff
  to nil.
  e.g. (jam (var melody))

       ; Later...
       (def melody nil)"
  [riff]
  (->> riff forever play))
