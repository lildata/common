(ns common.test.convert-test
  (:require
    [clojure.pprint :refer [cl-format]]
    [clojure.test :refer :all]
    [common.convert :refer :all]))

(deftest test-to-keyword
  (is (nil? (to-keyword nil)))
  (is (= (to-keyword :foo) :foo))
  (is (= (to-keyword :foo2) :foo2))
  (is (= (to-keyword 88) :88))
  (is (= (to-keyword "foo") :foo))
  (is (= (to-keyword "hello world") (keyword "hello world"))))

(def punct "`~!@#$%^&*()-_+={}|\\][':;,./<>?")

(deftest test-to-bytes
  (is (nil? (to-bytes nil)))
  (is (= [104 101 108 108 111] (vec (to-bytes "hello"))))
  (is (= [96 126 33 64 35 36 37 94 38 42 40 41 45 95 43 61 123 125 124
          92 93 91 39 58 59 44 46 47 60 62 63]
         (vec (to-bytes punct))))
  (is (= [112 105 32 -49 -128] (vec (to-bytes "pi π"))))
  (is (= [] (vec (to-bytes ""))))
  (dotimes [_ 10]
    (let [s (cl-format nil "~r" (rand-int (Math/pow 2 31)))]
      (is (= (from-bytes (to-bytes s)) s)))))

(deftest test-from-bytes
  (is (nil? (from-bytes nil)))
  (is (= "goodbye" (from-bytes (byte-array [103 111 111 100 98 121 101]))))
  (is (= "Omega Ω" (from-bytes (byte-array [79 109 101 103 97 32 -50 -87]))))
  (is (= "" (from-bytes (byte-array []))))
  (is (= (from-bytes (to-bytes punct)) punct)))

(deftest test-str->int
  (testing "str->int"
    (is (= (str->int "0") 0))
    (is (= (str->int "1") 1))
    (is (= (str->int "-1") -1))
    (is (= (str->int "123") 123))
    (is (= (str->int 123) 123))
    (is (= (str->int -1) -1))
    (is (= (str->int "2147483647") 2147483647))
    (is (= (str->int "2147483648") 2147483648))))
