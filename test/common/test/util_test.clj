(ns common.test.util-test
  "test our utility function

   @ctdean"
  (:require
   [clojure.test :refer :all]
   [common.util :refer :all]))

(deftest test-uuid
  (let [uus-1 (repeatedly 10000 make-uuid)]
    (is (= 10000 (count (distinct uus-1))))
    (is (= (sort uus-1) uus-1))
    (let [uus-2 (concat uus-1 (repeatedly 10000 make-uuid))]
      (is (= 20000 (count (distinct uus-2))))
      (is (= (sort uus-2) uus-2)))))

(deftest test-make-tag
  (let [normal (make-tag "common" (make-uuid))
        short  (make-tag "common" "a")
        short2 (make-tag "common" "a")
        cis    (make-tag "cis" "a")]
    (is (= 1 (count (re-seq #":" normal))))
    (is (.startsWith normal "common:"))
    (is (= short short2))
    (is (not= short cis))))
