(ns common.test.coll-test
  (:require
    [clojure.test :refer :all]
    [common.coll :refer :all]
    slingshot.test))

(deftest contains-exactly?-test
  (let [x {:foo 1 :bar 2}]
    (is (true? (contains-exactly? x [:foo :bar])))
    (is (false? (contains-exactly? x [:foo :bar :baz])))
    (is (false? (contains-exactly? x [:foo])))))

(deftest ensure-coll-test
  (is (= (ensure-coll nil) []))
  (is (= (ensure-coll 88) [88]))
  (is (= (ensure-coll [88]) [88]))
  (is (= (ensure-coll []) []))
  (is (= (ensure-coll [88 "foo"]) [88 "foo"]))
  (is (= (ensure-coll (seq [88 "foo"])) (seq [88 "foo"])))
  (is (= (ensure-coll "bar") ["bar"])))

(deftest find-first-test
  (is (= (find-first #(do true) []) nil))
  (let [coll [:foo :bar :baz :bar :baz]]
    (is (= (find-first #(= :baz %) coll) :baz))
    (is (= (find-first #(or (= :bar %) (= :baz %)) coll) :bar)))
  (let [coll (sorted-map :foo 1 :bar 2 :baz 2)]
    ;; Unsurprisingly, hash-maps yield undefined results here,
    ;; so we use a sorted-map instead.
    (is (= (find-first #(= (second %) 2) coll) [:bar 2]))))

(deftest get-or-throw-test
  (testing "key exists"
    (is (= (get-or-throw {:foo :bar} :foo) :bar)))
  (testing "key not found"
    (is (thrown+? [:type :key-not-found] (get-or-throw {} :foo)))))

(deftest keys-set-test
  (is (= (keys-set {:foo 1 :bar 2}) #{:foo :bar})))

(deftest update-vals-if-exist-test
  (is (= (update-vals-if-exist {:a 1 :b 2 :c 3} [:a :c :d] str)
         {:a "1" :b 2 :c "3"}))
  (is (= (update-vals-if-exist {:a 1 :b 2 :c 3} [] str)
         {:a 1 :b 2 :c 3}))
  (is (= (update-vals-if-exist nil [] str)
         nil))
  (is (= (update-vals-if-exist {} [:a :c :d] str)
         {})))
