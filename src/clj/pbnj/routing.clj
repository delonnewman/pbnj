(ns pbnj.routing
  (:require [pbnj.paths :as path]
            [clojure.string :as str]))

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

(def route-methods #{:get :post :put :delete})

(defn route->tree-node [r]
  (if-let [method (route-methods (:route/method r))]
    (let [key (keyword "route.tree" (name method))]
      {key (:route/src r)})
    (throw (Error. "Invalid method"))))

(defn path-parts
  [str]
  (if (= str "/")
    [""]
    (str/split str #"/")))

(defn route-tree [rs]
  (case (count rs)
    0 nil
    :else
    (let [nodes (map (juxt :route/path route->tree-node) rs)
          index (->> nodes (group-by (fn [[path _]] (path-parts path))))]
    index
    )))

(comment
  (route->tree-node (page "/" :name "root" :to #pbnj/path "welcome/index"))
  (route-tree #{(page "/" :name "root" :to #pbnj/path "welcome/index")})

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
