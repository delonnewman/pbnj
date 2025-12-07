(ns pbnj.paths
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(deftype Path [name])

(def str->path ->Path)
(def path-reader str->path)

(defn path? [p]
  (instance? Path p))

(defn root? [p] (= (.name p) "/"))

(defn- with-ext
  [file ext]
  (let [ext
        (if (str/starts-with? ext ".")
          (.substring ext 1)
          ext)]
    (str file "." ext)))

(comment
  (with-ext "welcome" "html")
  (with-ext "welcome" ".html")
  )

(defn path->file
  ([path]
   (io/as-file (.name path)))
  ([parent path]
   (io/file parent (.name path)))
  ([parent path ext]
   (io/file parent (with-ext (.name path) ext))))

(defn exists?
  ([path]
   (.exists (path->file path)))
  ([parent path]
   (.exists (path->file parent path)))
  ([parent path ext]
   (.exists (path->file parent path ext))))

(defn fetch
  ([path]
   (when (exists? path)
     (path->file path)))
  ([parent path]
   (when (exists? parent path)
     (path->file parent path)))
  ([parent path ext]
   (when (exists? parent path ext)
     (path->file parent path ext))))

(defn parts [path]
  (if (root? path)
    [""]
    (str/split (.name path) #"/")))

(defn locate
  [path & {:keys [parents formats]}]
  (->>
   (map
    #(path->file %1 path (name %2))
    parents formats)
   #_(filter java.io.File/.exists)))

(comment
  (fetch #pbnj/path "test/resources/welcome/index.html")
  (fetch #pbnj/path "test/resources/welcome/index.php")
  (fetch "test/resources" #pbnj/path "welcome/index" "html")

  (locate #pbnj/path "welcome/index" :parents #{"test/resources"} :formats #{:html :php :js})

  (zipmap #{1} #{:a :b :c})
  )
