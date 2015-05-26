(ns common.test.url-test
  ">> jimbru"
  (:require [clojure.test :refer :all]
            [common.url :as url]))

(deftest join-test
  (let [base-url-1 "http://foo.com/a/b"
        base-url-2 "http://foo.com/a/b/"]
    (is (= "http://foo.com/a/b/bar" (url/join base-url-1 "/bar")))
    (is (= "http://foo.com/a/b/bar" (url/join base-url-1 "bar")))
    (is (= "http://foo.com/a/b/bar" (url/join base-url-2 "/bar")))
    (is (= "http://foo.com/a/b/bar" (url/join base-url-2 "bar")))
    (is (= "http://foo.com/a/b/bar/x/y" (url/join base-url-2 "/bar" "x" "y")))
    (is (= "http://foo.com/a/b/bar/x/y" (url/join base-url-2 "bar" "x" "y")))))
