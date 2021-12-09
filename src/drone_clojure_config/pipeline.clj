(ns drone-config.pipeline
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.helpers :as h]
    [clojure.set :as set-ops]
    [drone-config.common :as c]))


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


(defn all-vols-linked
  [pipeline]
  (let [{steps :steps volumes :volumes} pipeline
        step-vol-names (->> steps
                            (map :volumes) flatten (map :name) (filter #(not (= nil %))))
        pipeline-vol-names (->> volumes
                                (map :name))]
    (= (set step-vol-names) (set pipeline-vol-names))))


;; --trigger
(s/def :pipeline/trigger
  (s/and
    (h/no-extra-keys-m (set-ops/difference c/condition-exact-keys #{:instance}))
    :drone-config.common/condition-keys))


;; --base
(s/def :pipeline/kind #{"pipeline"})
(s/def :pipeline/type #{"docker"})
(s/def :pipeline/name string?)


;; --platform
(s/def :platform/os #{"windows" "os"})
(s/def :platform/arch string?)
(s/def :platform/version integer?)


(s/def :pipeline/platform
  (s/and
    (h/no-extra-keys-m #{:os :arch :version})
    (s/keys :req-un [:platform/os :platform/arch] :opt-un [:platform/version])
    #(if (= "windows" (% :os)) (boolean (% :version)) true)))


;; --workspace
(s/def :pipeline/workspace #(string? (% :path)))


;; --clone
(s/def :clone/depth #(integer? (% :clone)))
(s/def :clone/disable #(boolean? (% :disable)))
(s/def :pipeline/clone (s/or :clone/depth :clone/disable))


;; --steps
(s/def :steps/all-deps-linked all-deps-linked)


(s/def :pipeline/steps
  (s/and
    (s/coll-of :drone-config.step/step)
    :drone-config.common/unique-names
    :steps/all-deps-linked))


;; --services
(s/def :services/name string?)
(s/def :services/image string?)


(s/def :services/service
  (s/and (h/no-extra-keys-m #{:name :image :environment})
         (s/keys :req-un [:services/name :services/image] :opt-un [:drone-config.common/environment])))


(s/def :pipeline/services (s/coll-of :services/service))


;; --node
(s/def :pipeline/node :drone-config.common/key-string-pair)


;; --volumes
(s/def :pipeline-volumes/name string?)


(s/def :pipeline-volumes/temp
  (s/and
    map?
    (s/or :empty empty? :medium-memory #(= (% :medium) "memory"))))


(s/def :pipeline-volumes/host (s/and map? #(string? (% :path))))


(s/def :pipeline-volumes/volume
  (s/and (h/no-extra-keys-m #{:name :host :temp})
         (s/keys :req-un
                 [(and :pipeline-volumes/name
                       (or :pipeline-volumes/temp :pipeline-volumes/host))])))


(s/def :pipeline/volumes (s/coll-of :pipeline-volumes/volume))


;; --overall rules
(s/def :pipeline/all-vols-linked all-vols-linked)


(s/def ::pipeline
  (s/and
    :pipeline/all-vols-linked
    (h/no-extra-keys-m
      #{:trigger :kind :type :name :workspace :clone :steps :depends_on :volumes :node})
    (s/keys :req-un [:pipeline/kind :pipeline/type :pipeline/name]
            :opt-un [:pipeline/trigger :pipeline/platform :pipeline/workspace
                     :pipeline/clone :pipeline/steps :pipeline/services
                     :drone-config.common/depends_on :pipeline/node
                     :pipeline/volumes])))
