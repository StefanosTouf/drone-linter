(ns drone-config.core
  ;;
  (:require
    [clojure.spec.alpha :as spec]))


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
                             :when {:instance ["asd"]}}
                            {:name "asd1"
                             :image "asd"
                             :commands ["do stuff" "do stuff 2"]
                             :when {:instance ["asd"]}}
                            {:name "asd2"
                             :image "asd"
                             :commands ["do stuff" "do stuff 2"]
                             :volumes [{:name "name2" 
                                        :path "/asd"}]
                             :when {:instance ["asd"]}
                             :depends_on ["asd" "asd1"]}]
                    :volumes [{:name "name1"
                               :host {:path "s/asd/"}}
                              {:name "name2"
                               :temp {}}]})


