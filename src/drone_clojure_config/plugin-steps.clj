(ns drone-config.plugins
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.helpers :as h]))


(def docker-plugin-settings
  #{:registry
    :username
    :password
    :repo
    :tags
    :dockerfile
    :dry_run
    :purge
    :context
    :target
    :force_tag
    :insecure
    :mirror
    :bip=false
    :custom_dns
    :custom_dns_search
    :storage_driver
    :storage_path
    :build_args
    :build_args_from_env
    :auto_tag
    :auto_tag_suffix
    :debug
    :launch_debug
    :mtu
    :ipv6
    :experimental
    :daemon_off
    :cache_from
    :squash
    :pull_image
    :compress
    :custom_labels
    :label_schema
    :email
    :no_cache
    :add_host})


(s/def :settings/username #(string? (% :from_secret)))
(s/def :settings/password #(string? (% :from_secret)))


(s/def :plugin-step-base/settings
  (s/keys :opt-un [:settings/username :settings/password]))


(s/def :docker-plugin-step/settings
  (s/and :plugin-step-base/settings
         (s/map-of docker-plugin-settings string?)))


(s/def :default-plugin-step/settings
  (s/and :plugin-step-base/settings
         (s/map-of keyword? string?)))


(s/def :plugin-step-base/plugin-keys
  (s/and (s/keys :req-un [:step/name :step/image])
         (h/no-extra-keys-m #{:name :image :settings})))


(defmulti plugin-type :image)


(defmethod plugin-type "plugins/docker" [_]
  (s/and
    :plugin-step-base/plugin-keys
    (s/keys :opt-un [:docker-plugin-step/settings])))


(defmethod plugin-type :default [_]
  (s/and :plugin-step-base/plugin-keys 
         (s/keys :opt-un [:default-plugin-step/settings])))

(s/def ::plugin-step (s/multi-spec plugin-type :image))
