(ns site
  (:require [pbnj.site :as s]
            [pbnj.routing :as r]))

(def site (s/create-basis :site "site.edn"))

(def routes
  (r/routes
   (r/get "/" :to #path "contacts#new")
   (r/post "/" :to #path "contacts#create")))

(defn build []
  (s/build site))
