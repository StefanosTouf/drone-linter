(ns drone-config.plugins
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.helpers :as h]))


(defn from-secret?
  [v]
  (and (map? v) (string? (v :from_secret))))


(def protected-regexs [#"username" #"password" #"token"])


(defn protected-key?
  [k]
  (loop [[re & res] protected-regexs]
    (if re (if (re-find re (name k)) true (recur res)) false)))


(defn protected-key-from-secret
  [[k v]]
  (if (protected-key? k) (from-secret? v) true))


(s/def :plugins-base-settings/protected-secret-values 
  (s/coll-of :plugins-base-settings/protected-secret-value 
             protected-key-from-secret))

(s/def :plugins-base/settings
  :plugins-base-settings/protected-secret-values)


(defmacro plugin-keys
  [settings-spec]
  `(s/and
     (h/no-extra-keys-m
       #{:name :image :settings})
     (s/keys :opt-un [:plugins-base/settings])
     (s/keys :req-un [:step/name :step/image]
             :opt-un [:step/volumes ~settings-spec])))


(s/def :plugins-docker/settings (s/map-of keyword? (s/or :string string? :map map?)))
(s/def :plugins-discord/settings (s/map-of keyword? number?))
(s/def :plugins-default/settings map?)


(defmulti plugin-type :image)


(defmethod plugin-type "plugins/docker" [_]
  (plugin-keys :plugins-docker/settings))


(defmethod plugin-type "appleboy/drone-discord" [_]
  (plugin-keys :plugins-discord/settings))


(defmethod plugin-type :default [_]
  (plugin-keys :plugins-default/settings))

(s/def ::plugin (s/multi-spec plugin-type :image))

