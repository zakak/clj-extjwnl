# clj-extjwnl

Clojure wrapper for a subset of the [extJWNL](https://github.com/extjwnl/extjwnl)
(Extended Java WordNet Library) Java API.

This library provides an API to select from WordNet using data patterns
inspired by [Datomic pull](https://docs.datomic.com/on-prem/pull.html).

For direct Java interop usage, see
[the wiki](https://github.com/zakak/clj-extjwnl/wiki/Using-the-Extended-Java-WordNet-Library-from-Clojure).

## Installation

Add dependency to your project:

```clojure
[net.zakak/clj-extjwnl "0.1.0-SNAPSHOT"]
```

## Usage

There are two main functions.

* lookup-index-word - to find only one part of speech for a word
* lookup-all-index-words - to find all parts of speech for a word

For example:

```clojure
(ns hello-extjwnl.core
  (:require [net.zakak.clj-extjwnl :as extjwnl]))

(def dict (extjwnl/default-dictionary))

;; select a map of data about the noun 'pit'
(extjwnl/lookup-index-word dict
                           "noun"
                           "pit"
                           '[:index-word/lemma
                             {:index-word/pos [:pos/label]}
                             {:index-word/senses [{:synset/words [:word/lemma]}
                                            {:synset/pos [:pos/label]}]}])
;; => #:index-word{:lemma "pit", :pos #:pos{:label "noun"}, :senses [...] ...}


;; that was too much, same thing with a limit of 1 on select attributes
(extjwnl/lookup-index-word dict
                           "noun"
                           "pit"
                           '[:index-word/lemma
                             {:index-word/pos [:pos/label]}
                             {(:index-word/senses :limit 1) [{(:synset/words :limit 1) [:word/lemma]}
                                                       {:synset/pos [:pos/label]}]}])
;; => #:index-word{:lemma "pit", :pos #:pos{:label "noun"}, :senses [...] ...}


;; use the same query but a vector containing all parts of speech for 'pit'
(extjwnl/lookup-all-index-words dict
                                "pit"
                                '[:index-word/lemma
                                  {:index-word/pos [:pos/label]}
                                  {(:index-word/senses :limit 1) [{(:synset/words :limit 1) [:word/lemma]}
                                                            {:synset/pos [:pos/label]}]}]) 
;; [#:index-word{:lemma "pit", :pos #:pos{:label "noun"}, :senses [...] ...]
```

## License

Copyright © 2019 Zak Kriner

Distributed under the Eclipse Public License version 2.0.
