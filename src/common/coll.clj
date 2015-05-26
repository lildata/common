(ns common.coll
  "Collection operations.
  >> ctdean jimbru"
  (:require [slingshot.slingshot :refer [throw+]]))

(declare keys-set)

(defn contains-exactly?
  "Tests whether a map contains exactly the specified keys."
  [coll keys]
  (= (keys-set coll) (set keys)))

(defn ensure-coll
  "Ensure that X is a collection. If X is nil when return an empty collection."
  [x]
  (cond
   (coll? x) x
   (nil? x) []
   :else [x]))

(defn find-first
  "Returns the first item in coll for which (pred item) returns true.
  Note that this is subtley different than some, which returns the output
  of (pred item), rather than the item itself."
  [pred coll]
  (first (filter pred coll)))

(defn get-or-throw
  "Strict version of 'get' that throws when the key cannot be found."
  [map key]
  (when-not (contains? map key)
    (throw+ {:type :key-not-found}))
  (get map key))

(defn keys-set
  "Returns a map's keys as a set."
  [coll]
  (set (keys coll)))

(defn update-vals-if-exist
  "Update the values if the keys exist."
  [map keys f]
  (when map
    (reduce (fn [m key]
                (if (contains? m key)
                    (update-in m [key] f)
                    m))
        map
        keys)))
