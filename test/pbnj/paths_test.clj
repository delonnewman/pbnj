(ns pbnj.paths-test
  (:require
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [pbnj.paths :as path]))

(deftest locate-test
  (let [expected (io/file "test/resources/welcome/index.html")
        actual (-> #pbnj/path "welcome/index"
                   (path/locate :parents #{"test/resources"} :formats #{:html})
                   first)]
    (is (= expected actual))))

(deftest path->file-test
  (let [expected (io/file "resources/welcome/index.html")]
    (testing "just path"
      (is (= expected (path/path->file #pbnj/path "resources/welcome/index.html"))))
    (testing "parent and path"
      (is (= expected (path/path->file "resources" #pbnj/path "welcome/index.html"))))
    (testing "parent, path and extension"
      (let [file (path/path->file "resources" #pbnj/path "welcome/index" "html")]
        (is (= expected file))))
    (testing "parent, path and extension with a leading dot"
      (let [file (path/path->file "resources" #pbnj/path "welcome/index" ".html")]
        (is (= expected file))))))
