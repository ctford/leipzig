Change Log
==========

Leipzig still sees occasional breaking changes, though every effort will be made to
document them here.

New in 0.10.0
------------
* *Addition* - Clojurescript is supported on all namespaces other than `live`.
* *Addition* - `augment` adds to an element of a chord.
* *Bug fix* - The example works again.

New in 0.9.0
------------
* *Addition* - `phrase` accepts vector durations, representing repeated notes.
* *Addition* - `phrase` accepts an optional third argument for velocities.
* *Addition* - `but` replaces part of a melody.
* *Addition* - `tempo` applies a transformation to both time and duration.
* *Addition* - Experimental `accelerando` linearly interpolates between two relatively different rates.
* *Addition* - `play` allows for lead-in notes with negative times. 
* *Breaking change* - `where` ignores missing keys.
* *Bug fix* - `play` no longer throws a `NullPointerException` for finite sequences of notes.

New in 0.8.0
------------
* *Breaking change* - In `phrase`, rests are reified as notes of nil pitch.
* *Breaking change* - `then` and `times` do not allow padding.
* *Breaking change* - `bpm` returns a fn that converts beats to seconds, to match Supercollider. 
* *Addition* - `mapthen` provides a temporal equivalent to mapcat.

New in 0.7.0
------------
* *Breaking change* - The `help` macro has been removed because it wasn't helpful. 
* *Addition* - `stop` kills all running melodies.
* *Addition* - `with` is variadic.

New in 0.6.0
------------
* *Addition* - `nil`s represent rests in `phrase`. Note, ensure you use `rhythm` instead of `(phrase ... (repeat nil))` or `phrase` will never return.
* *Addition* - `Sequential`s (lists, vectors and lazy-seqs) represent clusters in `phrase`.
* *Addition* - Maps represent chords in `phrase`.
* *Addition* - `then` and `times` accept an optional parameter controlling when the subsequent melody starts.

New in 0.5.0
------------
* *Breaking change* - `jam` is not a macro, so it expects a ref as an argument.
* *Breaking change* - `crab` translates the result to avoid playing it before the original.
* *Bug fix* - `crab` reflects melodies according to the end of each note (see issue #2) and sorts the resulting notes by time to ensure order is preserved.
* *Addition* - `having` zips arbitrary atributes onto melodies.
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
