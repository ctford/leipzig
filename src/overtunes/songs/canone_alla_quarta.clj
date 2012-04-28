(ns overtunes.songs.canone-alla-quarta
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano#}]))

(defn update-all [m [& ks] f]
    (if ks
          (update-in
                  (update-all m (rest ks) f)
                  [(first ks)]
                  f)
          m))

(defn natural-map [ys] (zipmap (-> ys count range) ys))
(defn natural-seq [f]
    (if-let [y (f 0)]
          (cons y (natural-seq (comp f inc)))
          '()))

(defn connect [f1 f2] #(if-let [y2 (f2 %)] (f1 y2) nil))

(defn sum-n [series n] (reduce + (take n series)))
(defn sums [series] (cons 0 (reductions + series)))
(defn scale [intervals]
  #(if (neg? %)
     (let [downward-scale (connect - (scale (reverse intervals)))]
       (-> % - downward-scale))
     (sum-n (cycle intervals) %)))

(defn translate [f x y] #(-> % (+ x) f (+ y)))
(def major (scale [2 2 1 2 2 2 1]))
(def g-major (translate major 0 74))

(defn bpm [per-minute] #(-> % (/ per-minute) (* 60) (* 1000)))
(defn syncopate [timing durations] #(->> % (sum-n durations) timing))
(defn run [a b] 
  (if (<= a b)
    (range a (inc b))
    (reverse (run b a))))

(def call {:time (concat (repeat 2 1/4) [1/2] (repeat 14 1/4) [3/2])
           :pitch (concat [0] (run -1 3) (run 2 0) [4] (run 1 8))})

(def response {:time (concat (repeat 10 1/4) [1/2] (repeat 3 1/4))
               :pitch (concat (run 7 -1) [0] (run 0 -3))})

(defn subsequent [melody1 melody2]
  (merge-with concat melody1 melody2))

(def melody
  (let [functionalise #(update-all % [:time :pitch] natural-map)
        ground-time #(update-in % [:time] sums)]
    (functionalise (ground-time (subsequent call response)))))

(defn melody# [melody] 
  (let [notes (update-all melody [:pitch :time] natural-seq)
        play-at #(at %1 (piano# %2))]
    (dorun (map play-at (:time notes) (:pitch notes)))))

(defn update [k f] #(update-in % [k] f))
(def mirror (update :pitch #(connect % -))) 
(defn echo [beats] (update :time #(translate % 0 beats)))
(defn transpose [interval] (update :pitch #(translate % interval 0))) 
(def canone-alla-quarta (reduce connect [(echo 3) (transpose -3) mirror])) 

(defn play# []
  (let [from-now #(translate % 0 (now))
        beat (from-now (bpm 120))
        with-beat #(update-in % [:time] (partial connect beat))
        in-key #(update-in % [:pitch] (partial connect g-major))
        leader (-> melody with-beat in-key)]
  ;(-> leader canone-alla-quarta melody#)))
  (-> leader melody#)))

(play#)
