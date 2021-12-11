(ns drone-config.steps.specs
  (:require
    [clojure.set :as set-ops]
    [clojure.spec.alpha :as s]
    [drone-config.common.conditions :as c]
    [drone-config.common.general :as g]))

(set! *warn-on-reflection* true)

;; general
(s/def :step/name :general/string)
(s/def :step/image :general/string)
(s/def :step/command :general/string)
(s/def :step/commands (s/coll-of :step/command))


;; extras
(s/def :step/failure #{"ignore"})
(s/def :step/detach :general/boolean)
(s/def :step/privileged :general/boolean)


(s/def :step/when
  (s/and
    (g/no-extra-keys-m (set-ops/difference c/condition-exact-keys #{:action}))
    :conditions/condition-keys))


(s/def :step-volumes/name :general/string)
(s/def :step-volumes/path :general/string)


(s/def :step-volumes/volume
  (s/and (g/no-extra-keys-m #{:name :path})
         (s/keys :req-un [:step-volumes/name :step-volumes/path])))


(s/def :step/volumes (s/coll-of :step-volumes/volume))

(s/def :step/depends_on
  (s/coll-of :general/string))

(s/def :steps/step
  (s/or
    :plugin :plugins/plugin
    :normal (s/and
              (g/no-extra-keys-m
                #{:name :image :commands :environment :depends_on
                  :when :failure :detach :privileged :volumes})
              (s/keys :req-un [:step/name :step/image :step/commands]
                      :opt-un [:general/environment :step/when
                               :step/depends_on :step/failure
                               :step/detach :step/privileged :step/volumes]))))
