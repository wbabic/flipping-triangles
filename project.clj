(defproject geometry "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"] 
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [com.cemerick/piggieback "0.1.2"]]
  :plugins [[lein-cljsbuild "1.0.1"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :cljsbuild
  {:builds
   [{:id "dev"
     :source-paths ["src/geometry"
                    "src/dev"]
     :compiler {:optimizations :whitespace
                :pretty-print false
                :output-to "resources/public/js/geometry.js"}}
    {:id "advanced"
     :source-paths ["src/geometry"]
     :compiler {:optimizations :advanced
                :pretty-print false
                :output-to "resources/public/js/geometry-advanced.js"
                :output-wrapper true}}]}
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [org.clojure/java.classpath "0.2.0"]]}})
