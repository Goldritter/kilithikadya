(ns kilithikadya.csv
  (:require [clojure.data.csv :as csv]
           [clojure.java.io :as io]))

(defn write-csv [path columns row-data & {:keys [separator] :or {separator \;} }]
  (let [headers (map name columns)
        rows (mapv #(mapv % columns) row-data)]
    (with-open [file (io/writer path)]
      (csv/write-csv file (cons headers rows) :guote? true :separator separator))))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn read-csv->maps [path & {:keys [separator] :or {separator \;} }]
  (csv-data->maps (csv/read-csv (io/reader path) :separator separator)))