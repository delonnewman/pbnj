(ns pbnj.paths
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(deftype Path [name])

(defn path-reader [string]
  (Path. string))

(defn path? [p]
  (instance? Path p))

(defn path->file
  ([path] (io/as-file (.name path)))
  ([parent path] (io/file parent (.name path)))
  ([parent path ext]
   (let [ext (if (str/starts-with? ext ".") (.substring ext 1) ext)]
     (io/file parent (str (.name path) "." ext)))))

(defn path-exists?
  ([path] (.exists (path->file path)))
  ([parent path] (.exists (path->file parent path)))
  ([parent path ext] (.exists (path->file parent path ext))))

(comment
  (path->file "resources" #pbnj/path "welcome/index" "html")
  (path->file "resources" #pbnj/path "welcome/index" ".html")
  (path-exists? "src/clj" #pbnj/path "pbnj/paths" "clj")
  (path? #pbnj/path "entities/list")
  (path? "hey")
  )
