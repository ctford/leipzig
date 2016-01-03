(ns leipzig.macros)

(defmacro defs [names docstring values]
  `(do ~@(map
     (fn [name i] `(def ~name ~docstring (nth ~values ~i)))
     names
     (range))))
