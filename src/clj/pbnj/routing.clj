(ns pbnj.routing
  (:refer-clojure :exclude [get])
  (:require [pbnj.paths :as path :refer [path]]
            [clojure.string :as str]))

(defn route
  [path
   & {:keys [name to via formats]
      :or {via #{:get} formats #{:html}}}]
  #:route{:path path
          :src to
          :name name
          :methods via
          :formats formats})

(defmacro ^:private router-form [method]
  `(def ~(symbol (name method))
     (fn [path# & opts#]
       (apply route (cons path# (cons :via (cons #{~method} opts#)))))))

(router-form :get)
(router-form :post)
(router-form :put)
(router-form :delete)

(comment
  (macroexpand '(router-form :get))
  (get "/" :to #path "welcome#index" :name "hey")
  )

(defn route? [r]
  (and
   (map? r)
   (contains? r :route/path)
   (contains? r :route/src)
   (contains? r :route/methods)
   (contains? r :route/formats)))

(defn routes
  ([r] (if (route? r) #{r} r))
  ([r & rs] (set (concat (routes r) rs))))

(defn resources
  ([key]
   (let [plural (name key) singular plural]
     (routes
      (get (str "/" plural)
           :to (path plural "list")
           :name plural)
      (get (str "/" plural "/new")
           :to (path plural "new")
           :name (str "new_" singular))
      (post (str "/" plural)
            :to (path plural "create")
            :name plural)
      (get (str "/" plural "/:id")
           :to (path plural "show")
           :name (str "new_" singular))
      (get (str "/" plural "/:id/edit")
           :to (path plural "edit")
           :name (str "edit_" singular))
      (route (str "/" plural "/:id")
             :to (path plural "edit")
             :name (str "update_" singular)
             :via #{:post :put})
      (delete (str "/" plural "/:id")
              :to (path plural "remove")
              :name (str "delete_" singular)))))
   ([key & keys]
    (routes
     (resources key)
     (apply routes (map resources keys)))))


(comment
  (resources :photos :recordings)
  
  (let [r (route "/" :name "root" :to #pbnj/path "welcome/index")]
    (route? r))

   (routes
    (route "/" :name "root" :to #pbnj/path "welcome/index")
    (route "/entities" :name "entities" :to #pbnj/path "entities/list")
    (route "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/show")
    (route "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/update" :via :post))
  )
