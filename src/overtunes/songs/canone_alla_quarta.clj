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
(defn shift [f x] (connect #(+ x %) f))
(def major (scale [2 2 1 2 2 2 1]))
(def g-major (translate major 0 74))

(defn bpm [per-minute] #(-> % (/ per-minute) (* 60) (* 1000)))
(defn syncopate [timing durations] #(->> % (sum-n durations) timing))
(defn run [a b] 
  (if (<= a b)
    (range a (inc b))
    (reverse (run b a))))

(defn melody [notes]
  (let [functionalise #(update-all % [:time :pitch] natural-map)
        accumulate-time (update :time sums)]
    (-> notes accumulate-time functionalise)))

(def leader 
  (let [call
          {:time (concat (repeat 2 1/4) [1/2] (repeat 14 1/4) [3/2])
           :pitch (concat [0] (run -1 3) (run 2 0) [4] (run 1 8))}
        response
          {:time (concat (repeat 10 1/4) [1/2] (repeat 2 1/4) [9/4])
           :pitch (concat (run 7 -1) [0] (run 0 -3))}
        development
          {:time (concat [3/4] (repeat 12 1/4) [1/2 1 1/2] (repeat 12 1/4) [3])
           :pitch (concat [4 4] (run 2 -3) [-1 -2 0] (run 3 5) (repeat 3 1) [2] (run -1 1) [0 -1] (run 5 0))}
        line
          (merge-with concat call response development)]
    (melody line)))

(def bass
  (let [line
          {:time (repeat 24 1) 
           :pitch (mapcat (partial repeat 3) (concat (run 0 -3) (run -5 -3) [0 7]))}
        lower-note #(- % 7)
        lower-melody (update :pitch #(connect lower-note %))]
   (-> bassline melody lower-melody)))

(defn melody# [melody] 
  (let [notes (update-all melody [:pitch :time] natural-seq)
        play-at #(at %1 (piano# %2))]
    (dorun (map play-at (:time notes) (:pitch notes)))))

(defn update [k f] #(update-in % [k] f))
(def mirror (update :pitch #(connect - %))) 
(defn after [beats] (update :time #(shift % beats)))
(defn transpose [interval] (update :pitch #(shift % interval))) 
(def canone-alla-quarta (reduce connect [(after 3) (transpose -3) mirror])) 

(defn sharps [notes] #(if (contains? notes %) (inc %) %))
(defn flats [notes] #(if (contains? notes %) (dec %) %))

(defn play# []
  (let [from-now #(translate % 0 (now))
        beat (from-now (bpm 100))
        with-beat (update :time (partial connect beat))
        in-key (update :pitch (partial connect g-major))
        after-a-half (after 1/2)
        with-sharps (update :pitch #(comp (sharps [12 22]) %))
        with-flats (update :pitch #(comp (flats [37]) %))]
    (-> bass in-key with-beat melody#)
    (-> leader after-a-half canone-alla-quarta in-key with-beat melody#)
    (-> leader after-a-half in-key with-sharps with-flats with-beat melody#)
    ))

(play#)
