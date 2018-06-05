# clj-extjwnl

Clojure wrapper for [extJWNL](https://github.com/extjwnl/extjwnl) (Extended Java WordNet Library) Java API.

Alpha and only implements a small subset of extJWNL.

## Installation

Add dependency to your project:

```clojure
[net.zakak/clj-extjwnl "0.1.0-SNAPSHOT"]
```

## Usage

```clojure
(ns hello-extjwnl.core
  (:require [net.zakak.clj-extjwnl :as extjwnl]))

(def dict (extjwnl/default-dictionary))

(def example-pointers-query
  [:word/lemma
   {:word/pos [:pos/label]}
   {:word/senses [:synset/gloss
                  {:synset/words [:word/lemma
                                  {:word/pos [:pos/label]}]}
                  {:synset/pos [:pos/label]}
                  {:synset/pointers [{:pointer/type [:pointer-type/label]}
                                     {:pointer/synset [:synset/gloss
                                                       {:synset/pos [:pos/label]}
                                                       {:synset/words [:word/lemma
                                                                       {:word/pos [:pos/label]}]}]}]}]}])

(lookup-all-index-words dict "pit" example-pointers-query)
```

## License

Copyright © 2018 Zachary Kriner

Distributed under the Eclipse Public License version 2.0.
