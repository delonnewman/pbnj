(ns pbnj.routing-test
  (:require [pbnj.routing :as r]
            [clojure.test :refer :all]))

(deftest- route-tree-test
  (testing "simple"
    (let [tree
          (r/route-tree
           (r/routes
            (r/page "/" :to #pbnj/path "welcome/index" :name "root")))]
      (is (= tree {"" {:route.tree/get "resources/welcome/index.html"}})))))
