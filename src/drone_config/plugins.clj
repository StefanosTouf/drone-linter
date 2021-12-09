(ns drone-config.plugins
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.helpers :as h]
    [drone-config.plugin-settings.discord :as discord]
    [drone-config.plugin-settings.docker :as docker]))


(defn from-secret?
  [v]
  (and (map? v) (string? (v :from_secret))))


(def protected-regexs [#"username" #"password" #"token"])


(defn protected-key?
  [k]
  (loop [[re & res] protected-regexs]
    (if re (if (re-find re (name k)) true (recur res)) false)))


(defn protected-value-from-secret
  "A protected value must be obtained from a secret"
  [[k v]]
  (if (protected-key? k) (from-secret? v) true))


(s/def :plugins-base-settings/protected-secret-values
  (s/coll-of drone-config.plugins/protected-value-from-secret))


(s/def :plugins-base/settings
  :plugins-base-settings/protected-secret-values)

(s/def :plugins-base-settings/setting-value (s/or :string string? :map from-secret?))

(defmacro plugin-keys
  [settings-spec]
  `(s/and
     (h/no-extra-keys-m
       #{:name :image :settings})
     (s/keys :opt-un [:plugins-base/settings])
     (s/keys :req-un [:step/name :step/image]
             :opt-un [:step/volumes ~settings-spec])))


(s/def :plugins-docker/settings
  (s/map-of docker/settings :plugins-base-settings/setting-value))


(s/def :plugins-discord/settings 
  (s/map-of discord/settings :plugins-base-settings/setting-value))

(s/def :plugins-default/settings 
  (s/map-of keyword? :plugins-base-settings/setting-value))


(defmulti plugin-type :image)


(defmethod plugin-type docker/image-name [_]
  (plugin-keys :plugins-docker/settings))


(defmethod plugin-type discord/image-name [_]
  (plugin-keys :plugins-discord/settings))


(defmethod plugin-type :default [_]
  (plugin-keys :plugins-default/settings))


(s/def ::plugin (s/multi-spec plugin-type :image))

