(ns pbnj.paths
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(deftype Path [name])

(def str->path ->Path)
(def path-reader str->path)

(defn path? [p]
  (instance? Path p))

(defn root? [p] (= (.name p) "/"))

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
  (if (root? path)
    [""]
    (str/split (.name path) #"/")))

(defn locate
  [path & {:keys [parents formats]}]
  (->>
   (map
    (fn [parent format]
      (path->file parent path (name format)))
    parents formats)
   (filter java.io.File/.exists)))

(comment
  (exists? "src/clj" #pbnj/path "pbnj/paths" "clj")
  (exists? "src/clj" #pbnj/path "pbnj/paths" "js")

  (path? #pbnj/path "entities/list")
  (path? "hey")

  (parts #pbnj/path "entities/list")
  (parts #pbnj/path "/")

  (locate #pbnj/path "welcome/index" :parents #{"test/resources"} :formats #{:html})
  (io/file "test/resources/welcome/index.html")
  )
