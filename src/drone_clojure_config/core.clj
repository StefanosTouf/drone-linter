(ns drone-config.core
  (:require
    [clojure.spec.alpha :as spec]))


(spec/explain :drone-config.step/step
              {:name "asd"
               :image "asd"
               :commands ["do stuff" "do stuff 2"]
               :when {:branch {:exclude ["asd" "asd"]}
                      :event  {:include ["pull_request"] :exclude ["push"]}
                      :repo   ["octocat/hello-world"]
                      :ref    {:include ["asd/asd/asd"]  :exclude ["asd/asd/asd"]}
                      :instance ["asd"]}})


