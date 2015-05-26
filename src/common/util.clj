(ns common.util
  "general purpose utilities

   @ctdean"
  (:import com.eaio.uuid.UUIDGen))

(defn make-uuid
  "Make a time based uuid. Inspired by the Netflix astyanax lib."
  []
  (java.util.UUID. (UUIDGen/newTime) (UUIDGen/getClockSeqAndNode)))

(defn make-tag
  "A ST tag is a formatted string with an encoded namespace in it. In
  general, use the app name for the namespace unless there is a good
  reason not to.

  As an example, a tag created by CIS would look like

        cis:123-456-789
  "
  [namespace value]
  {:pre (pos? (count namespace))}
  (str namespace ":" value))
