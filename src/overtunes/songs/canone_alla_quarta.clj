(ns overtunes.songs.canone-alla-quarta
  (:use
    [overtone.live :only [at now]]
    [overtone.inst.sampled-piano :only [sampled-piano] :rename {sampled-piano piano#}]))

(defn => [val & fs] (reduce #(apply %2 [%1]) val fs))

(defn update-all [m [& ks] f]
    (if ks
      (-> m
        (update-all (rest ks) f)
        (update-in [(first ks)] f))
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
(defn run [a & bs] 
  (let [up-or-down #(if (<= %1 %2) (range %1 %2) (reverse (range (inc %2) (inc %1))))]
    (if bs
      (concat (up-or-down a (first bs)) (apply run bs))
      [a])))

(defn melody [notes]
  (let [functionalise #(update-all % [:time :pitch] natural-map)
        accumulate-time (update :time sums)]
    (-> notes accumulate-time functionalise)))

(def leader 
  (let [call
          {:time (mapcat repeat [2 1 14 1] [1/4 1/2 1/4 3/2])
           :pitch (concat (run 0 -1 3 0) [4] (run 1 8))}
        response
          {:time (mapcat repeat [10 1 2 1]  [1/4 1/2 1/4 9/4])
           :pitch (concat (run 7 -1 0) (run 0 -3))}
        development
          {:time (mapcat repeat [1 12 1 1 1 12 1] [3/4 1/4 1/2 1 1/2 1/4 3])
           :pitch (concat [4 4] (run 2 -3) [-1 -2 0] (run 3 5) (repeat 3 1) [2] (run -1 1 -1) (run 5 0))}
        line
          (merge-with concat call response development)]
    (melody line)))

(def bass
  (let [line
          {:time (mapcat repeat [21 12] [1 1/4]) 
           :pitch (concat (mapcat (partial repeat 3) (concat (run 0 -3) (run -5 -3))) (run 12 0))}
        lower-note #(- % 7)
        lower-melody (update :pitch #(connect lower-note %))]
   (-> line melody lower-melody)))

(defn melody# [melody] 
  (let [notes (update-all melody [:pitch :time] natural-seq)
        play-at #(at %1 (piano# %2))]
    (dorun (map play-at (:time notes) (:pitch notes)))))

(defn update [k f] #(update-in % [k] f))
(def mirror (update :pitch #(connect - %))) 
(defn after [beats] (update :time #(shift % beats)))
(defn transpose [interval] (update :pitch #(shift % interval))) 
(def canone-alla-quarta (reduce connect [(after 3) (transpose -3) mirror])) 

(defn sharps [notes fpitches] #(if (some (partial = %) notes) (-> % fpitches inc) (fpitches %))) 
(defn flats [notes fpitches] #(if (some (partial = %) notes) (-> % fpitches dec) (fpitches %))) 

(defn play# []
  (let [from-now #(translate % 0 (now))
        with-beat (update :time (partial connect (from-now (bpm 90))))
        in #(update :pitch (partial connect %))
        with-sharps #(update :pitch (partial sharps %))
        with-flats #(update :pitch (partial flats %))]
    (=> bass (in g-major) (with-sharps [8]) with-beat melody#)
    (=> leader (after 1/2) canone-alla-quarta (in g-major) with-beat melody#)
    (=> leader (after 1/2) (in g-major) (with-sharps [22 32]) (with-flats [37]) with-beat melody#)
    ))

(play#)
