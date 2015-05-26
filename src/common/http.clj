(ns common.http
  "HTTP client helpers. These all throw an exception on error.

  Note: The Ring spec [1] asks for lower-cased strings for HTTP header names.
  We respect that convention here even though an more flexible implementation
  like clj-http's [2] would ultimately be preferable.

  [1] https://github.com/ring-clojure/ring/blob/master/SPEC
  [2] https://github.com/dakrone/clj-http#headers

  >> ctdean jimbru"
  (:refer-clojure :exclude [get])
  (:require
    [cheshire.core :as json]
    [clojure.set :refer [intersection subset?]]
    [clojure.tools.logging :as log]
    [common.coll :refer [keys-set]]
    [common.string :refer [starts-with?]]
    [org.httpkit.client :as http]
    [slingshot.slingshot :refer [throw+]])
  (:import com.fasterxml.jackson.core.JsonParseException))

(defn- error [type response]
  {:type type :response response})

(defn response->json
  "JSON-decodes HTTP response bodies.
  Transforms JSON string keys into Clojure keywords for easier access."
  [response]
  (try
    (json/parse-string (:body response) true)
    (catch JsonParseException e
      (throw+ (error ::error-response-json response)))))

(defn- decode-response-body
  [response]
  (let [content-type (:content-type (:headers response))]
    (condp #(starts-with? %2 %1) content-type
      "application/json" (response->json response)
      (do
        (log/debug "Unknown Content-Type, skipping decode:" response)
        (:body response)))))

(defn- process-response
  [response]
  (when (:error response)
    (throw+ (error ::error-network response)))
  (when-not (<= 200 (:status response) 299)
    (throw+ (error ::error-response response)))
  (decode-response-body response))

(defn- opt-json-body [opts]
  (if-let [json-body (:json-body opts)]
    (do
      (when (get-in opts [:headers "content-type"])
        (log/warnf "Overwriting existing Content-Type header in request: %s" opts))
      (-> opts
          (assoc :body (json/generate-string json-body))
          (assoc-in [:headers "content-type"] "application/json")
          (dissoc :json-body)))
    opts))

(defn- opt-jwt [opts]
  (if-let [jwt (:jwt opts)]
    (do
      (when (get-in opts [:headers "authorization"])
        (log/warnf "Overwriting existing Authorization header in request: %s" opts))
      (-> opts
          (assoc-in [:headers "authorization"] (str "JWT " jwt))
          (dissoc :jwt)))
    opts))

(defn request
  "Performs an HTTP request. Supports the following options:

    :url            string
        URL to request.
    :method         keyword
        HTTP method.
    :headers        {string string}
        Additional HTTP headers to include. Note that some headers
        will be included automatically.
    :query-params   {string string}
        Will be URL-encoded and added to the URL's query string.
    :form-params    {string string}
        Will be form-encoded and set as the request body.
    :body           string
        Sets the request body.
    :json-body      {any any}
        Will be JSON-encoded and set as the request body.
    :jwt            string
        Sets a JWT authorization header with this value.
  "
  [opts]
  (assert (subset? (keys-set opts)
                   #{:url :method :headers :query-params :form-params
                     :body :json-body :jwt})
          "Unsupported option.")
  (assert (intersection (keys-set opts) #{:form-params :body :json-body})
          "Options :form-params, :body, and :json-body are mutually exclusive.")
  (let [processed-opts (-> opts
                           opt-json-body
                           opt-jwt)]
    (future (process-response @(http/request processed-opts)))))

(defn get
  "Convenience function for GETs."
  [url & {:keys [jwt]
          :or   {jwt nil}}]
  (request {:method :get
            :url url
            :jwt jwt}))

(defn post
  "Convenience function for POSTs."
  ([url]
    (post url nil))
  ([url body & {:keys [jwt]
                :or   {jwt nil}}]
    (request {:method :post
              :url url
              :jwt jwt
              :json-body body})))

(defn put
  "Convenience function for PUTs."
  ([url]
    (put url nil))
  ([url body & {:keys [jwt]
                :or   {jwt nil}}]
    (request {:method :put
              :url url
              :jwt jwt
              :json-body body})))
