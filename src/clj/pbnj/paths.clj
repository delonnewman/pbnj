(ns pbnj.paths
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(deftype Path [name])

(def str->path ->Path)
(def path-reader str->path)

(defn path? [p]
  (instance? Path p))

(defn path->file
  ([path] (io/as-file (.name path)))
  ([parent path] (io/file parent (.name path)))
  ([parent path ext]
   (let [ext (if (str/starts-with? ext ".") (.substring ext 1) ext)]
     (io/file parent (str (.name path) "." ext)))))

(defn exists?
  ([path] (.exists (path->file path)))
  ([parent path] (.exists (path->file parent path)))
  ([parent path ext] (.exists (path->file parent path ext))))

(defn parts [path]
  (if (= (.name path) "/")
    [""]
    (str/split (.name path) #"/")))

(comment
  (path->file "resources" #pbnj/path "welcome/index" "html")
  (path->file "resources" #pbnj/path "welcome/index" ".html")
  (exists? "src/clj" #pbnj/path "pbnj/paths" "clj")
  (exists? "src/clj" #pbnj/path "pbnj/paths" "js")
  (path? #pbnj/path "entities/list")
  (path? "hey")
  (parts #pbnj/path "entities/list")
  (parts #pbnj/path "/")
  )
