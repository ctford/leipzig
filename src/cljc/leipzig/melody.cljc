(ns leipzig.melody
  (:require [leipzig.scale :as scale]))

(defn bpm
  "Returns a function that translates a beat number into seconds.
  e.g. ((bpm 90) 5)" 
  [beats] (fn [beat] (-> beat (/ beats) (* 60))))

(defn having
  "Zips an arbitrary quality onto a melody.
  e.g. (->> (rhythm [1 1/2]) (having :drum [:kick :snare]))"
  [k values notes]
  (map #(assoc %1 k %2) notes values))

(defn utter [object time duration velocity]
  (cond
    (number? object)     [{:pitch object :time time :duration duration :velocity velocity}]
    (sequential? object) (mapcat #(utter % time duration velocity) object)
    (map? object)        (utter (-> object vals sort) time duration velocity)
    (nil? object)        [{:time time :duration duration :rest? true}]))

(defn phrase
  "Translates a sequence of durations and pitches into a melody.
  nil pitches signify rests, vectors represent clusters, and maps
  represent chords. Vector durations represent repeated notes.
  e.g. (phrase [1/2 1/2 3/2 3/2] [0 1 nil 4])
  (phrase [1 1 2] [4 3 [0 2]])
  (phrase [1 [1 2]] [4 3])
  (phrase (repeat 4) (map #(-> triad (root %))) [0 3 4 3])"
  ([durations pitches velocities]
   (let [wrap (fn [x] (if (sequential? x) x [x]))
         counts (map (comp count wrap) durations)
         normalised-pitches (mapcat repeat counts pitches)
         normalised-durations (mapcat wrap durations)
         times (reductions + 0 normalised-durations)]
     (mapcat utter normalised-pitches times normalised-durations velocities)))
  ([durations pitches]
   (->> (phrase durations pitches (repeat nil))
        (map #(dissoc % :velocity)))))

(defn rhythm
  "Translates a sequence of durations into a rhythm.
  e.g. (rhythm [1 1 2])"
  [durations]
  (->> (phrase durations (repeat 0)) ; If we use (repeat nil) that means rest.
       (map #(dissoc % :pitch))))

(def is
  "Synonym for constantly.
  e.g. (->> notes (wherever (comp not :part), :part (is :bass)))"
  constantly)

(defn wherever
  "Applies f to the k key of each note wherever applies? returns true.
  e.g. (->> notes (wherever (comp not :part), :part (is :piano))"
  [applies? k f notes]
  (letfn [(do-if [applies? f] (fn [x] (if (applies? x) (f x) x)))]
    (map
      (do-if applies? #(update-in % [k] f))
      notes)))

(defn where
  "Applies f to the k key of each note in notes, ignoring missing keys.
  e.g. (->> notes (where :pitch inc))"
  [k f notes]
  (wherever #(contains? % k), k f notes))

(defn all
  "Sets a constant value for each note of a melody.
  e.g. (->> notes (all :part :drum))"
  [k v notes]
  (wherever (is true), k (is v) notes))

(defn after
  "Delay notes by wait.
  e.g. (->> melody (after 3))"
  [wait notes] (where :time (scale/from wait) notes))

(defn with
  "Blends melodies.
  e.g. (->> melody (with bass drums))"
  ([& melodies]
   (letfn [(before? [{a :time} {b :time}] (<= a b))
           (with* [[a & other-as :as as] [b & other-bs :as bs]]
             (cond
               (empty? as) bs
               (empty? bs) as
               (before? a b) (cons a (lazy-seq (with* other-as bs)))
               :otherwise    (cons b (lazy-seq (with* as other-bs)))))]
     (reduce with* [] melodies))))

(defn but
  "Replaces part of a melody with another.
  e.g. (->> notes (but 2 4 variation))"
  [start end variation notes]
  (let [starts-in? (fn [{:keys [time]}]
                     (and (<= start time) (< time end)))
        clip (fn [{:keys [time duration] :as note}]
               (if (and (< time start) (<= start (+ time duration)))
                 (assoc note :duration (- start time))
                 note))]
    (->> notes
         (filter (complement starts-in?))
         (map clip)
         (with (->> variation (after start))) )))

(defn duration
  "Returns the total duration of notes.
  e.g. (->> melody duration)"
  [notes]
  (let [length (fn [{:keys [time duration]}] (+ time duration))]
    (->> notes
         (map length)
         (reduce max 0))))

(defn then 
  "Sequences later after earlier.
  e.g. (->> call (then response))"
  [later earlier]
  (->> later
       (after (duration earlier))
       (with earlier)))

(defn mapthen [f & melodies]
  "Apply f to each melody, then join them together.
  e.g. (mapthen drop-last [bassline vocals])"
  (->> melodies
       (apply map f)
       (reduce #(then %2 %1))))

(defn times
  "Repeats notes n times.
  e.g. (->> bassline (times 4))"
  [n notes]
  (->> notes
       (repeat n)
       (mapthen identity)))

(defn tempo
  "Transform both :time and :duration according to timing.
  e.g. (->> notes (tempo (bpm 120)))"
  [timing notes]
  (letfn [(update-duration [{t :time d :duration :as note}]
            (assoc note :duration (- (timing (+ t d)) (timing t))))]
    (->> notes
      (map update-duration)
      (where :time timing))))

(defn accelerando
  "Linearly interpolated change between from and to.
  e.g. (->> notes (tempo (accelerando 0 4 3/2))))"
  [from to by]
  (fn rate [t]
    (cond
      (>= from t) t
      (>= to t) (let [duration (- to from)
                      position (- t from)
                      completion (/ position duration)
                      extent (- by 1)
                      base t
                      extra (* position #? (:clj 1/2 :cljs 0.5) completion extent)]
                  (+ base extra))
      :otherwise (+ (rate to) (* by (- t to))))))
