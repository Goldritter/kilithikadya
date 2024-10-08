(defproject kilithikadya "0.1.1-a.2"
  :description "Probability calculation application for Warhammer 40k 10'th edition."
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.apache.commons/commons-statistics-descriptive "1.1"]
                 [org.apache.commons/commons-statistics-distribution "1.1"]
                 [org.apache.commons/commons-statistics-inference "1.1"]
                 [org.clojure/math.combinatorics "0.3.0"]
                 ]
  :main ^:skip-aot kilithikadya.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
