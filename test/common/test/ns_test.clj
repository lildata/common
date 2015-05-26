(ns common.test.ns-test
  (:require [clojure.test :refer :all]
            [common.ns :refer :all]))

(def- x 10)
(def- ^{:foo :bar} y 20)
(def- ^:private z 30)
(def- ^{:private false} zz 40)

(defn- meta-private
  [v]
  (:private (meta v)))

(deftest def-private-test
  (is (= x 10))
  (is (= (meta-private #'x) true)))

(deftest def-private-additional-metadata-test
  (is (= y 20))
  (is (= (meta-private #'y) true))
  (is (= (:foo (meta #'y)) :bar)))

(deftest def-private-redefined-test
  (is (= z 30))
  (is (= (meta-private #'z) true)))

(deftest def-private-overridden-test
  (is (= zz 40))
  ;; This behavior is intentional and matches `defn-`.
  (is (= (meta-private #'zz) true)))
