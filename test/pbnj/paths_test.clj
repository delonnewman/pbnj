(ns pbnj.paths-test
  (:require
   [clojure.test :refer :all]
   [clojure.java.io :as io]
   [pbnj.paths :as path]))

(deftest locate-test
  (let [expected (io/file "test/resources/welcome/index.html")
        actual (-> #path "welcome#index"
                   (path/locate :parents #{"test/resources"} :formats #{:html})
                   first)]
    (is (= expected actual))))

(deftest path->file-test
  (let [expected (io/file "test/resources/welcome/index.html")]
    (testing "path and extension"
      (is (= expected (path/path->file #path "test/resources/welcome#index" "html"))))
    (testing "path and extension with a leading dot"
      (let [file (path/path->file #pbnj/path "test/resources/welcome#index" ".html")]
        (is (= expected file))))
    (testing "parent, path and extension"
      (let [file (path/path->file "test/resources" #pbnj/path "welcome#index" "html")]
        (is (= expected file))))))

(deftest exists?-test
  (testing "path exists"
    (is (path/exists? #path "test/resources/welcome#index" "html"))
    (is (path/exists? "test/resources" #path "welcome#index" "html")))
  (testing "path doesn't exist"
    (is (not (path/exists? #path "test/resources/welcome#index" "php")))
    (is (not (path/exists? "test/resources" #pbnj/path "welcome#index" "php")))))

(deftest path-test
  (is (= (path/path "welcome" "index") #path "welcome#index"))
  (let [metadata {:doc "Hey"}]
    (is (= metadata (meta (path/path "welcome" "index" metadata))))))

(deftest with-ext-test
  (let [root #path "welcome#index"]
    (is (= "html" (path/path-ext (path/with-ext root "html"))))))

(deftest with-formats-test
  (let [root #path "welcome#index"
        formats #{:html :json}]
    (is (= formats (path/path-formats (path/with-formats root formats))))))
