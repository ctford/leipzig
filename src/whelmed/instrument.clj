(ns whelmed.instrument
  (:use
    [overtone.live]))

(definst shudder# [freq 440 shudder 6]
  (let [envelope (env-gen (perc 2 1.5) :action FREE)]
    (*
      (* envelope (sin-osc shudder))
      (square freq)
      (sin-osc freq))))

(definst sawish# [freq 440]
  (let [envelope (pluck (env-gen (perc 0.1 1.5) :action FREE))]
    (*
      envelope
      (saw freq))))
