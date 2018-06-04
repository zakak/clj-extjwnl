(ns net.zakak.clj-extjwnl
  (:import (net.sf.extjwnl.data POS)
           net.sf.extjwnl.dictionary.Dictionary)
  (:require [clojure.string :as str]))

(def ^:private default-map-element-opts {:limit 1000})

(defn default-dictionary []
  (Dictionary/getDefaultResourceInstance))

;; ## Elements

(defmulti ^:private -keyword-element (fn [k _] k))
(defmulti ^:private -map-element (fn [m _] (:key m)))

(defn- keyword-element
  "k is a keyword, e.g. :word/lemma"
  [k el]
  [k (apply -keyword-element [k el])])

(defn- map-element-with-keyword-key
  "m looks like {:word/lemma ...}"
  [m el]
  (let [k (ffirst m)]
    [k
     (-map-element {:key  k
                    :opts default-map-element-opts
                    k     (get m k)}
                   el)]))

(defn- map-element-with-list-key
  "m looks like '{(:word/lemma :limit 1) ...}"
  [m el]
  (let [coll (ffirst m)
        k    (first coll)
        opts (merge default-map-element-opts (apply hash-map (rest coll)))]
    [(get opts :as k) 
     (-map-element {:key  k 
                    :opts opts
                    k     (get m coll)}
                   el)]))

(defn- element
  "Evaluates element based on key type"
  [k el]
  (cond
    (keyword? k)             (keyword-element k el)
    (and (map? k)
         (list? (ffirst k))) (map-element-with-list-key k el)
    (map? k)                 (map-element-with-keyword-key k el)))

(defn- elements
  "Create a map of elements"
  [el ks]
  (into {}
        (mapv #(element % el)
              ks)))

;; ## Element types

(defn- pos-element [pos el]
  (elements (.getPOS el) pos))

(defmethod -keyword-element :identity [_ el]
  el)

(defmethod -keyword-element :pointer-type/label [_ el]
  (.getLabel el))

(defmethod -map-element :pointer/synset [{:keys [pointer/synset]} el]
  (elements (.getTargetSynset el)
            synset))

(defmethod -map-element :pointer/type [{:keys [pointer/type]} el]
  (elements (.getType el)
            type))

(defmethod -keyword-element :pos/label [_ el]
  (.getLabel el))

(defmethod -keyword-element :synset/gloss [_ el]
  (.getGloss el))

(defmethod -map-element :synset/pointers [{:keys [synset/pointers opts]} el]
  (mapv #(elements % pointers)
        (take (:limit opts) (.getPointers el))))

(defmethod -map-element :synset/pos [{:keys [synset/pos]} el]
  (pos-element pos el))

(defmethod -map-element :synset/words [{:keys [synset/words opts]} el]
  (mapv #(elements % words)
        (take (:limit opts) (.getWords el))))

(defmethod -keyword-element :word/lemma [_ el]
  (.getLemma el))

(defmethod -map-element :word/pos [{:keys [word/pos]} el]
  (pos-element pos el))

(defmethod -map-element :word/senses [{:keys [word/senses opts]} el]
  (mapv #(elements % senses)
        (take (:limit opts) (.getSenses el))))

;; ## Main

(defn lookup-all-index-words
  "Use dict to look up lemma using query"
  [dict lemma query]
  (->> (.lookupAllIndexWords dict lemma)
       (.getIndexWordCollection)
       (mapv #(elements % query))))

(defn lookup-index-word
  "Use dict to look up lemma using pos and query"
  [dict pos lemma query]
  (elements (.lookupIndexWord dict
                              (or (POS/getPOSForLabel pos)
                                  (throw (Exception. (str "Unknown part of speech (pos): '" pos "'"))))
                              lemma)
            query))
