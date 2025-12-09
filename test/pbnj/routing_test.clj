(ns pbnj.routing-test
  (:require [pbnj.routing :as r]
            [clojure.test :refer :all]))

(deftest- route-tree-test
  (testing "simple"
    (let [tree
          (r/route-tree
           (r/routes
            (r/page "/" :to #path "welcome#index" :name "root")))]
      (is (= tree {"" {:route.tree/get #path "welcome#index"}})))))
