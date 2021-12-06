(ns drone-config
  (:require
    [clojure.spec.alpha :as s]))


(set! *warn-on-reflection* true)

(s/def ::name string?)
(s/def ::image string?)
(s/def ::command string?)
(s/def ::commands (s/coll-of ::command))

(s/def ::step (s/keys :req-un [::name ::image ::commands]))

(def step {:name "asd" :image "asd" :commands ["do stuff" "do stuff 2"]})

(s/valid? ::step step)


