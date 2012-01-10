(ns overtunes.core
  (:use [overtone.live]))

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
    (let [seconds (/ duration 1000)
          frequency (midi->hz (note tone))]
      (instrument frequency seconds))))

(defn play-melody [melody instrument metro]
  "Plays a seq of notes on instrument.
  Takes a relative metronome in addition to the melody.
  (play-melody [:C3 :E3 :G3] organ-cornet metro)"
  (let [duration (beat-length metro)]
    (when-not (empty? melody)
      (at (metro) (play-note (first melody) instrument duration))
      (play-melody (rest melody) instrument (metronome-from metro 1)))))
