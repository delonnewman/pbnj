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

(comment
  (macroexpand '(router-form :get))
  (route "/" :via #{:get} :to #path "welcome#index" :name "hey")
  )

(defn route? [r]
  (and
   (map? r)
   (contains? r :route/path)
   (contains? r :route/src)
   (contains? r :route/methods)
   (contains? r :route/formats)))

(defn routes
  ([r] (if (route? r) (list r) r))
  ([r & rs] (concat (routes r) rs)))

(defn resources
  ([key]
   (let [plural (name key) singular plural]
     (routes
      (route
       (str "/" plural)
       :via #{:get}
       :to (path plural "list")
       :name plural)
      (route
       (str "/" plural "/new")
       :via #{:get}
       :to (path plural "new")
       :name (str "new_" singular))
      (route
       (str "/" plural)
       :via #{:post}
       :to (path plural "create")
       :name plural)
      (route
       (str "/" plural "/:id")
       :via #{:get}
       :to (path plural "show")
       :name (str "new_" singular))
      (route
       (str "/" plural "/:id/edit")
       :via #{:get}
       :to (path plural "edit")
       :name (str "edit_" singular))
      (route
       (str "/" plural "/:id")
       :to (path plural "edit")
       :name (str "update_" singular)
       :via #{:post :put})
      (route
       (str "/" plural "/:id")
       :via #{:delete}
       :to (path plural "remove")
       :name (str "delete_" singular)))))
   ([key & keys]
    (apply
     routes
     (resources key)
     (mapcat resources keys))))


(comment
  (resources :photos :recordings)

  (resources :photos)
  (resources :recordings)

  (routes
   (route "/" :name "root" :to #pbnj/path "welcome/index")
   (list (route "/" :name "root" :to #pbnj/path "welcome/index" :via #{:post}))
   )

  (mapcat resources [:photos :recordings])

  (let [r (route "/" :name "root" :to #pbnj/path "welcome/index")]
    (route? r))

   (routes
    (route "/" :name "root" :to #pbnj/path "welcome/index")
    (route "/entities" :name "entities" :to #pbnj/path "entities/list")
    (route "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/show")
    (route "/entities/:entity_id" :name "entities" :to #pbnj/path "entities/update" :via :post))
  )
