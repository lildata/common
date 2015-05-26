(ns common.test.http-test
  ">> ctdean jimbru"
  (:require
    [clojure.test :refer :all]
    [org.httpkit.fake :refer :all])
  (:require
    [cheshire.core :as json]
    [common.http :as http]
    [ring.util.response :as response]
    slingshot.test)
  (:import com.fasterxml.jackson.core.JsonParseException))

;; Different valid payloads
(def PAYLOADS
  {1  ""
   2  "2"
   3  "true"
   4  "\"hello\""
   5  "[1, 2, 3, 4, 5]"
   6  "{\"six\": 6, \"sixes\": 66}"
   7 "[{\"id\":2,\"name\":\"MAIN CHECKING\",\"current_balance\":1234}]"})

(deftest response->json-test
  (doseq [payload (vals PAYLOADS)]
    (let [data (http/response->json (response/response payload))]
      (is (= data (json/parse-string payload true)))))
  (is (thrown+? [:type :common.http/error-response-json]
                (http/response->json {:body "{\"foo\":\"bar\""}))))

(defn- json-response [body]
  {:status 200 :headers {:content-type "application/json;"} :body body})

(deftest http-get-test
  (with-fake-http ["http://localhost/foo/1" (json-response (PAYLOADS 1))
                   "http://localhost/foo/2" (json-response (PAYLOADS 2))
                   "http://localhost/foo/3" (json-response (PAYLOADS 3))
                   "http://localhost/foo/4" (json-response (PAYLOADS 4))
                   "http://localhost/foo/5" (json-response (PAYLOADS 5))
                   "http://localhost/foo/6" (json-response (PAYLOADS 6))
                   "http://localhost/foo/7" (json-response (PAYLOADS 7))
                   "http://localhost/foo/error" {:status 500 :body "a bad error"}]
    (doseq [[key payload] PAYLOADS]
      (let [data @(http/get (str "http://localhost/foo/" key))]
        (is (= data (json/parse-string payload true)))))
    (is (thrown+? [:type :common.http/error-response]
                  @(http/get "http://localhost/foo/error")))))

(defn- fake-server
  [expected-body response]
  (fn [opts]
    (is (= (json/parse-string (:body opts)) expected-body))
    (future response)))

(defn- fake-auth-server
  [response expected-jwt]
  (fn [opts]
    (is (= expected-jwt (get-in opts [:headers "authorization"])))
    (future response)))

(deftest http-get-jwt-test
  (with-redefs [org.httpkit.client/request (fake-auth-server {:status 200} "JWT ABC123")]
    @(http/get "http://localhost/foo" :jwt "ABC123")))

(deftest http-post-jwt-test
  (with-redefs [org.httpkit.client/request (fake-auth-server {:status 200} "JWT ABC123")]
    @(http/post "http://localhost/foo" {"foo" "bar" "baz" "quux"} :jwt "ABC123")))

(deftest http-post-test
  (let [expected-body {"foo" "bar" "baz" "quux"}]
    (with-redefs [org.httpkit.client/request
                  (fake-server expected-body (json-response "{\"foo\": 1}"))]
      (is (= @(http/post "http://localhost/foo" expected-body) {:foo 1})))))

(deftest http-post-no-body-test
  (with-redefs [org.httpkit.client/request (fake-server nil (json-response "{\"foo\": 1}"))]
    (is (= @(http/post "http://localhost/foo") {:foo 1}))))

(deftest http-put-jwt-test
  (with-redefs [org.httpkit.client/request (fake-auth-server {:status 200} "JWT ABC123")]
    @(http/put "http://localhost/foo" {"foo" "bar" "baz" "quux"} :jwt "ABC123")))

(deftest http-put-test
  (let [expected-body {"foo" "bar" "baz" "quux"}]
    (with-redefs [org.httpkit.client/request
                  (fake-server expected-body (json-response "{\"foo\": 1}"))]
      (is (= @(http/put "http://localhost/foo" expected-body) {:foo 1})))))

(deftest http-put-no-body-test
  (with-redefs [org.httpkit.client/request (fake-server nil (json-response "{\"foo\": 1}"))]
    (is (= @(http/put "http://localhost/foo") {:foo 1}))))

(deftest network-error-test
  (is (thrown+? [:type :common.http/error-network] @(http/get "http://localhost:79"))))
