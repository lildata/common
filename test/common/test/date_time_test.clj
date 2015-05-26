(ns common.test.date-time-test
  (:require
    [clojure.test :refer :all]
    [common.date-time :refer :all]))

(deftest test-msec->iso8601
  (is (= (msec->iso8601 0) "1970-01-01T00:00:00.000Z"))
  (is (= (msec->iso8601 1) "1970-01-01T00:00:00.001Z"))
  (is (= (msec->iso8601 -1000) "1969-12-31T23:59:59.000Z"))
  (is (= (msec->iso8601 1411517671131) "2014-09-24T00:14:31.131Z"))
  (is (thrown? ArithmeticException (msec->iso8601 (inc Long/MAX_VALUE)))))

;; Not sure how to do this...
(deftest test-now-iso8601
  (is (= (subs (now-iso8601) 0 20)
         (subs (msec->iso8601 (System/currentTimeMillis)) 0 20))))
