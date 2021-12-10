(ns drone-config.steps.plugin-predicates)

(defn from-secret?
  "A protected value must be obtained from a secret"
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
