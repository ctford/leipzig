(ns whelmed.instrument
  (:use
    [overtone.live]))

(definst shudder# [freq 440]
  (let [envelope (env-gen (perc 2 1.5) :action FREE)]
    (*
      (* envelope (sin-osc 6))
      (square freq)
      (sin-osc freq))))

(definst sawish# [freq 440]
  (let [envelope (env-gen (perc 0.1 1.5) :action FREE)]
    (*
      envelope
      (saw freq))))
