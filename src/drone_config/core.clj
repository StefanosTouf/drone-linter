(ns drone-config.core
  (:require
    [clojure.spec.alpha :as spec]
    [drone-config.common]
    [drone-config.helpers]
    [drone-config.pipeline]
    [drone-config.plugins]
    [drone-config.step]))


(defn -main
  [args]
  (println "hello world"))


(spec/explain-data :drone-config.pipeline/pipeline
                   {:kind "pipeline"
                    :type "docker"
                    :name "ablabla"
                    :steps [{:name "asd"
                             :image "asd"
                             :commands ["do stuff" "do stuff 2"]
                             :volumes [{:name "name1"
                                        :path "/s/d"}
                                       {:name "name2"
                                        :path "/asd"}]
                             :when {:instance ["asd"]
                                    :branch {:include ["aa"]}}}
                            {:name "asd1"
                             :image "plugins/docker"
                             :settings {:username {:from_secret "asd"}
                                        :password {:from_secret "asd"}}}
                            {:name "asd2"
                             :image "appleboy/drone-discord"
                             :settings {:username {:from_secret "as"}}}]
                    :volumes [{:name "name1"
                               :host {:path "s/asd/"}}
                              {:name "name2"
                               :temp {}}]})


