(defproject whelmed "0.1.0-SNAPSHOT"
  :description "Whelmed. Not overwhelmed. Not underwhelmed. Just whelmed."
  :main whelmed.core
  :run-aliases {:west whelmed.core/play#}
  :dependencies	[
    [org.clojure/clojure "1.3.0"]
    [overtone "0.7.0-SNAPSHOT"]
  ]
)
