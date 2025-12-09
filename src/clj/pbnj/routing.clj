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

(defn route-method [r]
  (if-let [method (route-methods (:route/method r))]
    method
    (throw (Error. "Invalid method"))))

(defn path-parts
  [r]
  (let [path (:route/path r)]
    (if (= path "/")
      [""]
      (str/split path #"/"))))

(defn route->tree-node [r]
  (let [method (route-method r)
        parts (path-parts r)
        key (keyword "route.tree" (name method))]
    {(first parts) {key (:route/src r)}}
    ))

(defn route-tree [rs]
  (case (count rs)
    0 {}
    (route->tree-node (first rs))
  ))


(comment
  (route->tree-node (page "/" :name "root" :to #pbnj/path "welcome/index"))

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
