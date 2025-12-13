(ns site
  (:require [pbnj.site :as s]
            [pbnj.routing :as r]))

(def site (s/create-basis :site "site.edn"))

(def routes
  (r/routes
   (r/page "/" :to #path "contacts#new")
   (r/resource "contacts")
   ))

(defn build []
  (s/build site))
