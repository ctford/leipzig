(ns overtunes.songs.frere-jacques
  (:use [overtone.live])
  (:use [overtunes.core])
  (:use [overtunes.instruments.foo]))

;; Frère Jacques, frère Jacques,
;; Dormez-vous? Dormez-vous?
;; Sonnez les matines! Sonnez les matines!
;; Din, dan, don. Din, dan, don.

(def frere-jacques [:C4 :D4 :E4 :C4])
(def dormez-vous [:E4 :F4 :G4 :rest])
(def sonnez-les-matines [:G4 :G4 :E4 :C4])
(def din-dan-don [:C4 :G3 :C4 :rest])

(def melody (concat
  frere-jacques frere-jacques
  dormez-vous dormez-vous
  sonnez-les-matines sonnez-les-matines
  din-dan-don din-dan-don
))

(defn frere-jacques-round []
  (let [metro (metronome 120)] 
    (play-melody melody foo metro)
    (play-melody melody foo (metronome-from metro 8))
    (play-melody melody foo (metronome-from metro 16))
    (play-melody melody foo (metronome-from metro 24))))
