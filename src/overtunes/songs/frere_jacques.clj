(ns overtunes.songs.frere-jacques
  (:use [overtone.live])
  (:use [overtunes.core])
  (:use [overtunes.instruments.foo]))

(def Frère-Jacques [:C4 :D4 :E4 :C4])
(def Dormez-vous? [:E4 :F4 :G4 :rest])
(def Sonnez-les-matines! [:G4 :G4 :E4 :C4])
(def Din-dan-don [:C4 :G3 :C4 :rest])

(def melody (concat
  Frère-Jacques Frère-Jacques
  Dormez-vous? Dormez-vous?
  Sonnez-les-matines! Sonnez-les-matines!
  Din-dan-don Din-dan-don))

(defn frere-jacques []
  (let [metro (metronome 120)] 
    (play-melody melody foo metro)
    (play-melody melody foo (metronome-from metro 8))
    (play-melody melody foo (metronome-from metro 16))
    (play-melody melody foo (metronome-from metro 24))))
