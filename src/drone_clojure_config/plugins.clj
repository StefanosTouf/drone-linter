(ns drone-config.plugins
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.helpers :as h]))


(defn from-secret?
  [v]
  (and (map? v) (string? (v :from_secret))))


(defn token-from-secret?
  [[k v]]
  (if (re-find #"token" (name k)) (from-secret? v) true))


(s/def :base-settings/username from-secret?)
(s/def :base-settings/password from-secret?)


(s/def :plugins-base/settings
  (s/and
    (s/every token-from-secret?)
    (s/keys :opt-un [:base-settings/username :base-settings/password])))


(defmacro plugin-keys
  [settings-spec]
  `(s/and
     (h/no-extra-keys-m
       #{:name :image :settings})
     (s/keys :opt-un [:plugins-base/settings])
     (s/keys :req-un [:step/name :step/image]
             :opt-un [:step/volumes ~settings-spec])))


(s/def :plugin-docker/settings (s/map-of keyword? (s/or :string string? :map map?)))
(s/def :plugin-discord/settings (s/map-of keyword? number?))
(s/def :plugin-default/settings map?)


(defmulti plugin-type :image)


(defmethod plugin-type "plugins/docker" [_]
  (plugin-keys :plugin-docker/settings))


(defmethod plugin-type "appleboy/drone-discord" [_]
  (plugin-keys :plugin-discord/settings))


(defmethod plugin-type :default [_]
  (plugin-keys :plugin-default/settings))


(s/def ::plugin (s/multi-spec plugin-type :image))
