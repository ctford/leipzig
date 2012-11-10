(ns leipzig.chord)

(defn- mapval [m f]
  ((fn update-all [m [k & ks] f]
     (if k
       (-> m (update-in [k] f) (update-all ks f))
       m))
     m (keys m) f))

(defn root
  "Translates a chord so that its root is at tonic.
  e.g. (-> triad (root 4))" 
  [chord tonic] (-> chord (mapval #(+ % tonic)))) 

(def triad
  "A three-tone chord."
  {:i 0, :iii 2, :v 4})

(def seventh 
  "A four-tone chord."
  (-> triad (assoc :vii 6)))

(def ninth 
  "A five-tone chord."
  (-> seventh (assoc :ix 8)))
