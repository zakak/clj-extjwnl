(defproject net.zakak/clj-extjwnl "0.1.1-SNAPSHOT"
  :description "Clojure wrapper for extJWNL (Extended Java WordNet Library) Java API."
  :url "https://github.com/zakak/clj-extjwnl"
  :license {:name "Eclipse Public License - v 2.0"
            :url "https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt"}
  :global-vars {*warn-on-reflection* true}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [net.sf.extjwnl/extjwnl "2.0.2"]
                 [net.sf.extjwnl/extjwnl-data-wn31 "1.2"]])
