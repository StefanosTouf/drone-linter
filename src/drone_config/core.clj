(ns drone-config.core
  (:gen-class)
  (:require
    [clojure.data.json :as json]
    [clojure.spec.alpha :as spec]
    [clojure.string :as str-ops]
    [drone-config.common.conditions]
    [drone-config.common.general]
    [drone-config.error-messages :as error]
    [drone-config.pipeline.specs]
    [drone-config.steps.plugin-specs]
    [drone-config.steps.specs]))

(set! *warn-on-reflection* true)

(defn docstring
  [f-name]
  (:doc (meta (resolve f-name))))


(defn stack-tracer
  [via]
  (str-ops/join
    "\n "
    (->>
      (map #(error/error-messages %) via)
      reverse
      (filter #(not (nil? %))))))


(defn problem-printer
  [problem]
  (let [value (:val problem)
        in (:in problem)
        via (:via problem)]
    (str "Errors found! \n Value: " value
         " In: " in " \nErrors:\n "
         (stack-tracer via))))


(defn problems-printer
  [expl-data]
  (let [c-via #(count (:via %))
        cmpr #(compare (c-via %2) (c-via %1))
        problems (sort cmpr (:clojure.spec.alpha/problems expl-data))]
    (map problem-printer problems)))


(defn linter-out
  ([expl-data]
   (str-ops/join "\n"
                 (problems-printer expl-data)))
  ([take-num expl-data]
   (str-ops/join "\n"
                 (take take-num
                       (problems-printer expl-data)))))


(defn preprocessor
  [pipelines]
  (if (vector? pipelines)
    pipelines
    (vector pipelines)))


(defn -main
  [& args]
  (println
    (linter-out
      (spec/explain-data
        :main/config
        (preprocessor
          (json/read-json
            (str-ops/join args)))))))
