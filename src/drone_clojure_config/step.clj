(ns drone-config.step
  (:require
    [clojure.set :as set-ops]
    [clojure.spec.alpha :as s]
    [drone-config.common :as c]
    [drone-config.helpers :as h]))


(set! *warn-on-reflection* true)


;; general
(s/def :step/name    string?)
(s/def :step/image   string?)
(s/def :step/command string?)
(s/def :step/commands    (s/coll-of :step/command))


;; extras
(s/def :step/failure #{"ignore"})
(s/def :step/detach boolean?)
(s/def :step/privileged boolean?)


(s/def ::when
  (s/and
    (h/no-extra-keys-m
      (set-ops/difference c/conditions-exact-keys #{:action}))
    :drone-config.common/conditions-keys-base))


(s/def :step-volumes/name string?)
(s/def :step-volumes/path string?)


(s/def :step-volumes/volume
  (s/and (h/no-extra-keys-m #{:name :path})
         (s/keys :req-un [:step-volumes/name :step-volumes/path])))


(s/def :step/volumes (s/coll-of :step-volumes/volume))


(s/def ::step
  (s/or
    (s/and 
      (h/no-extra-keys-m #{:name :image :settings})
      ::plugin-step)
    (s/and
      (h/no-extra-keys-m
        #{:name :image :commands :environment :depends_on
          :when :failure :detach :privileged :volumes})
      (s/keys :req-un [:step/name :step/image :step/commands]
              :opt-un [:drone-config.common/environment ::when
                       :drone-config.common/depends_on :step/failure
                       :step/detach :step/privileged :step/volumes]))))
