(ns drone-config.pipeline.specs
  (:require
    [clojure.set :as set-ops]
    [clojure.spec.alpha :as s]
    [drone-config.common.conditions :as c]
    [drone-config.common.general :as g]
    [drone-config.common.predicates :as p]
    [drone-config.pipeline.predicates :as pp]))


(def trigger-exact-keys (set-ops/difference c/condition-exact-keys #{:instance}))


;; --general
(s/def :pipeline/has-path pp/has-path?)
(s/def :pipeline/has-depth pp/has-depth?)
(s/def :pipeline/has-disable pp/has-disable?)
(s/def :pipeline/all-vols-linked pp/all-vols-linked)


;; --trigger
(s/def :pipeline/trigger
  (s/and
    (g/no-extra-keys-m trigger-exact-keys)
    :conditions/condition-keys))


;; --base
(s/def :pipeline/kind (p/one-of #{"pipeline"}))
(s/def :pipeline/type (p/one-of  #{"docker"}))
(s/def :pipeline/name :general/string)


;; --platform
(s/def :platform/os (p/one-of  #{"windows" "linux"}))
(s/def :platform/arch :general/string)
(s/def :platform/version :general/number)

(s/def :platform/windows pp/windows-platform-validator)


(s/def :pipeline/platform
  (s/and
    (g/no-extra-keys-m #{:os :arch :version})
    (s/keys :req-un [:platform/os :platform/arch] :opt-un [:platform/version])
    :platform/windows))


;; --workspace
(s/def :pipeline/workspace :pipeline/has-path)


;; --clone
(s/def :clone/depth :pipeline/has-depth)
(s/def :clone/disable :pipeline/has-disable)
(s/def :pipeline/clone (s/or :clone/depth :clone/disable))


;; --steps
(s/def :steps/all-deps-linked pp/all-deps-linked)
(s/def :pipeline/all-deps-linked pp/all-deps-linked)


(s/def :pipeline/steps
  (s/and
    :general/unique-names
    :steps/all-deps-linked
    (s/coll-of :steps/step)))


;; --services
(s/def :services/name :general/string)
(s/def :services/image :general/string)


(s/def :services/service
  (s/and (g/no-extra-keys-m #{:name :image :environment})
         (s/keys :req-un [:services/name :services/image] :opt-un [:general/environment])))


(s/def :pipeline/services (s/coll-of :services/service))


;; --node
(s/def :pipeline/node (s/map-of :general/keyword :general/string))


;; --volumes
(s/def :pipeline-volumes/name :general/string)


(s/def :pipeline-volumes/temp
  (s/and
    :general/map
    (s/or :empty :general/empty :medium-memory pp/medium-memory)))


(s/def :pipeline-volumes/host (s/and :general/map :pipeline/has-path))


(s/def :pipeline-volumes/volume
  (s/and (g/no-extra-keys-m #{:name :host :temp})
         (s/keys :req-un
                 [(and :pipeline-volumes/name
                       (or :pipeline-volumes/temp :pipeline-volumes/host))])))


(s/def :pipeline/volumes (s/coll-of :pipeline-volumes/volume))


;; --general
(s/def :pipeline/depends_on
  (s/coll-of :general/string))


(s/def :pipelines/pipeline
  (s/and
    :pipeline/all-vols-linked
    :pipeline/all-deps-linked
    (g/no-extra-keys-m
      #{:trigger :platform :kind :type :name :workspace :clone :steps :depends_on :volumes :node})
    (s/keys :req-un [:pipeline/kind :pipeline/type :pipeline/name]
            :opt-un [:pipeline/trigger :pipeline/platform :pipeline/workspace
                     :pipeline/clone :pipeline/steps :pipeline/services
                     :pipeline/depends_on :pipeline/node
                     :pipeline/volumes])))


(s/def :main/config
  (s/coll-of :pipelines/pipeline))
