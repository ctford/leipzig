(ns whelmed.instrument
  (:use
    [overtone.live]))

(definst recorder# [freq 440]
  (let [envelope (env-gen (perc 0.1 5) :action FREE)]
    (*
      envelope
      (+ 0.7 (* 0.2 (dust)))
      (sin-osc freq))))

(definst sawish# [freq 440 depth 10]
  (let [envelope (env-gen (perc 0.1 0.9) :action FREE)]
    (*
      envelope
      (sin-osc freq)
      (saw (+ freq (* depth (lf-saw:kr 0.1 0.2)))))))

(definst harps# [freq 440]
  (let [duration 1]
    (*
      (line:kr 1 1 duration FREE)
      (pluck (* (white-noise) (env-gen (perc 0.001 5) :action FREE)) 1 1 (/ 1 freq) (* duration 2) 0.25))))
