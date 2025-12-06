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
   (contains? r :route/method)))

(comment
  (page "/" :name "root" :to #pbnj/path "welcome/index")
  (let [r (page "/" :name "root" :to #pbnj/path "welcome/index")]
    (route? r))
  )
