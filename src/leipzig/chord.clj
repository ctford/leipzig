(ns leipzig.chord)

(def neutral-scale
  (zipmap
    [:i :ii :iii :iv :v :vi :vii :viii :ix :xii :xiii]
    (range)))

(defn- add [chord k] (-> note (assoc k (neutral-scale k))))
(defn diminish [chord k] (-> note (update-in [k] #(- % 1/2))))
(defn augment [chord k] (-> note (update-in [k] #(+ % 1/2))))

(def triad (-> neutral-scale (select-keys [:i :iii :v])))
(def sixth (-> triad (add :vi)))
(def seventh (-> triad (add :vii)))
(def ninth (-> seventh (add :ix)))
(def eleventh (-> ninth (add :xi)))
(def thirteenth (-> eleventh (add :xiii)))
