(ns overtunes.songs.variatio-12
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano}]))

(defn sum-n [series n] (reduce + (take n series)))
(defn scale [intervals]
  #(if (neg? %)
     (let [downward-scale (comp - (scale (reverse intervals)))]
       (-> % - downward-scale))
     (sum-n (cycle intervals) %)))

(defn translate [f x y] #(-> % (+ x) f (+ y)))
(def major (scale [2 2 1 2 2 2 1]))
(defn modulate [scale mode] (translate scale mode (- (scale mode))))
(def g-major (translate major 0 74))
(def scales (repeat g-major))

(defn bpm [per-minute] #(-> % (/ per-minute) (* 60) (* 1000)))
(defn syncopate [timing durations] #(->> % (sum-n durations) timing))
(defn run [a b] 
  (if (<= a b)
    (range a (inc b))
    (reverse (run b a))))

(def durations (flatten [(repeat 2 1/4) 1/2 (repeat 6 1/4) (repeat 8 1/4) 3/2 (repeat 10 1/4) 1/2 (repeat 2 1/4) 9/4 3/4 (repeat 12 1/4) 1/2 1 1/2 (repeat 12 1/4) 1])) 
(def pitches (flatten [0 (run -1 3) (run 2 0) 4 (run 1 8) (run 7 -1) 0 (run 0 -3) 4 4 (run 2 -3) -1 1 (run 4 6) 1 1 1 2 -1 -2 0 1 -1 -2 (run 5 0)]))
(def bass (map #(- % 7) (flatten (map #(repeat 3 %) (concat (run 0 -3) (run -5 -3) [-7])))))

(defn melody# [timing notes] 
  (let [note# #(at (timing %1) (piano ((nth scales %1) %2)))]
    (dorun (map-indexed note# notes)))) 

(defn play# []
  (let [timing (-> (bpm 90) (translate 0 (now)))
        rhythm-from #(syncopate (translate timing % 0) durations)
        leader pitches
        follower (->> leader (map -) (map #(- % 4)))]
    (melody# timing bass)
    (melody# (rhythm-from 1/2) leader)
    (melody# (rhythm-from 7/2) follower)
    (melody# (syncopate (translate timing 23 0) [1 1/4 1/4 1/4 1/4 1]) [-7 -7 -5 -3 -1 0])
    ))

(play#)
