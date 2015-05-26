(ns common.ns)

(defmacro def-
  "Defines a private symbol. Analogous to defn- but for non-functions."
  [symbol init]
  `(def ~(vary-meta symbol assoc :private true) ~init))
