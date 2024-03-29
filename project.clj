(defproject workshub/shyvana "0.2.10-SNAPSHOT"
  :description "Clojure, data-based, wrapper for getstream.io Java API"
  :url "https://github.com/WorksHub/shyvana"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [io.getstream.client/stream-java "3.3.0"]]
  :repositories [["releases" {:url   "https://clojars.org/repo"
                              :creds :gpg}]
                 ["snapshots" {:url   "https://clojars.org/repo"
                               :creds :gpg}]])
