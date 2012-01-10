(ns overtunes.instruments.foo
  (:use [overtone.live]))

(defsynth foo
  "Simple, pure tone at freq for dur milliseconds.
  (foo 440 1200)"
  [freq 200 dur 500]
  (let [src (saw [freq (* freq 1.01) (* 0.99 freq)])
        low (sin-osc (/ freq 2))
        filt (lpf src (line:kr (* 10 freq) freq 10))
        env (env-gen (perc 0.1 (/ dur 1000)) :action FREE)]
    (out 0 (pan2 (* 0.8 low env filt)))))
