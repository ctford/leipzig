(defproject leipzig "0.4.0-SNAPSHOT"
  :description "A composition library for Overtone."
  :url "http://github.com/ctford/leipzig"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments "Same as Overtone"}
  :dependencies	[
    [org.clojure/clojure "1.4.0"]
    [overtone "0.8.0"]
    [org.clojure/math.numeric-tower "0.0.1"]]
  :profiles {:dev
             {:plugins [[lein-midje "3.0.0"]]
              :dependencies [[midje "1.5.1"]]}})
