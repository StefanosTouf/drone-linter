(ns drone-config.core
  (:require
    [clojure.spec.alpha :as spec]))


(spec/valid? :drone-config.step/step
             {:name "asd"
              :image "asd"
              :commands ["do stuff" "do stuff 2"]
              :when {:branch {:exclude ["asd" "asd"]}
                     :event {:include ["pull_request"] :exclude ["push"]}}})


