(ns leipzig.test.chord
  (:use midje.sweet leipzig.chord))

(fact
  (-> triad (root 3)) =>
  {:i 3, :iii 5, :v 7})

(fact
  (-> seventh (root 4)) =>
  {:i 4, :iii 6, :v 8, :vii 10})

(fact
  (-> ninth (root 5)) =>
  {:i 5, :iii 7, :v 9, :vii 11, :ix 13})

(fact "The first inversion drops everything except the tonic."
  (-> triad (inversion 1)) =>
  {:i 0, :iii -5, :v -3})

(fact "The second inversion drops everything except the tonic and the third."
  (-> seventh (inversion 2)) =>
  {:i 0, :iii 2, :v -3, :vii -1})

(fact "The third inversion drops everything except the tonic, third and fifth."
  (-> seventh (inversion 3)) =>
  {:i 0, :iii 2, :v 4, :vii -1})
