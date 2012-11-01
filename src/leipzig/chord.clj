(ns leipzig.chord)

(defn triad [root] {:i root, :iii (+ root 2), :v (+ root 4)})
(defn seventh [root] (-> root triad (assoc :vii (+ root 6))))
(defn ninth [root] (-> root seventh (assoc :ix (+ root 8))))
