(ns pbnj.paths
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(deftype Path [namespace name metadata]
  clojure.lang.Named
  (getName [this] (.name this))
  (getNamespace [this] (.namespace this))

  clojure.lang.IObj
  (meta [this] (.metadata this))
  (withMeta [this meta]
    (if (= meta (.metadata this))
      this
      (->Path (.namespace this) (.name this) meta)))

  Comparable
  (compareTo [this other]
    (if (.equals this other)
      0
      (let [nsc (.compareTo (.namespace this) (.namespace other))]
        (if (not= 0 nsc)
          nsc
          (.compareTo (.name this) (.name other))))))

  Object
  (toString [this]
    (str (.namespace this) "#" (.name this)))
  (equals [this other]
    (and
     (= (.namespace this) (.namespace other))
     (= (.name this) (.name other))))
  (hashCode [this]
    (hash-combine
     (hash (.namespace this))
     (hash (.name this)))))

(defn str->path
  [str]
  (let [[ns name] (str/split str #"#")]
    (->Path ns name {})))

(comment
  (name (->Path "welcome" "index" {}))
  (namespace (->Path "welcome" "index" {}))
  (meta (->Path "welcome" "index" {}))

  (meta (str->path "welcome#index"))
  (with-meta (str->path "welcome#index") {:doc "Hey"})
  (meta ^{:doc "Hey"} #path "welcome#index")

  (compare #path "entities#create" #path "entities#new")
  (str (->Path "welcome" "index" {}))
  (str (str->path "welcome#index"))
)

(def path-reader str->path)

(defn path? [p]
  (instance? Path p))

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

(defn path->fs-path
  "Convert a path into a file system path"
  ([path] (str (namespace path) "/" (name path)))
  ([path ext] (with-ext (path->fs-path path) ext)))

(defn path->symbol
  "Convert a path into a symbol"
  [path]
  (let [ns (str/replace (namespace path) #"/" ".")]
    (symbol ns (name path))))

(def path->var (comp find-var path->symbol))

(comment
  (path->fs-path (str->path "welcome#index"))
  (path->fs-path (str->path "welcome#index") "html")
  #path "welcome#index"
  (path->symbol #path "pbnj/paths#str->path")
  (path->var #path "pbnj/paths#str->path")
  )

(defn path->file
  ([path ext]
   (io/file (path->fs-path path ext)))
  ([parent path ext]
   (io/file parent (path->fs-path path ext))))

(defn exists?
  ([parent ext]
   (.exists (path->file parent ext)))
  ([parent path ext]
   (.exists (path->file parent path ext))))

(defn fetch
  ([path ext]
   (when (exists? path ext)
     (path->file path ext)))
  ([parent path ext]
   (when (exists? parent path ext)
     (path->file parent path ext))))

(defn locate
  [path & {:keys [parents formats]}]
  (->>
   formats
   (mapcat
    (fn [format]
      (map
       (fn [parent]
         (path->file parent path (name format))) parents)))
   (filter java.io.File/.exists)))

(comment
  (fetch #pbnj/path "test/resources/welcome#index" "html")
  (fetch #pbnj/path "test/resources/welcome#index" "php")
  (fetch "test/resources" #pbnj/path "welcome#index" "html")

  (locate #path "welcome#index" :parents #{"test/resources"} :formats #{:html :php :js})

  (= #path "welcome#index" #path "welcome#index")
  )
