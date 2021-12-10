(ns drone-config.pipeline.predicates
  (:require [drone-config.common.predicates :as p]))


(defn all-deps-linked
  "All graph step dependencies need to be linked"
  [step-seq]
  (let [names-deps-map
        (reduce (fn [acc step]
                  (if (step :depends_on)
                    (merge acc {:deps (concat (acc :deps) (step :depends_on))})
                    (merge acc {:names (conj (acc :names) (step :name))}))) {:names [] :deps []} step-seq)]
    (if (-> (:deps names-deps-map) count (> 0))
      (= (set (:names names-deps-map)) (set (:deps names-deps-map)))
      true)))


(defn all-vols-linked
  "All volumes need to be linked"
  [pipeline]
  (let [{steps :steps volumes :volumes} pipeline
        step-vol-names (->> steps
                            (map :volumes) flatten (map :name) (filter #(not (= nil %))))
        pipeline-vol-names (->> volumes
                                (map :name))]
    (= (set step-vol-names) (set pipeline-vol-names))))


(defn windows-platform-validator
  "Windows platform requires version"
  [platform-map]
  (if (= "windows" (platform-map :os))
    (boolean (platform-map :version))
    true))


(defn has-path?
  "Expected a :path key with string value"
  [v]
  (p/is-string? (v :path)))


(defn has-depth?
  "Expected a :depth key with integer value"
  [v]
  (p/is-integer? (v :depth)))


(defn has-disable?
  "Expected a :disable key with boolean value"
  [workspace]
  (p/is-boolean? (workspace :disable)))


(defn medium-memory
  "Expected :medium \"memory\""
  [temp]
  (= (temp :medium) "memory"))
