(ns pbnj.paths-test
  (:require
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [pbnj.paths :as path]))

(def root-path #path "welcome#index")

(deftest path-test
  (let [metadata {:doc "Hey"}
        path (path/path "welcome" "index")]
    (is (= path root-path))
    (is (= {} (meta path))
    (is (= metadata (meta (path/path "welcome" "index" metadata)))))))

(deftest with-ext-test
  (let [ext "html"]
    (is (= ext (-> root-path (path/with-ext ext) path/path-ext)))))

(deftest with-formats-test
  (let [formats #{:html :json}]
    (is (= formats (-> root-path (path/with-formats formats) path/path-formats)))))

(deftest with-parents-test
  (let [parents #{"test/resources"}]
    (is (= parents (-> root-path (path/with-parents parents) path/path-parents)))))

(deftest locate-test
  (let [expected (io/file "test/resources/welcome/index.html")
        actual (-> root-path
                   (path/locate :parents #{"test/resources"} :formats #{:html})
                   first)]
    (is (= expected actual))))

(deftest path->file-test
  (let [expected (io/file "test/resources/welcome/index.html")
        base     #path "test/resources/welcome#index"]
    (testing "path only"
      (is (= expected (path/path->file (path/with-ext base "html")))))
    (testing "path and extension"
      (is (= expected (path/path->file base "html"))))
    (testing "path and extension with a leading dot"
      (is (= expected (path/path->file base ".html"))))
    (testing "parent, path and extension"
      (let [file (path/path->file "test/resources" root-path "html")]
        (is (= expected file))))))

(deftest exists?-test
  (testing "path exists"
    (is (path/exists? #path "test/resources/welcome#index" "html"))
    (is (path/exists? "test/resources" root-path "html")))
  (testing "path doesn't exist"
    (is (not (path/exists? #path "test/resources/welcome#index" "php")))
    (is (not (path/exists? "test/resources" root-path "php")))))

