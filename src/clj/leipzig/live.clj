(ns leipzig.live
  (:require [overtone.live :as overtone]
            [leipzig.melody :as melody]))

(defmulti play-note
  "Plays a note according to its :part.
  e.g. (play-note {:part :bass :time _})"
  :part)

(defn- trickle [[note & others]]
  (when-let [{epoch :time} note]
    (Thread/sleep (max 0 (- epoch (+ 100 (overtone/now)))))
    (cons note (lazy-seq (trickle others)))))

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

(defn- translate [notes]
  (->> notes
       (melody/after (-> notes first :time -)) ; Allow for notes that lead in.
       (melody/after 0.1) ; Make sure we have time to realise the seq.
       (melody/where :time (partial * 1000))
       (melody/after (overtone/now))))

(def play-fn
  "A function that plays a note via overtone/at"
  (fn [epoch note]
    (overtone/at epoch (play-note note))))

(defn play
  "Plays notes now.
  e.g. (->> melody play)"
  ([notes]
   (play play-fn notes))
  ([play-fn notes]
   (->>
     notes
     translate
     trickle
     (remove :rest?)
     (map (fn [{epoch :time :as note}]
            (->> (dissoc note :time)
                 (play-fn epoch)
                 (when (< (overtone/now) epoch))))) ; Don't play notes in the past.
     dorun
     future
     register)))

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
  ([riff]
   (jam play-fn))
  ([riff play-fn]
   (->> riff forever
        (play play-fn))))
