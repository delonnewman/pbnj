(ns pbnj.paths-test
  (:require
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [pbnj.paths :as path]))

(deftest locate-test
  (let [expected (io/file "test/resources/welcome/index.html")
        actual (->
                #pbnj/path "welcome/index"
                (path/locate :parents #{"test/resources"} :formats #{:html})
                first)]
    (is (= expected actual))))
