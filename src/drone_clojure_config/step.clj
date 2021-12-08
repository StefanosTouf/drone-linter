(ns drone-config.step
  (:require
    [clojure.spec.alpha :as s]))


(set! *warn-on-reflection* true)

(s/def ::single-elem-map #(= (count %) 1))


(defmacro incl-excl
  [spec]
  `(s/or :array
         (s/and #(or (vector? %) (seq? %)) ~spec)
         :incl-excl
         (s/or
           :one-of (s/and
                     (s/map-of
                       (s/or
                         :include #(= :include %)
                         :exclude #(= :exclude %))
                       ~spec)
                     ::single-elem-map)
           :both (s/and
                   #(s/valid? ~spec (:include %))
                   #(s/valid? ~spec (:exclude %))))))


(defn belongs
  [key-set]
  (fn [[k _]] (boolean (key-set k))))


(defmacro no-extra-keys-m
  [k-set]
  `(s/coll-of
     (belongs ~k-set)))


;; general
(s/def :step/name    string?)
(s/def :step/image   string?)
(s/def :step/command string?)
(s/def :step/commands    (s/coll-of :step/command))
(s/def :step/environment (s/every (fn [[k v]] (and (keyword? k) (string? v)))))


;; conditions
(s/def :when/branch   (incl-excl (s/coll-of string?)))
(s/def :when/event    (incl-excl (s/coll-of #{"push" "pull_request" "tag" "promote" "rollback"})))
(s/def :when/repo     (incl-excl (s/coll-of  #(boolean (re-find #"^[^/]+/[^/]+$" %)))))
(s/def :when/ref      (incl-excl (s/coll-of  #(boolean (re-find #"^([^/]+/)+[^/]+$" %)))))
(s/def :when/instance (incl-excl (s/coll-of  string?)))
(s/def :when/status (s/coll-of  #{"success" "failure"}))
(s/def :when/target (incl-excl (s/coll-of  string?)))
(s/def :when/cron (incl-excl (s/coll-of  string?)))


(s/def :when/when
  (s/and
    (no-extra-keys-m  #{:branch :event :ref :repo :instance :status :target :cron})
    (s/keys :req-un [(or :when/branch :when/event :when/ref :when/repo :when/instance :when/status :when/target :when/cron)])))

;extras
(s/def :step/failure #{"ignore"})
(s/def :step/detach boolean?)
(s/def :step/privileged boolean?)


(s/def ::step
  (s/and
    (no-extra-keys-m #{:name :image :commands :environment :when :failure :detach :privileged})
    (s/keys :req-un [:step/name :step/image :step/commands] :opt-un [:step/environment :when/when :step/failure :step/detach :step/privileged])))
