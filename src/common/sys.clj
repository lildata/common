(ns common.sys
  "System operators.
  >> ctdean jimbru"
  (:require [plumbing.core :refer [defnk]]))

(defn sleep
  "Sleep this thread for N milliseconds"
  [milliseconds]
  (Thread/sleep milliseconds))

(defnk retry-action
  "Retry action ATTEMPTS times until SUCCESS? is achieved.
   Run PAUSE to wait between actions."
  [action
   {success? (fn [state] state)}
   {attempts 5}
   {initial-state nil}
   {pause (fn [n state] (sleep (* 100 n)))}
   {finish (fn [n state] state)}]
  (loop [i 0
         state initial-state]
    (when (< i attempts)
      (let [xstate (action state)]
        (if (success? xstate)
            (finish (inc i) xstate)
            (do
              (pause i xstate)
              (recur (inc i) xstate)))))))
