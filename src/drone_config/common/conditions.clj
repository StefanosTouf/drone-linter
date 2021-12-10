(ns drone-config.common.conditions
  (:require [clojure.spec.alpha :as s]))

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
                     #(= (count %) 1))
           :both (s/and
                   #(s/valid? ~spec (:include %))
                   #(s/valid? ~spec (:exclude %))))))

;; conditions
(s/def :conditions/branch   (incl-excl (s/coll-of :general/string)))

(s/def :conditions-event/event 
  #{"push" "pull_request" "tag" "promote" "rollback"})
(s/def :conditions/event    (incl-excl (s/coll-of :conditions-event/event)))

(s/def :conditions-repo/repo #(boolean (re-find #"^[^/]+/[^/]+$" %)))
(s/def :conditions/repo (incl-excl (s/coll-of  :conditions-repo/repo)))

(s/def :conditions-ref/ref #(boolean (re-find #"^([^/]+/)+[^/]+$" %)))
(s/def :conditions/ref      (incl-excl (s/coll-of  :conditions-ref/ref)))
(s/def :conditions/instance (incl-excl (s/coll-of  :general/string)))

(s/def :condition-status/status #{"success" "failure"})
(s/def :conditions/status (s/coll-of  :condition-status/status))
(s/def :conditions/target (incl-excl (s/coll-of  :general/string)))
(s/def :conditions/cron (incl-excl (s/coll-of  :general/string)))
(s/def :conditions/action (incl-excl (s/coll-of :general/string)))


(def condition-exact-keys #{:branch :event :ref :repo :instance :status :target :cron :action})

(s/def :conditions/condition-keys
  (s/keys :req-un
            [(or :conditions/branch :conditions/event :conditions/ref :conditions/repo
                 :conditions/instance :conditions/status :conditions/target
                 :conditions/cron :conditions/action)]))
