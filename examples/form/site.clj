(ns site
  (:require [pbnj.site :as s]
            [pbnj.routing :as r]))

(def site (s/create-basis :site "site.edn"))

(def routes
  (r/routes
   (r/route "/" :to #path "contacts#new", :via #{:get})
   (r/route "/" :to #path "contacts#create" :via #{:post})))

(defn build []
  (s/build site))
