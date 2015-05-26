(ns common.string
  "String operations that should really be in the standard lib.
  >> jimbru")

(defn starts-with?
  "Predicate whether a string starts with a substring."
  [s sub]
  (when s
    (.startsWith s sub)))
