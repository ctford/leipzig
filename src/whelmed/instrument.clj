(ns whelmed.instrument
  (:use
    [overtone.live]))

(definst shudder# [freq 440]
  (let [envelope (env-gen (perc 0.1 5) :action FREE)]
    (*
      (* envelope (sin-osc 6))
      (square freq)
      (sin-osc freq))))

(definst sawish# [freq 440 depth 3]
  (let [envelope (env-gen (perc 0.05 1.5) :action FREE)]
    (*
      envelope
      (saw (+ freq (* depth (lf-saw:kr 0.1 0.2)))))))
