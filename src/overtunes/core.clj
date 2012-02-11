(ns overtunes.core
  (:use
    [overtone.live :exclude [scale octave sharp flat sixth unison play]]
    [overtone.inst.sampled-piano :only [sampled-piano]]))

(defn beat-length
  "Determines the beat length in milliseconds of metro."
  [metro] (- (metro 1) (metro 0))) 

(defn metronome-from
  "Returns a metronome that measures beats relative to start."
  ([metro start] (fn
    ([] (metro (+ 0 start)))
    ([beat] (metro (+ beat start))))))

(defn play-note
  "Plays a single tone on instrument for duration, assuming an instrument that
  takes a frequency in Hz and a duration in seconds.
  (play-note :C4 organ-cornet 1200)

  Also accepts (and does nothing with) :rest.
  (play-note :rest organ-cornet 1200)"

  [tone instrument duration]

  (if-not (= :rest tone)
    (let [frequency (midi->hz tone)]
      (instrument frequency duration))))

(defn play-chord 
  "Plays a seq of tones as a chord on instrument for duration.
  (play-chord (chord :C4 :major) organ-cornet 1200)"
  [tones instrument duration]
  (if (not (empty? tones))
      (doall (map (fn [tone] (play-note tone instrument duration)) (vals tones)))))

; Let's play!
(def note# sampled-piano)
(defn chord# [chord] (doseq [note chord] (note# note)))

(defn play-phrase
  "Plays a phrase using f according to metro's timing."
  [phrase f metro]
  (when-not (empty? (first phrase))
    (let [[[sound & sounds] [timing & timings]] phrase]
      (at (metro) (f sound))
      (play-phrase [sounds timings] f (metronome-from metro timing)))))

(defn play-progression
  "Plays a chord progression on instrument according to metro's timing."
  [progression instrument metro]
  (play-phrase progression #(chord# (vals %)) metro))

(defn play-melody
  "Plays a melody on instrument according to metro's timing."
  [melody instrument metro]
  (play-phrase melody #(note# %) metro))
