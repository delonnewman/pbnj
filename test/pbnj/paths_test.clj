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
  (let [expected (io/file "test/resources/welcome/index.html")]
    (testing "just path"
      (is (= expected (path/path->file #pbnj/path "test/resources/welcome/index.html"))))
    (testing "parent and path"
      (is (= expected (path/path->file "test/resources" #pbnj/path "welcome/index.html"))))
    (testing "parent, path and extension"
      (let [file (path/path->file "test/resources" #pbnj/path "welcome/index" "html")]
        (is (= expected file))))
    (testing "parent, path and extension with a leading dot"
      (let [file (path/path->file "test/resources" #pbnj/path "welcome/index" ".html")]
        (is (= expected file))))))

(deftest exists?-test
  (testing "path exists"
    (is (path/exists? #pbnj/path "test/resources/welcome/index.html"))
    (is (path/exists? "test/resources" #pbnj/path "welcome/index.html"))
    (is (path/exists? "test/resources" #pbnj/path "welcome/index" "html")))
  (testing "path doesn't exist"
    (is (not (path/exists? #pbnj/path "test/resources/welcome/index.php")))
    (is (not (path/exists? "test/resources" #pbnj/path "welcome/index.php")))
    (is (not (path/exists? "test/resources" #pbnj/path "welcome/index" "php")))))
