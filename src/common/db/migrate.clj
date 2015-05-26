(ns common.db.migrate
  "Database migrations management alternative main/entry-point.
  This is runnable using `lein run -m common.db.migrate` or compiled using
  `java -cp uberjar.jar common.db.migrate`. See usage below for more details."
  (:require
    [clams.conf :as conf]
    [clj-time.core :refer [now]]
    [clj-time.format :as tf]
    [clojure.string :refer [upper-case]]
    [clojure.tools.logging :as log]
    ragtime.core
    ragtime.sql.resources)
  (:gen-class))


(def ^:private migration-spec
  {:database (conf/get :database-jdbc-url)
   :migrations (ragtime.sql.resources/migrations (conf/get :migrations))})

(def usage
  "Manage database migrations.

Usage: lein run -m common.db.migrate <COMMAND> <ARGS>

Commands:
  migrate           Migrate to the latest version
  rollback [n]      Rollback n versions (defaults to 1)
  create            Creates a new migration template")

;;; Borrowed from ragtime.main. Remove once upstream fixed. >>>

(defn- wrap-println [f s]
  (fn [& args]
    (log/info s)
    (apply f args)))

(defn- verbose-migration [{:keys [id up down] :as migration}]
  (assoc migration
    :up   (wrap-println up   (str "Applying " id))
    :down (wrap-println down (str "Rolling back " id))))

(defn- resolve-migrations [migration-fn]
  (map verbose-migration (migration-fn)))

(defn migrate []
  (let [{:keys [database migrations]} migration-spec]
    (ragtime.core/migrate-all
      (ragtime.core/connection database)
      (resolve-migrations migrations))))

(defn rollback [n]
  (let [{:keys [database migrations]} migration-spec
        db (ragtime.core/connection database)]
    (doseq [m (resolve-migrations migrations)]
      (ragtime.core/remember-migration m))
    (ragtime.core/rollback-last db (or (when n (Integer/parseInt n)) 1))))

;;; <<<

(defn create [name]
  (doseq [direction ["up" "down"]]
    (let [tfmt (tf/formatter "yyyyMMddHHmm")
          prefix (tf/unparse tfmt (now))
          f (str "resources/migrations/" prefix "-" name "." direction ".sql")]
      (spit f (str
        "/**\n"
        " * " name "\n"
        " * " (upper-case direction) "\n"
        " */\n"))
      (println (str "Created " f))))
  (println
   "Don't forget to append to the 'migrations' vector in your app configuration!"))

(defn -main
  [& args]
  (case (first args)
    "migrate" (migrate)
    "rollback" (rollback (second args))
    "create" (create (second args))
    (do (println usage)
        (System/exit 1))))
