(defproject leipzig "0.11.0-SNAPSHOT"
  :description "A composition library for Clojure and Clojurescript."
  :url "http://github.com/ctford/leipzig"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments "Same as Overtone"}
  :dependencies	[[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [overtone "0.9.1"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :source-paths ["src/clj" "src/cljc"]
  :profiles {:dev
             {:plugins [[lein-midje "3.1.3"]
                        [lein-cljsbuild "1.1.1"]
                        [codox "0.8.8"]]
              :dependencies  [[com.cemerick/piggieback "0.2.1"]
                              [org.clojure/tools.nrepl "0.2.10"]
                              [midje "1.6.3"] ]
              :repl-options  {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  :cljsbuild {:builds {:prod {:source-paths ["src/cljc"]
                              :compiler     {:output-to     "target/cljs/leipzig.js"
                                             :optimizations :whitespace}}}}
  :codox {:src-dir-uri "http://github.com/ctford/leipzig/blob/0.9.0/"})
