(ns drone-config.steps.plugins
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.common.general :as g]
    [drone-config.steps.plugin-predicates :as pp]
    [drone-config.steps.plugin-settings.discord :as discord]
    [drone-config.steps.plugin-settings.docker :as docker]))


(set! *warn-on-reflection* true)


(s/def :plugins/protected-secret-value
  pp/protected-secret-value)


(s/def :plugins/from-secret
  pp/from-secret?)


(s/def :plugins/settings
  (s/coll-of :plugins/protected-secret-value))


(s/def :plugins/setting-value
  (s/or :string :general/string
        :map :plugins/from-secret))


(defmacro plugin-keys
  [settings-spec]
  `(s/and
     (g/no-extra-keys-m
       #{:name :image :when :settings})
     (s/keys :opt-un [:plugins/settings])
     (s/keys :req-un [:step/name :step/image]
             :opt-un [:step/volumes :step/when ~settings-spec])))


(s/def :plugins-docker/settings
  (s/map-of docker/settings :plugins/setting-value))


(s/def :plugins-discord/settings
  (s/map-of discord/settings :plugins/setting-value))


(s/def :plugins-default/settings
  (s/map-of :general/keyword :plugins/setting-value))


(defmulti plugin-type :image)


(defmethod plugin-type docker/image-name [_]
  (plugin-keys :plugins-docker/settings))


(defmethod plugin-type discord/image-name [_]
  (plugin-keys :plugins-discord/settings))


(defmethod plugin-type :default [_]
  (plugin-keys :plugins-default/settings))


(s/def :plugins/plugin (s/multi-spec plugin-type :image))

