(ns drone-config.common.general
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.common.predicates :as p]))

(defmacro no-extra-keys-m
  [k-set]
  `(s/coll-of
     (p/belongs ~k-set)))

(s/def :general/environment 
  (s/map-of keyword? string?))

(defn unique-names-checker
  ":name keys must be unique"
  [map-coll]
  (let [name-seq (map #(% :name) map-coll)]
      (= (count name-seq) (count (set name-seq)))))

(s/def :general/unique-names unique-names-checker)

(s/def :general/string string?)
(s/def :general/integer integer?)
(s/def :general/map map?)
(s/def :general/empty empty?)
(s/def :general/boolean boolean?)
(s/def :general/number number?)
(s/def :general/keyword keyword?)
