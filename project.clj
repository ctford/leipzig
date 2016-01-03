(defproject leipzig "0.10.0-SNAPSHOT"
  :description "A composition library for Overtone."
  :url "http://github.com/ctford/leipzig"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments "Same as Overtone"}
  :dependencies	[
    [org.clojure/clojure "1.7.0"]
    [overtone "0.9.1"]
    [org.clojure/math.numeric-tower "0.0.4"]]
  :profiles {:dev
             {:plugins [[lein-midje "3.1.3"]
                        [codox "0.8.8"]]
              :dependencies [[midje "1.6.3"]]}}
  :codox {:src-dir-uri "http://github.com/ctford/leipzig/blob/0.9.0/"})
