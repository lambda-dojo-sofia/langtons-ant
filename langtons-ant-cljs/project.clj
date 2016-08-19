(defproject langtons-ant-cljs "0.1.0-SNAPSHOT"
  :description "Langton's Ant build for the Sofia Lambda Dojo!"
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources"]
  :url "https://www.meetup.com/Lambda-Dojo-Sofia/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main langtons-ant-cljs.core
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [hiccup "1.0.5"]
                 [compojure "1.5.1"]
                 [ring "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring/ring-defaults "0.2.1"]
                 ;;cljs
                 [org.clojure/clojurescript "1.9.89"]
                 [org.clojure/core.async "0.2.385"
                  :exclusions [org.clojure/tools.reader]]
                 [reagent "0.5.1"]
                 [binaryage/devtools "0.7.2"]
                 [figwheel-sidecar "0.5.4-7"]
                 [com.cemerick/piggieback "0.2.1"]]
  :plugins [[lein-figwheel "0.5.4-7"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]]
  :figwheel {:builds           [{:id           "dev"
                                 :source-paths ["src/cljs" "src/cljc"]
                                 :compiler     {:output-to            "resources/public/js/ant.js"
                                                :source-map           true
                                                :source-map-timestamp true
                                                :output-dir           "resources/public/js/out"
                                                :asset-path           "http://localhost:3449/js/out"
                                                :optimizations        :none
                                                :warnings             true
                                                :cache-analysis       true
                                                :main                 langtons-ant-cljs.core}}]
             :http-server-root "public"
             :repl             false
             :server-port      3449
             :nrepl-port       7888
             :ring-handler     langtons-ant-cljs.fig/figwheel-resources})
