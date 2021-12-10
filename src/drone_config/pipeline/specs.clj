(ns drone-config.pipeline.specs
  (:require
    [clojure.set :as set-ops]
    [clojure.spec.alpha :as s]
    [drone-config.common.conditions :as c]
    [drone-config.common.general :as g]
    [drone-config.common.predicates :as p]
    [drone-config.pipeline.predicates :as pp]))


;; --trigger
(s/def :pipeline/trigger
  (s/and
    (g/no-extra-keys-m (set-ops/difference c/condition-exact-keys #{:instance}))
    :conditions/condition-keys))


;; --base
(s/def :pipeline/kind (p/one-of #{"pipeline"}))
(s/def :pipeline/type (p/one-of  #{"docker"}))
(s/def :pipeline/name p/is-string?)


;; --platform
(s/def :platform/os (p/one-of  #{"windows" "os"}))
(s/def :platform/arch p/is-string?)
(s/def :platform/version p/is-number?)


(s/def :pipeline/platform
  (s/and
    (g/no-extra-keys-m #{:os :arch :version})
    (s/keys :req-un [:platform/os :platform/arch] :opt-un [:platform/version])
    pp/windows-platform-validator))


;; --workspace
(s/def :pipeline/workspace pp/has-path?)


;; --clone
(s/def :clone/depth pp/has-depth?)
(s/def :clone/disable pp/has-disable?)
(s/def :pipeline/clone (s/or :clone/depth :clone/disable))


;; --steps
(s/def :steps/all-deps-linked pp/all-deps-linked)


(s/def :pipeline/steps
  (s/and
    :general/unique-names
    :steps/all-deps-linked
    (s/coll-of :steps/step)))


;; --services
(s/def :services/name p/is-string?)
(s/def :services/image p/is-string?)


(s/def :services/service
  (s/and (g/no-extra-keys-m #{:name :image :environment})
         (s/keys :req-un [:services/name :services/image] :opt-un [:general/environment])))


(s/def :pipeline/services (s/coll-of :services/service))


;; --node
(s/def :pipeline/node (s/map-of p/is-keyword? p/is-string?))


;; --volumes
(s/def :pipeline-volumes/name p/is-string?)


(s/def :pipeline-volumes/temp
  (s/and
    p/is-map?
    (s/or :empty p/is-empty? :medium-memory pp/medium-memory)))


(s/def :pipeline-volumes/host (s/and p/is-map? pp/has-path?))


(s/def :pipeline-volumes/volume
  (s/and (g/no-extra-keys-m #{:name :host :temp})
         (s/keys :req-un
                 [(and :pipeline-volumes/name
                       (or :pipeline-volumes/temp :pipeline-volumes/host))])))


(s/def :pipeline/volumes (s/coll-of :pipeline-volumes/volume))


;; --general
(s/def :pipeline/depends_on
  (s/coll-of p/is-string?))


;; --overall rules
(s/def :pipeline/all-vols-linked pp/all-vols-linked)


(s/def ::pipeline
  (s/and
    :pipeline/all-vols-linked
    (g/no-extra-keys-m
      #{:trigger :kind :type :name :workspace :clone :steps :depends_on :volumes :node})
    (s/keys :req-un [:pipeline/kind :pipeline/type :pipeline/name]
            :opt-un [:pipeline/trigger :pipeline/platform :pipeline/workspace
                     :pipeline/clone :pipeline/steps :pipeline/services
                     :pipeline/depends_on :pipeline/node
                     :pipeline/volumes])))
