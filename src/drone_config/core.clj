(ns drone-config.core
  (:require
    [clojure.spec.alpha :as spec]))


(defn -main
  [args]
  (println "hello world"))


(defn docstring
  [f-name]
  (:doc (meta (resolve f-name))))


(spec/explain-data :pipelines/pipeline
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
                             :settings {:username "secrett!!"}}]
                    :volumes [{:name "name1"
                               :host {:path "s/asd/"}}
                              {:name "name2"
                               :temp {}}]})


