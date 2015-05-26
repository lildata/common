(ns common.test.error-test
  "test our error helpers

   @ctdean"
  (:require
   [clojure.test :refer :all]
   [common.error :refer :all]))

(defn throw-error [x]
  (throw (Exception. (str "Test error " x))))

(deftest test-ignore-errors-and-log
  (is (= 3 (ignore-errors-and-log (+ 1 2))))
  (is (= 3 (ignore-errors-and-log (+ 1 2) "err msg")))
  (is (= 3 (ignore-errors-and-log (+ 1 2) "err msg" 88)))

  (is (nil? (ignore-errors-and-log (/ 2 0))))
  (is (nil? (ignore-errors-and-log (throw-error "simple"))))
  (is (nil? (ignore-errors-and-log (throw-error "with msg") "err msg")))
  (is (= 88 (ignore-errors-and-log (throw-error "with default") "err msg" 88))))
