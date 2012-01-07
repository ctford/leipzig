(ns overtunes.core
  (:use [overtone.live])
)

(defn record [piece filename] ( do
  (recording-start filename)
  (piece)
  (recording-stop)))
