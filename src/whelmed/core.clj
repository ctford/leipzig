(ns whelmed.core
  (:use
    [overtone.live :only [recording-start recording-stop]]
    [whelmed.melody :only [play then after]]
    [whelmed.songs.west :only [west-with-the-sun]]
    [whelmed.songs.SKA :only [ska]]))

(def tracks
  (sorted-map
    "ska" ska
    "west" west-with-the-sun))

(defn -main

  ([trackname filename]
   (recording-start filename)
   (-main trackname)
   (->>
     trackname
     tracks
     last
     ((fn [{:keys [time duration]}] (+ time duration)))
     Thread/sleep)
   (recording-stop))

  ([trackname]
    (->>
      trackname
      tracks
      play))

  ([]
    (->>
      tracks
      (map second)
      (reduce #(then (after 2000 %2) %1))
       play)))
