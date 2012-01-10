(ns overtunes.songs.tarai
  (:use [overtone.live])
  (:use [clojure.algo.monads :only [state-t cont-m run-cont domonad]]))

;; From https://gist.github.com/1441831, which is an edit of
;; https://gist.github.com/1371488 by @philandstuff.

(def m (metronome 400))

;; basic.clj より
(defsynth foo [freq 200 dur 0.5]
  (let [src (saw [freq (* freq 1.01) (* 0.99 freq)])
        low (sin-osc (/ freq 2))
        filt (lpf src (line:kr (* 10 freq) freq 10))
        env (env-gen (perc 0.1 dur) :action FREE)]
    (out 0 (pan2 (* 0.8 low env filt)))))

(defmacro at-current-beat
  "An action in the beat monad which evaluates a number of exprs on the current beat using the at macro."
  [& exprs]
  `(fn [[m# beat#]]
    (fn [k#]
      (at (m# beat#) ~@exprs)
      (k# [nil [m# beat#]]))))

(defn wait
  "An action in the beat monad which waits for a given number of beats. Schedules the
   next beat using apply-at and updates the state accordingly."
  [num-beats]
  (fn [[m beat]]
    (fn [k]
      (let [next-beat (+ beat num-beats)]
        (apply-at (m next-beat) k [nil [m next-beat]] [])))))

(def beat-m
  "The beat monad; equivalent to (state-t cont-m).

  The state used by the state-t is of the form [metro beat-num], where metro is a value
  returned by the metronome fn from overtone.
  The use of cont-m allows the scheduling of future events using apply-at."
  (state-t cont-m))

(defn run-beat
  "Runs a beat defined by the beat-m, the beat monad.

   Runs the continuation and sets the initial state to [metro beat-num]. If no beat-num
   is supplied, (metro) is used."
  ([beat metro]
     (run-beat beat metro (metro)))
  ([beat metro beat-num]
     (run-cont (beat [metro beat-num]))))

(defn foo-play-b [x y z j]
  (domonad beat-m
           [_ (at-current-beat (foo (* 100 (+ x 3)) 1.0))
            _ (wait 1)
            _ (at-current-beat (foo (* 100 (+ y 3)) 1.0))
            _ (wait 1)
            _ (at-current-beat (foo (* 100 (+ z 3)) 1.0))
            _ (wait 1)
            _ (at-current-beat (foo (* 100 (+ j 3)) 1.0))
            _ (wait 1)]
           nil))

(comment ;use as follows:
  (run-beat (foo-play-b 4 3 2 1) m)
  ;; which is equivalent to the long form:
  (run-cont ((foo-play-b 4 3 2 1) [m (m)]))
  )

(defn tarai-b [x y z j]
  (domonad beat-m
           [_    (foo-play-b x y z j)
            x    (if (<= x y)
                   (m-result y)
                   (domonad
                    [new-x (tarai-b (dec x) y z j)
                     new-y (tarai-b (dec y) z j x)
                     new-z (tarai-b (dec z) j x y)
                     new-j (tarai-b (dec j) x y z)
                     result (tarai-b new-x new-y new-z new-j)]
                    result))]
           x))

(defn play [] (run-beat (tarai-b 8 3 2 3) m))
