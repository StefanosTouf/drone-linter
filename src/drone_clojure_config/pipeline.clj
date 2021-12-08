(ns drone-config.pipeline
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.helpers :as h]))


(s/def :pipeline/trigger
  (s/and
    #(not (contains? % :instance))
    :drone-config.common/conditions))


(s/def :pipeline/kind #{"pipeline"})
(s/def :pipeline/type #{"docker"})
(s/def :pipeline/name string?)


(s/def :platform/os #{"windows" "os"})
(s/def :platform/arch string?)
(s/def :platform/version integer?)


(s/def :pipeline/platform
  (s/and
    (h/no-extra-keys-m #{:os :arch :version})
    (s/keys :req-un [:platform/os :platform/arch] :opt-un [:platform/version])
    #(if (= "windows" (% :os)) (boolean (% :version)) true)))


(s/def :pipeline/workspace #(string? (% :path)))

(s/def :clone/depth #(integer? (% :clone)))
(s/def :clone/disable #(boolean? (% :disable)))
(s/def :pipeline/clone (s/or :clone/depth :clone/disable))


(defn all-deps-linked
  "Checks if all dependencies are linked in a graph step execution pipeline"
  [step-seq]
  (let [names-deps-map
        (reduce (fn [acc step]
                  (if (step :depends_on)
                    (merge acc {:deps (concat (acc :deps) (step :depends_on))})
                    (merge acc {:names (conj (acc :names) (step :name))}))) {:names [] :deps []} step-seq)]
    (if (-> (:deps names-deps-map) count (> 0))
      (= (set (:names names-deps-map)) (set (:deps names-deps-map)))
      true)))


(s/def :steps/all-deps-linked all-deps-linked)


(s/def :pipeline/steps
  (s/and
    (s/coll-of :drone-config.step/step)
    :drone-config.common/unique-names
    :steps/all-deps-linked))


(s/def :services/name string?)
(s/def :services/image string?)


(s/def :services/service
  (s/and (h/no-extra-keys-m #{:name :image :environment})
         (s/keys :req-un [:services/name :services/image] :opt-un [:drone-config.common/environment])))


(s/def :pipeline/services (s/coll-of :services/service))


(s/def ::pipeline
  (s/and
    (h/no-extra-keys-m #{:trigger :kind :type :name :workspace :clone :steps :depends_on})
    (s/keys :req-un [:pipeline/kind :pipeline/type :pipeline/name]
            :opt-un [:pipeline/trigger :pipeline/platform :pipeline/workspace
                     :pipeline/clone :pipeline/steps :pipeline/services
                     :drone-config.common/depends_on])))
