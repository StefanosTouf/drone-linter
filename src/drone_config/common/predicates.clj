(ns drone-config.common.predicates)


(defn belongs
  "Unexpected key"
  [key-set]
  (fn [[k _]] (boolean (key-set k))))


(defn is-keyword?
  "Expected a keyword"
  [v]
  (keyword? v))

(defn is-boolean?
  "Expected a boolean"
  [v]
  (boolean? v))

(defn is-string?
  "Expected a string"
  [v]
  (string? v))


(defn is-integer?
  "Expected an integer"
  [v]
  (string? v))


(defn is-number?
  "Expected a number"
  [v]
  (string? v))


(defn is-map?
  "Expected a map"
  [v]
  (map? v))


(defn is-empty?
  "Expected to be empty"
  [v]
  (empty? v))


(defn one-of
  "Unexpected value"
  [s]
  (fn [v] (boolean (s v))))
