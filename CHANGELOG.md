Change Log
==========

Leipzig still sees occasional breaking changes, though every effort will be made to
document them here.

New in 0.5.0
------------
* *Bug fix* - `crab` reflects melodies according to the end of each note (see issue #2).
* *Bug fix* - `canon` sorts the transformed notes by time to ensure order is preserved.
* *Addition* - `just` translates from midi to frequency using just intonation.
* *Addition* - `rhythm` builds sequences of notes without pitch.
* *Addition* - Codox documentation.

New in 0.4.0
------------
* *Breaking change* - `play` and `play-note` are in the new `leipzig.live` namespace.
* *Breaking change* - `high` and `low` operate on midi, not degrees.
* *Bug fix* - Fractional degrees are linearly interpolated by `scale`.
* *Addition* - `inversion` transforms chords.
* *Addition* - `raise` and `lower` are like high and low, but for degrees.
* *Addition* - `from` is public in `leipzig.scale`.
* *Addition* - `wherever` is like `where`, but selectively transforms notes.
* *Experimental* - Alternatives to equal temperament are provided in `leipzig.temperament`.
* *Experimental* - The `jam` macro in `leipzig.live` affords redef-aware looping.

New in 0.3.0
------------
* *Breaking change* - `play` returns a future instead of blocking.
* *Bug fix* - `then` uses `with` instead of `concat` to ensure order is preserved.

New in 0.2.0
------------
* *Bug fix* - `phrase` is lazy both on its inputs and output.
* *Addition* - Leipzig is available under the MIT license.

New in 0.1.0
------------
* *Addition* - Melodies are modelled as sequences of maps, ordered by :time.
* *Addition* - `play` lazily sends notes to SuperCollider as they are required.
* *Addition* - The `play-note` multimethod arranges notes to instrument on the :part key.
* *Addition* - Scales are modelled as functions that translate degrees to midi.
* *Addition* - Canons are modelled as functions that transform one melody into another. 
* *Addition* - Chords are modelled as maps.
