(ns overtunes.core
  (:use
    [overtone.live :exclude [scale octave sharp flat sixth unison]]
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
    (let [frequency (midi->hz (note tone))]
      (instrument frequency duration))))

(defn play-chord 
  "Plays a seq of tones as a chord on instrument for duration.
  (play-chord (chord :C4 :major) organ-cornet 1200)"
  [tones instrument duration]
  (if (not (empty? tones))
      (doall (map (fn [tone] (play-note tone instrument duration)) tones))))

(defn play-progression
  "Plays a chord progression on instrument according to metro's timing."
  [progression instrument metro]
  (when-not (empty? (first progression))
    (let [weighted-chord (map first progression)
          chord (first weighted-chord)
          beats (second weighted-chord)
          duration (* beats (beat-length metro))]
      (at (metro) (play-chord chord instrument duration))
      (play-progression (map rest progression) instrument
        (metronome-from metro beats)))))

(defn play-melody
  "Plays a seq of weighted notes on instrument.
  Takes a relative metronome in addition to the melody.
  (play-melody [[:C3 :E3 :G3][1/1 1/2 3/2]] organ-cornet metro)"
  [melody instrument metro]
  (play-progression
    [(map vector (first melody)) (second melody)] instrument metro))

; Let's play!
(def note# sampled-piano)
(def chord# #(map note# %))
