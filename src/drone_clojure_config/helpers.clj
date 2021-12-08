(ns drone-config.helpers
  (:require [clojure.spec.alpha :as s]))

(s/def ::single-elem-map #(= (count %) 1))


(defmacro incl-excl
  [spec]
  `(s/or :array
         (s/and #(or (vector? %) (seq? %)) ~spec)
         :incl-excl
         (s/or
           :one-of (s/and
                     (s/map-of
                       (s/or
                         :include #(= :include %)
                         :exclude #(= :exclude %))
                       ~spec)
                     ::single-elem-map)
           :both (s/and
                   #(s/valid? ~spec (:include %))
                   #(s/valid? ~spec (:exclude %))))))


(defn belongs
  [key-set]
  (fn [[k _]] (boolean (key-set k))))


(defmacro no-extra-keys-m
  [k-set]
  `(s/coll-of
     (belongs ~k-set)))



