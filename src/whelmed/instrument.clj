(ns whelmed.instrument
  (:use
    [overtone.inst.sampled-piano]
    [overtone.live]))

(definst shudder# [freq 440 vibrato 6]
  (let [envelope (env-gen (perc 2 1.5) :action FREE)]
    (*
      (* envelope (sin-osc vibrato))
      (square freq)
      (sin-osc freq))))

(definst sawish# [freq 440]
  (let [envelope (pluck (env-gen (perc 0.2 1.5) :action FREE))]
    (*
      0.7
      envelope
      (+
        (square (* freq 0.99))
        (square freq)))))

(definst sinish# [freq 440]
  (let [envelope (env-gen (perc 0.1 1.1) :action FREE)]
    (*
      envelope
      (sin-osc freq))))

(definst groan# [freq 440 vibrato 8/3]
  (let [envelope (* (sin-osc vibrato) (env-gen (perc 0.1 10) :action FREE))]
    (*
      0.7
      envelope
      (+
        (* (sin-osc 0.5) (+ 0.1 (saw freq)))
        (* (sin-osc 0.8) (+ -0.03 (square freq)))
        (+ -0.04 (sin-osc freq))))))

(def piano# sampled-piano)
