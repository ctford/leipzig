[Leipzig](https://github.com/ctford/leipzig)
=========
A composition library for [Overtone](https://github.com/overtone/overtone) by [@ctford](https://github.com/ctford).

Examples
--------
See [Row, row, row your boat](leipzig/blob/master/src/leipzig/example/row_row_row_your_boat.clj) or [whelmed](https://github.com/ctford/whelmed).

Using it
--------
Include it as a dependency in your project.clj:

    [leipzig "0.2.0"]

Design
------

Leipzig models music as a sequence of notes, each of which is a map:

    {:time 2000,
     :pitch 67,
     :duration 1000}

You can create a melody with the phrase function. Here's the first few notes of 'Row, row, row your boat':

    (phrase [3/3 3/3 2/3 1/3 3/3]
            [  0   0   0   1   2])

To play a melody, define a default arrangement, put the melody into a particular key and time and then pass it to play:

    (defmethod play-note :default [{midi :pitch}] (sampled-piano midi))

    (->> melody
      (where :time (bpm 90))
      (where :duration (bpm 90))
      (where :pitch (comp C major))
      play)
