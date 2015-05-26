(defproject st/common
  "0.8.2-tag"
  :description "The Standard Treasury Common Library"
  :dependencies [
                 [cheshire "5.4.0"]
                 [clams "0.1.0" :exclusions [potemkin]]
                 [clj-time "0.8.0"]
                 [com.cemerick/url "0.1.1"]
                 [com.github.stephenc.eaio-uuid/uuid "3.4.0"]
                 [environ "1.0.0"]
                 [http-kit "2.1.19"]
                 [jarohen/nomad "0.7.0"]
                 [jimbru/ragtime "0.4.0-SNAPSHOT"]
                 [korma "0.4.0"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.7"]
                 ;; holding plumbing at 0.3.3 until this is resolved:
                 ;; https://github.com/Prismatic/plumbing/issues/74
                 [prismatic/plumbing "0.3.3"]
                 [prismatic/schema "0.3.1"]
                 [robert/hooke "1.3.0"]
                 [slingshot "0.12.1"]
                 ]
  :plugins [[lein-ancient "0.5.5"]
            [lein-environ "1.0.0"]
            [org.clojars.cvillecsteele/lein-git-version "1.0.0"]]
  :aot [common.db.migrate common.db.load]
  :profiles
  {:uberjar {:aot :all}
   :dev {:dependencies [[ring/ring-devel "1.3.1"]
                        [http-kit.fake "0.2.2"]]}}
  :min-lein-version "2.0.0")
