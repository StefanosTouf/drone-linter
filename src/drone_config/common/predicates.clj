(ns drone-config.common.predicates)

(set! *warn-on-reflection* true)

(defn belongs
  "Unexpected key"
  [key-set]
  (fn [[k _]] (boolean (key-set k))))

(defn one-of
  "Unexpected value"
  [s]
  (fn [v] (boolean (s v))))
