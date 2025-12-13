(ns pbnj.routing
  (:require [pbnj.paths :as path]
            [clojure.string :as str]))

(defn route
  [path
   & {:keys [name to via formats]
      :or {via :get formats #{:html}}}]
  #:route{:path path
          :src to
          :name name
          :methods via
          :formats formats})

(defn route? [r]
  (and
   (map? r)
   (contains? r :route/path)
   (contains? r :route/src)
   (contains? r :route/method)
   (contains? r :route/formats)))

(defn routes
  ([r] (if (route? r) #{r} r))
  ([r & rs] (set (concat (routes r) rs))))

(comment
  (let [r (route "/" :name "root" :to #pbnj/path "welcome/index")]
    (route? r))

   (routes
    (route "/" :name "root" :to #pbnj/path "welcome/index")
    (route "/entities" :name "entities" :to #pbnj/path "entities/list")
    (route "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/show")
    (route "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/update" :via :post))
  )
