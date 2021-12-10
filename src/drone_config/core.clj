(ns drone-config.core
  (:require
    [clojure.spec.alpha :as spec]
    [clojure.string :as str-ops]
    [drone-config.error-messages :as error]))


(defn -main
  [args]
  (println "hello world"))


(defn docstring
  [f-name]
  (:doc (meta (resolve f-name))))


(defn stack-tracer
  [via]
  (str-ops/join
    "\n "
    (filter #(not (nil? %)) (reverse (map #(error/error-messages %) via)))))


(defn problem-printer
  [problem]
  (let [value (:val problem)
        in (:in problem)
        via (:via problem)]
    (str "Errors found! \n Value: " value
         " In: " in " \nErrors:\n "
         (stack-tracer via))))


(defn linter-out
  [expl-data]
  (str-ops/join "\n"
                (reverse
                  (map problem-printer (:clojure.spec.alpha/problems expl-data)))))


(defn preprocessor
  [pipelines]
  (if (vector? pipelines)
    pipelines
    (vector pipelines)))


(println
  (linter-out
    (spec/explain-data :main/config
                       (preprocessor
                         {:kind "pipeline"
                          :type "docker"
                          :platform {:os "windows" :arch "as" :version 123}
                          :name "ablabla"
                          :steps [{:name "asd"
                                   :image "asd"
                                   :commands ["do stuff" "do stuff 2"]
                                   :volumes [{:name "name1"
                                              :path "/s/d"}
                                             {:name "name2"
                                              :path "/asd"}]
                                   :when {:instance ["asd"]
                                          :branch {:include "aa"}}}
                                  {:name "asd1"
                                   :image "plugins/docker"
                                   :settings {:username {:from_secret "asd"}
                                              :password {:from_secret "asd"}}}
                                  {:name "asd2"
                                   :image "appleboy/drone-discord"}]
                          :volumes [{:name "name1"
                                     :host {:path "s/asd/"}}
                                    {:name "name2"
                                     :temp {}}]}))))


