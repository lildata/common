(ns common.error
  "Error helpers

   @ctdean"
  (:require
   [clojure.tools.logging :as log]))

(defmacro ignore-errors-and-log
  "Execute FORM and return the result.  If FORM throws an exception,
   log the error and return nil (or the given default value)"
  ([form] `(ignore-errors-and-log ~form "" nil))
  ([form msg] `(ignore-errors-and-log ~form ~msg nil))
  ([form msg error-return-value]
     `(try
       ~form
       (catch Throwable e#
         (log/warnf "Ignoring exception %s: %s" ~msg e#)
         ~error-return-value))))
