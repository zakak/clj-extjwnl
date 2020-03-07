(ns net.zakak.clj-extjwnl-test
  (:require [clojure.test :refer [deftest is]]
            [net.zakak.clj-extjwnl :refer [default-dictionary
                                           lookup
                                           lookup-by-pos]]))

(def dict (default-dictionary))

(def example-pointers-query
  [:index-word/lemma
   {:index-word/pos [:pos/label]}
   {:index-word/senses [:synset/gloss
                        {:synset/words [:word/lemma
                                        {:word/pos [:pos/label]}]}
                        {:synset/pos [:pos/label]}
                        {:synset/pointers [{:pointer/type [:pointer-type/label]}
                                           {:pointer/synset [:synset/gloss
                                                             {:synset/pos [:pos/label]}
                                                             {:synset/words [:word/lemma
                                                                             {:word/pos [:pos/label]}]}]}]}]}])

(def example-senses-query
  [:index-word/lemma
   {:index-word/pos [:pos/label]}
   {:index-word/senses [:synset/gloss
                        {:synset/words [:word/lemma
                                        {:word/pos [:pos/label]}]}
                        {:synset/pos [:pos/label]}]}])

(deftest lookup-by-pos-test
  (is (= {:index-word/lemma  "pit"
          :index-word/pos    {:pos/label "noun"}
          :index-word/senses [{:synset/words [{:word/lemma "pit"}]
                               :synset/pos   {:pos/label "noun"}}]}
         (lookup-by-pos dict
                          '[:index-word/lemma
                            {:index-word/pos [:pos/label]}
                            {(:index-word/senses :limit 1) [{(:synset/words :limit 1) [:word/lemma]}
                                                            {:synset/pos [:pos/label]}]}]
                          "pit" "noun"))))

(deftest lookup-test
  (is (= [{:index-word/lemma  "pit"
           :index-word/pos    {:pos/label "noun"}
           :index-word/senses [{:synset/words [{:word/lemma "pit"}]
                                :synset/pos   {:pos/label "noun"}}]}
          {:index-word/lemma  "pit"
           :index-word/pos    {:pos/label "verb"}
           :index-word/senses [{:synset/words [{:word/lemma "pit"}]
                                :synset/pos   {:pos/label "verb"}}]}]
         (lookup dict
                 '[:index-word/lemma
                   {:index-word/pos [:pos/label]}
                   {(:index-word/senses :limit 1) [{(:synset/words :limit 1) [:word/lemma]}
                                                   {:synset/pos [:pos/label]}] }]
                 "pit"))))


(comment
  (let [pos (-> (lookup dict
                        '[{:index-word/pos [:pos/label
                                            :identity]}]
                        "dog")
                first
                :index-word/pos)]
    {:label (:pos/label pos)
     :id    (.getId (:identity pos))})

  (second (lookup
           dict
           '[{:index-word/pos [:pos/label]}
             {:index-word/senses [:synset/gloss]}]
           "dog")))
