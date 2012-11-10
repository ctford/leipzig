(ns leipzig.chord)

(defn- mapval [m f]
  ((fn update-all [m [k & ks] f]
     (if k
       (-> m (update-in [k] f) (update-all ks f))
       m))
     m (keys m) f))

(defn root
  "Translates a chord so that it's root is at tonic.
  e.g. (-> triad (root 4))" 
  [chord tonic] (-> chord (mapval #(+ % tonic)))) 

(defn triad
  "Returns a triad from the specified root.
  e.g. (triad 0)" 
  [root] {:i root, :iii (+ root 2), :v (+ root 4)})

(defn seventh
  "Returns a seventh chord from the specified root.
  e.g. (seventh 4)" 
  [root] (-> root triad (assoc :vii (+ root 6))))

(defn ninth
  "Returns a ninth chord from the specified root.
  e.g. (ninth 5)" 
  [root] (-> root seventh (assoc :ix (+ root 8))))
