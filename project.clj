(defproject leipzig "0.2.0-SNAPSHOT"
  :description "A composition library for Overtone."
  :url "http://github.com/ctford/leipzig"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments "Same as Overtone"}
  :dependencies	[
    [org.clojure/clojure "1.4.0"]
    [overtone "0.7.1" :exclusions [seesaw]]
    [midje "1.4.0"]
    [org.clojure/math.numeric-tower "0.0.1"]])
