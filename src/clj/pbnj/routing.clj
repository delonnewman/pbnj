(ns pbnj.routing)

(defn page
  [path
   & {:keys [name to via formats]
      :or {via :get formats #{:html}}}]
  {:route/path path
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

(comment
  (page "/" :name "root" :to #pbnj/path "welcome/index")

  (let [r (page "/" :name "root" :to #pbnj/path "welcome/index")]
    (route? r))

  (routes
   (page "/" :name "root" :to #pbnj/path "welcome/index")
   (page "/entities" :name "entities" :to #pbnj/path "entities/list")
   (page "/entities/:id" :name "entities" :to #pbnj/path "entities/show")
   (page "/entities/:id" :name "entities" :to #pbnj/path "entities/update" :via :post))
  )
