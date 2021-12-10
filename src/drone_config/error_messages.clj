(ns drone-config.error-messages
  (:require
    [drone-config.pipeline.specs :as p]))


(def mm-keys "Missing or mismatched keys")


(def inc-excl-cond
  "Must be either a collection, or include/exclude key with collection value")


(def error-messages
  {:general/environment
   "Environment is required to be a map of key-string pairs"
   :general/unique-names
   ":name keys must be unique"
   :pipeline/trigger
   (str  "Unexpected value: trigger keys must be one of " p/trigger-exact-keys)
   :pipeline/kind
   "Unsupported pipeline kind"
   :pipeline/type
   "Unsupported pipeline type"
   :general/string
   "Should be a string value"
   :general/integer
   "Should be an integer value"
   :general/map
   "Should be a map value"
   :general/empty
   "Should be empty"
   :general/boolean
   "Should be a boolean value"
   :general/number
   "Should be a number value"
   :general/keyword
   "Should be a keyword value"
   :pipeline/platform
   mm-keys
   :pipeline/has-path
   "Expected :path key with string value"
   :pipeline/has-depth
   "Expected :depth key with integer value"
   :pipeline/has-disable
   "Expected :disable key with boolean value"
   :steps/all-deps-linked
   "All graph step dependencies must be linked"
   :steps/all-vols-linked
   "All volumes must be linked"
   :services/service
   mm-keys
   :pipeline/services
   "Expected a collection of services"
   :pipeline/node
   "Expected a node map"
   :pipeline-volumes/temp
   "Invalid structure"
   :pipeline-volumes/volume
   mm-keys
   :pipeline/volumes
   "Expected a collection of volumes"
   :pipeline/depends_on
   "Expected a collection of dependencies"
   :pipelines/pipeline
   mm-keys
   :plugins/protected-secret-value
   "A protected value must be obtained through a secret"
   :plugins/from-secret
   "A protected value must be obtained through a secret"
   :plugins/settings
   "Protected settings must be obtained through secrets"
   :plugins/setting-value
   "Plugin setting values can be either strings or references to secrets"
   :plugins-docker/settings
   "Invalid settings for docker plugin"
   :plugins-discord/settings
   "Invalid settings for discord plugin"
   :step/commands
   "Expected a collection of commands"
   :step/failure
   "Unexpected value"
   :step/when
   "Invalid key"
   :step-volumes/volume
   mm-keys
   :step/volumes
   "Expected a collection of volumes"
   :step/depends_on
   "Expected a collection of dependencies"
   :steps/step
   mm-keys
   :platform/windows
   "Windows platform must specify a version"
   :conditions/ref
   inc-excl-cond
   :conditions/cron
   inc-excl-cond
   :conditions/repo
   inc-excl-cond
   :conditions/branch
   inc-excl-cond
   :conditions/event
   inc-excl-cond
   :conditions/instance
   inc-excl-cond
   :conditions/status
   inc-excl-cond
   :conditions/action
   inc-excl-cond
   :conditions/condition-keys
   mm-keys})
