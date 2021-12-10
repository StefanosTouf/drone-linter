(ns drone-config.steps.plugin-settings.docker)

(def image-name "plugins/docker")


(def settings
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
    :force_tag=false
    :insecure
    :mirror
    :bip
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
