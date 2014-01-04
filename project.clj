(defproject geometry "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2030"] 
                 [org.clojure/core.async "0.1.256.0-1bf8cf-alpha"]
                 [com.cemerick/piggieback "0.1.2"]]
  :profiles {:dev {:source-paths ["dev"]}}
  :plugins [[lein-cljsbuild "1.0.1"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :cljsbuild {
              :builds [
                       {:id "overview"
                        :source-paths ["src/geometry"]
                        :compiler {:output-to "resources/public/js/geometry.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       {:id "debug"
                        :source-paths ["src/geometry/connect"]
                        :compiler {:output-to "resources/public/js/connect.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]})
