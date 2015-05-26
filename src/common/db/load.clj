(ns common.db.load
  "Installs database fixture data. Provides an alternative -main so that it can
  be run from the command line: `lein run -m common.db.load`.

  The initial data file should be in a format that looks like this:

      [
        :table       ;; Table name as keyword
        [            ;; Row data
          {:id 1
           :foo \"bar\"}
          ...
        ]
        :another-table
        [
          {:baz :quux}
          ...
        ]
        ...
      ]

  This library also provides the following custom EDN tag/converters:

    - #date \"yyyy-mm-dd\"
      Yields a SQL-compatible Date object.
    - #datetime \"yyyy-mm-ddThh:mm:ssZ\"
      Yields a SQL-compatible DateTime object.

  >> jimbru"
  (:require
    [clams.conf :as conf]
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [common.convert :as convert]
    [common.db.util :as util]
    [korma.core :as korma]
    [korma.db :as db])
  (:gen-class))

(def dburi (conf/get :database-url))

(def database-map
  (when dburi
        (assoc (util/parse-url dburi)
               :subprotocol "postgresql"
               :subname (util/build-db-subname dburi))))

(db/defdb korma-db (db/postgres database-map))

(def ^:private edn-readers
  {'date     util/str->sql-date
   'datetime util/str->sql-date-time
   'uuid     convert/str->uuid})

(defn- read-edn-file [filename]
  (->> filename
       io/resource
       slurp
       (edn/read-string {:readers edn-readers})))

(defn- read-data-file [filename]
  (let [extension (last (clojure.string/split filename #"\."))]
    (case extension
      "edn" (read-edn-file filename)
      "clj" @(load-file filename))))

(defn load-data-file
  [file]
  (let [filename  (or file "initial_data.edn")
        file-data (read-data-file filename)]
    (db/transaction
      (doseq [[table rows] (partition 2 file-data)]
        (korma/insert (name table) (korma/values rows))))))

(defn -main
  [& args]
  (let [filename (first args)]
    (common.db.load/load-data-file filename)
    (println "Completed loading fixtures")))
