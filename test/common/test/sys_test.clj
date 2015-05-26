(ns common.test.sys-test
  (:require
    [clojure.test :refer :all]
    [common.sys :refer :all]))

(deftest test-retry-action
  ;; Test Basic
  (let [attempts (atom 0)
        res (retry-action {:action (fn [last-state]
                                     (swap! attempts inc)
                                     88)})]
    (is (= res 88))
    (is (= @attempts 1)))
  ;; Test the full monty
  (let [attempts (atom 0)
        pauses (atom 0)
        res (retry-action {:action (fn [last-state]
                                     (swap! attempts inc)
                                     (* last-state 2))
                           :success? (fn [state] (> state 33))
                           :finish (fn [n state] (vector n state))
                           :pause (fn [n state] (swap! pauses inc))
                           :attempts 10
                           :initial-state 1})]
    (is (= res [6 64]))
    (is (= @attempts 6))
    (is (= @pauses 5))))
