(ns whelmed.core
  (:use
    [overtone.live :only []]
    [whelmed.melody :only [play follow after]]
    [whelmed.songs.west :only [west-with-the-sun]]
    [whelmed.songs.SKA :only [ska]]))

(def tracks
  (sorted-map
    "ska" ska,
    "west" west-with-the-sun))

(defn -main

  ([track] (->>
             (tracks track)
             play))

  ([]      (->>
             tracks
             (map second)
             (reduce #(follow (after 2000 %2) %1))
             play)))
