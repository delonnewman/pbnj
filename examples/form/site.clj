(ns site
  (:require [pbnj.site :as s]
            [pbnj.routing :as r]
            [pbnj.util :as util]))

(def site
  (->
   (s/create-basis :dir (util/current-dir))
   (assoc
    :site/routes
    (r/routes
     (r/get "/" :to #path "contacts#new")
     (r/post "/" :to #path "contacts#create")))))

(defn build []
  (s/build site))

(comment
  *file*
  site
  )
