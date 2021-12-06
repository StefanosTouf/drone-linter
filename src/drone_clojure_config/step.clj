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


;; general
(s/def ::name string?)
(s/def ::image string?)
(s/def ::command string?)
(s/def ::commands (s/coll-of ::command))
(s/def ::environment (s/every (fn [[k v]] (and (keyword? k) (string? v)))))


;; conditions
(s/def ::branch (incl-excl (s/coll-of string?)))

(s/def ::event (incl-excl (s/coll-of #{"push" "pull_request" "tag" "promote" "rollback"})))
(s/def ::repo (incl-excl (s/coll-of  #(boolean (re-find #"^[^/]+/[^/]+$" %)))))
(s/def ::ref (incl-excl (s/coll-of  #(boolean (re-find #"^([^/]+/)+[^/]+$" %)))))
(s/def ::instance (incl-excl (s/coll-of  string?)))

(def when-keys #{:branch :event :ref :repo :instance :status :target :cron})
(s/def ::when 
  (s/and (s/every (fn [[k _]] (when-keys k))) 
         (s/keys :req-un [(or ::branch ::event ::ref ::repo ::instance ::status ::target ::cron)])))


(s/def ::step (s/keys :req-un [::name ::image ::commands] :opt-un [::environment ::when ::failure ::detach ::privileged]))
