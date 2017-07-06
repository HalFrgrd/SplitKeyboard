(defproject optcase "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                [org.clojure/clojure "1.8.0"]
                [unicode-math "0.2.0"]
  				      [scad-clj "0.4.0"]
                [org.clojure/math.numeric-tower "0.0.4"]]
  :main ^:skip-aot optcase.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :resource-paths ["resources/scad-clj/scad-clj-0.5.2.jar"])
