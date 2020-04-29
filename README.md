# clj-extjwnl

clj-extjwnl provides an API for querying
[WordNet](https://wordnet.princeton.edu/) using data patterns inspired
by [Datomic pull](https://docs.datomic.com/on-prem/pull.html).

It is a Clojure wrapper for a subset of the
Extended Java WordNet Library
([extJWNL](https://github.com/extjwnl/extjwnl)) that provides
[easy access](#identity) to the underlying library.

[See this post](https://www.zakak.net/blog/2019-using-wordnet-with-clojure/)
if you'd like to use Java interop without a wrapper
library.

## Installation

[deps.edn](https://clojure.org/guides/deps_and_cli) dependency:

```clojure
{net.zakak/clj-extjwnl {:mvn/version "0.1.2-SNAPSHOT"}}
```

[Leiningen](https://github.com/technomancy/leiningen) dependency:

```clojure
[net.zakak/clj-extjwnl "0.1.2-SNAPSHOT"]
```

## Usage

The primary functions are:

* default-dictionary - creates an instance of a WordNet dictionary
* lookup - retrieves word data from the dictionary using a data pattern

Lookup accepts an [edn](https://github.com/edn-format/edn) data
pattern describing the data to be retrieved.

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
;; => [#:index-word{:pos #:pos{:label "verb"}, :senses [#:synset{:gloss "go after with the intent to catch; ,,,"}]} ,,,]
```

## Data Patterns

### IndexWord

An [IndexWord](http://extjwnl.sourceforge.net/javadocs/net/sf/extjwnl/data/IndexWord.html)
represents a line of the pos.index file.

* :index-word/lemma
* {:index-word/pos [:pos/label]}
* {:index-word/senses [ [Synset pattern](#synset) ]}

There can be many senses for an IndexWord. The following returns all of them:

* {:index-word/senses [ [Synset pattern](#synset) ]}

If you'd like to return n senses:

* {(:index-word/senses :limit n) [ [Synset pattern](#synset) ]}

### Word

A [Word](http://extjwnl.sourceforge.net/javadocs/net/sf/extjwnl/data/Word.html)
represents the lexical information related to a specific sense of an IndexWord.

* :word/lemma
* {:word/pos [:pos/label]}

### Synset

A [Synset](http://extjwnl.sourceforge.net/javadocs/net/sf/extjwnl/data/Synset.html),
or synonym set, represents a line of a WordNet pos.data file.

* :synset/gloss
* {:synset/pos [:pos/label]}
* {:synset/pointers [ [Pointer pattern](#pointer) ]}
* {:synset/words [:word/lemma
                  {:word/pos [:pos/label]}]}

You can limit how many pointers and words are returned:

* {(:synset/pointers :limit n) [ [Pointer pattern](#pointer) ]}
* {(:synset/words :limit n) [ [Word pattern](#word) ]}

### Pointer

A [Pointer](http://extjwnl.sourceforge.net/javadocs/net/sf/extjwnl/data/Pointer.html)
encodes a lexical or semantic relationship between WordNet entities.

* {:pointer/type [:pointer-type/label]}
* {:pointer/synset [ [Synset pattern](#synset) ]}

### Identity

Use :identity to return the underlying Java object. Useful to
interop directly for features not covered by data patterns.

```clojure
(let [pos (-> (lookup dict
                      '[{:index-word/pos [:pos/label :identity]}]
                      "dog")
              first
              :index-word/pos)]
  {:label (:pos/label pos)
   :id   (.getId (:identity pos))})
;; => {:label "noun", :id 1}
```
