(ns leipzig.macros)

(defmacro defs [names docstring values]
  `(let [[~@names] ~values]
     ~@(for [name names]
       `(def ~name ~docstring ~name))))
