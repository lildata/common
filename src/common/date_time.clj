(ns common.date-time
  "Dates and times.
  >> ctdean jimbru"
  (:require
    [clj-time.core :as time]
    [clj-time.format :as tformat])
  (:import org.joda.time.DateTime))

(def date-time-fmt (tformat/formatters :date-time))

(defn now-iso8601
  "The time now in ISO 8601 format."
  []
  (tformat/unparse date-time-fmt (time/now)))

(defn msec->iso8601
  "Convert the time in milliseconds to ISO 8601 format."
  [milliseconds]
  (tformat/unparse date-time-fmt (DateTime. milliseconds)))
