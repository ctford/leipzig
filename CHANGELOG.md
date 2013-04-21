Change Log
==========

Leipzig still sees occasional breaking changes, though every effort will be made to
document them here.

New in 0.4.0
------------
* *Breaking change* - `play` and `play-note` moved to the new `leipzig.live` namespace.
* *Breaking change* - `high` and `low` now operate on midi, not degrees.
* *Bug fix* - Fractional degrees are linearly interpolated by `scale`.
* *Addition* - Alternatives to equal temperament provided in `leipzig.temperament`.
* *Addition* - `inversion` transforms chords.
* *Addition* - `raise` and `lower` are like high and low, but for degrees.
* *Addition* - `from` is public in `leipzig.scale`.
* *Addition* - `jam` macro added to `leipzig.live` allow redef-aware looping.
* *Addition* - `wherever` is like `where`, but selectively transforms notes.

New in 0.3.0
------------
* *Breaking change* - `play` returns a future instead of blocking.
* *Bug fix* - `then` uses `with` instead of `concat` to ensure order is preserved.

New in 0.2.0
------------
* *Bug fix* - `phrase` is lazy rather than strictly evaluating its inputs.
* *Addition* - Leipzig is available under the MIT license.

New in 0.1.0
------------
* *Addition* - Melodies are modelled as sequences of maps, ordered by :time.
* *Addition* - `play` lazily sends notes to SuperCollider as they are required.
* *Addition* - The `play-note` multimethod arranges notes to instrument on the :part key.
* *Addition* - Scales are modelled as functions that translate degrees to midi.
* *Addition* - Canons are modelled as functions that transform one melody into another. 
* *Addition* - Chords are modelled as maps.
