(ns drone-config.common.general
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.common.predicates :as p]))

(defmacro no-extra-keys-m
  [k-set]
  `(s/coll-of
     (p/belongs ~k-set)))

(s/def :general/environment 
  (s/map-of p/is-keyword? p/is-string?))

(defn unique-names-checker
  ":name keys must be unique"
  [map-coll]
  (let [name-seq (map #(% :name) map-coll)]
      (= (count name-seq) (count (set name-seq)))))

(s/def :general/unique-names unique-names-checker)
