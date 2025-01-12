(defproject com.github.goldritter/kilithikadya "0.1.1-a.8"
  :description "Probability calculation application for Warhammer 40k 10'th edition."
  :url "https://github.com/Goldritter/kilithikadya"
  :license {:name "MIT"
            :url "https://choosealicense.com/licenses/mit"
            :comment "MIT License"
            :year 2025
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.apache.commons/commons-statistics-descriptive "1.1"]
                 [org.apache.commons/commons-statistics-distribution "1.1"]
                 [org.apache.commons/commons-statistics-inference "1.1"]
                 [org.clojure/math.combinatorics "0.3.0"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [cheshire "5.13.0"]
                 [org.apache.commons/commons-math3 "3.6.1"]
                 [org.clojure/data.csv "1.0.0"]

                 ;; Logging
                 [org.slf4j/slf4j-api "1.7.35"]
                 [org.slf4j/slf4j-simple "1.7.35"]
                 [org.apache.logging.log4j/log4j-core "2.17.0"]
                 [org.apache.logging.log4j/log4j-api "2.17.0"]
                 [org.clojure/tools.logging "1.2.4"]

                 ]
  :plugins        [[cider/cider-nrepl "0.50.2"]
                   [lein-license "1.0.0"]]
  :main ^:skip-aot kilithikadya.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
