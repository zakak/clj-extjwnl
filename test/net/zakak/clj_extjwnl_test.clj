(ns net.zakak.clj-extjwnl-test
  (:require [clojure.test :refer [deftest is testing]]
            [net.zakak.clj-extjwnl :refer :all]))

(def dict (default-dictionary))

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

(def example-senses-query
  [:word/lemma
   {:word/pos [:pos/label]}
   {:word/senses [:synset/gloss
                  {:synset/words [:word/lemma
                                  {:word/pos [:pos/label]}]}
                  {:synset/pos [:pos/label]}]}])

(deftest lookup-index-word-test
  (is (= {:word/lemma  "pit"
          :word/pos    {:pos/label "noun"}
          :word/senses [{:synset/words [{:word/lemma "pit"}]
                         :synset/pos   {:pos/label "noun"}}]}
         (lookup-index-word dict "noun" "pit"
                            '[:word/lemma
                              {:word/pos [:pos/label]}
                              {(:word/senses :limit 1) [{(:synset/words :limit 1) [:word/lemma]}
                                                        {:synset/pos [:pos/label]}] }]))))

(deftest lookup-all-index-words-test
  (is (= [{:word/lemma  "pit"
           :word/pos    {:pos/label "noun"}
           :word/senses [{:synset/words [{:word/lemma "pit"}]
                          :synset/pos   {:pos/label "noun"}}]}
          {:word/lemma  "pit"
           :word/pos    {:pos/label "verb"}
           :word/senses [{:synset/words [{:word/lemma "pit"}]
                          :synset/pos   {:pos/label "verb"}}]}]
         (lookup-all-index-words dict "pit"
                                 '[:word/lemma
                                   {:word/pos [:pos/label]}
                                   {(:word/senses :limit 1) [{(:synset/words :limit 1) [:word/lemma]}
                                                             {:synset/pos [:pos/label]}] }]))))
