(ns drone-config.common
  (:require
    [clojure.spec.alpha :as s]
    [drone-config.helpers :as h]))


;; conditions
(s/def :conditions/branch   (h/incl-excl (s/coll-of string?)))
(s/def :conditions/event    (h/incl-excl (s/coll-of #{"push" "pull_request" "tag" "promote" "rollback"})))
(s/def :conditions/repo     (h/incl-excl (s/coll-of  #(boolean (re-find #"^[^/]+/[^/]+$" %)))))
(s/def :conditions/ref      (h/incl-excl (s/coll-of  #(boolean (re-find #"^([^/]+/)+[^/]+$" %)))))
(s/def :conditions/instance (h/incl-excl (s/coll-of  string?)))
(s/def :conditions/status (s/coll-of  #{"success" "failure"}))
(s/def :conditions/target (h/incl-excl (s/coll-of  string?)))
(s/def :conditions/cron (h/incl-excl (s/coll-of  string?)))
(s/def :conditions/action (h/incl-excl (s/coll-of string?)))

(def conditions-exact-keys 
  #{:branch :event :ref :repo :instance :status :target :cron :action})


(s/def ::conditions-keys-base
  (s/keys :req-un
          [(or :conditions/branch :conditions/event :conditions/ref :conditions/repo
               :conditions/instance :conditions/status :conditions/target
               :conditions/cron :conditions/action)]))


(s/def ::key-string-pair (s/map-of keyword? string?))

(s/def ::environment ::key-string-pair)


(s/def ::depends_on (s/coll-of string?))


(defn unique-names-checker
  "Checks if all maps have a unique :name value"
  [map-coll]
  (let [name-seq (map #(% :name) map-coll)]
    (= (count name-seq) (count (set name-seq)))))


(s/def ::unique-names unique-names-checker)
