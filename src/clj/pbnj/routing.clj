(ns pbnj.routing
  (:require [pbnj.paths :as path]))

(defn page
  [path
   & {:keys [name to via formats]
      :or {via :get formats #{:html}}}]
  {:route/path (path/str->path path)
   :route/src to
   :route/name name
   :route/method via
   :route/formats formats})

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

(defn route-tree [rs]
  (let [index (group-by #(-> (:route/path %) path/parts) rs)
        keys (sort-by count (keys index))]
    (reduce
     (fn [tree parts]
       (assoc-in tree parts {}))
     {} keys)
    ))

(comment
  (page "/" :name "root" :to #pbnj/path "welcome/index")

  (let [r (page "/" :name "root" :to #pbnj/path "welcome/index")]
    (route? r))

  (route-tree
   (routes
    (page "/" :name "root" :to #pbnj/path "welcome/index")
    (page "/entities" :name "entities" :to #pbnj/path "entities/list")
    (page "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/show")
    (page "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/update" :via :post))
   )
  )
