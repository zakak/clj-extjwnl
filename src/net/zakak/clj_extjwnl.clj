(ns net.zakak.clj-extjwnl
  (:import (net.sf.extjwnl.data
            IndexWord
            Word
            POS
            Pointer
            PointerType
            Synset)
           (net.sf.extjwnl.dictionary Dictionary)))

(def ^:private default-map-element-opts {:limit 1000})

(defn default-dictionary
  []
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

(defmethod -keyword-element :identity
  [_ el]
  el)

(defmethod -map-element :index-word/pos
  [{:keys [index-word/pos]} ^IndexWord el]
  (elements (.getPOS el) pos))

(defmethod -map-element :index-word/senses
  [{:keys [index-word/senses opts]} ^IndexWord el]
  (mapv #(elements % senses)
        (take (:limit opts) (.getSenses el))))

(defmethod -keyword-element :index-word/lemma
  [_ ^IndexWord el]
  (.getLemma el))

(defmethod -keyword-element :pointer-type/label
  [_ ^PointerType el]
  (.getLabel el))

(defmethod -map-element :pointer/synset
  [{:keys [pointer/synset]} ^Pointer el]
  (elements (.getTargetSynset el)
            synset))

(defmethod -map-element :pointer/type
  [{:keys [pointer/type]} ^Pointer el]
  (elements (.getType el)
            type))

(defmethod -keyword-element :pos/label
  [_ ^POS el]
  (.getLabel el))

(defmethod -keyword-element :synset/gloss
  [_ ^Synset el]
  (.getGloss el))

(defmethod -map-element :synset/pointers
  [{:keys [synset/pointers opts]} ^Synset el]
  (mapv #(elements % pointers)
        (take (:limit opts) (.getPointers el))))

(defmethod -map-element :synset/pos
  [{:keys [synset/pos]} ^Synset el]
  (elements (.getPOS el) pos))

(defmethod -map-element :synset/words
  [{:keys [synset/words opts]} ^Synset el]
  (mapv #(elements % words)
        (take (:limit opts) (.getWords el))))

(defmethod -keyword-element :word/lemma
  [_ ^Word el]
  (.getLemma el))

(defmethod -map-element :word/pos
  [{:keys [word/pos]} ^Word el]
  (elements (.getPOS el) pos))

;; ## Main

(defn lookup-all-index-words
  "Use dict to look up lemma using query"
  [^Dictionary dict lemma query]
  (->> (.lookupAllIndexWords dict lemma)
       (.getIndexWordCollection)
       (mapv #(elements % query))))

(defn lookup-index-word
  "Use dict to look up lemma using pos and query"
  [^Dictionary dict pos lemma query]
  (elements (.lookupIndexWord dict
                              (or (POS/getPOSForLabel pos)
                                  (throw (Exception. (str "Unknown part of speech (pos): '" pos "'"))))
                              lemma)
            query))
