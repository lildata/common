(ns common.url
  ">> jimbru"
  (:require [clojure.string :as string]))

(defn- strip-leading-slash
  [segment]
  (if (= (first segment) \/)
    (apply str (drop 1 segment))
    segment))

(defn- strip-trailing-slash
  [segment]
  (if (= (last segment) \/)
    (apply str (drop-last segment))
    segment))

(defn- normalize-path-segment
  [segment]
  (-> segment
      strip-leading-slash
      strip-trailing-slash))

(defn join
  "Create a url from a base URL and any additional paths segments.
  All path segments are assumed to be relative paths regardless of
  whether they start with a slash character."
  [base-url & path-segments]
  (let [norm-base-url (strip-trailing-slash base-url)
        norm-segments (map normalize-path-segment path-segments)]
    (string/join "/" (cons norm-base-url norm-segments))))
