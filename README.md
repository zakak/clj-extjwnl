# clj-extjwnl

clj-extjwnl provides an API for querying
[WordNet](https://wordnet.princeton.edu/) using data patterns inspired
by [Datomic pull](https://docs.datomic.com/on-prem/pull.html).

It is a Clojure wrapper for the
Extended Java WordNet Library ([extJWNL](https://github.com/extjwnl/extjwnl)).

For direct Java interop usage, see
[the wiki](https://github.com/zakak/clj-extjwnl/wiki/Using-the-Extended-Java-WordNet-Library-from-Clojure).

## Installation

[deps.edn](https://clojure.org/guides/deps_and_cli) dependency:

```clojure
{net.zakak/clj-extjwnl {:mvn/version "0.1.1-SNAPSHOT"}}
```

[Leiningen](https://github.com/technomancy/leiningen) dependency:

```clojure
[net.zakak/clj-extjwnl "0.1.1-SNAPSHOT"]
```

## Usage

The primary functions are:

* default-dictionary - creates an instance of a WordNet dictionary
* lookup - retrieves word data from the dictionary using a pattern

Lookup accepts an [edn](https://github.com/edn-format/edn) pattern
describing the data to be retrieved.

An example:

```clojure
(ns hello-extjwnl.core
  (:require [net.zakak.clj-extjwnl :as extjwnl]))

;; Load the default dictionary.
(def dict (extjwnl/default-dictionary))

;; Describe what we want to know about the word.
(def part-of-speech-pattern '[{:index-word/pos [:pos/label]}])

;; Lookup data about 'dog' from the dictionary.
(extjwnl/lookup dict part-of-speech-pattern "dog")
;; => [#:index-word{:pos #:pos{:label "noun"}} #:index-word{:pos #:pos{:label "verb"}}]

;; Add glossary data to a pattern.
(def glossary-pattern '[{:index-word/pos [:pos/label]}
                        {:word/senses [:synset/gloss]}])

;; Lookup using the new pattern. 
(extjwnl/lookup dict glossary-pattern "dog")
;; [#:index-word{:pos #:pos{:label "verb"}, :senses [#:synset{:gloss "go after with the intent to catch; ..."}]} ...]
```

## License

Copyright © 2020 Zak Kriner

Distributed under the Eclipse Public License version 2.0.
