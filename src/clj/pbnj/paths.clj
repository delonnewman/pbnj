(ns pbnj.paths
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(deftype Path [ns name metadata]
  clojure.lang.Named
  (getName [this] (.name this))
  (getNamespace [this] (.ns this))

  clojure.lang.IObj
  (meta [this] (.metadata this))
  (withMeta [this meta]
    (if (= meta (.metadata this))
      this
      (Path. (.ns this) (.name this) meta)))

  Comparable
  (compareTo [this other]
    (if (.equals this other)
      0
      (let [nsc (.compareTo (.ns this) (.ns other))]
        (if (not= 0 nsc)
          nsc
          (.compareTo (.name this) (.name other))))))

  Object
  (toString [this]
    (str (.ns this) "#" (.name this)))
  (equals [this other]
    (and
     (= (.ns this) (.ns other))
     (= (.name this) (.name other))))
  (hashCode [this]
    (hash-combine
     (hash (.ns this))
     (hash (.name this)))))

(defn path
  "Return a path with a namespace and a name and optional metadata map."
  ([ns name] (->Path ns name {}))
  ([ns name meta] (->Path ns name meta)))

(defn path?
  "Return true if the value is a path, otherwise return false."
  [p]
  (instance? Path p))

(defn str->path
  "Parse a path from a string with the format 'namespace#name'."
  [str]
  (let [[ns name] (str/split str #"#")]
    (path ns name)))

(def path-reader str->path)

(defn- meta-reader
  [key]
  (fn [path]
    (-> path meta key)))

(defn- meta-writer
  [key]
  (fn [path value]
    (with-meta path (assoc (meta path) key value))))

(def with-ext (meta-writer ::ext))
(def path-ext (meta-reader ::ext))

(def with-parents (meta-writer ::parents))
(def path-parents (meta-reader ::parents))

(def with-formats (meta-writer ::formats))
(def path-formats (meta-reader ::formats))

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

(defn path->symbol
  "Convert a path into a symbol"
  [path]
  (let [ns (str/replace (namespace path) #"/" ".")]
    (symbol ns (name path))))

(def ^{:doc "Convert a path into a var"}
  path->var (comp find-var path->symbol))

(defn- join-ext
  [file ext]
  (let [ext
        (if (str/starts-with? ext ".")
          (.substring ext 1)
          ext)]
    (str file "." ext)))

(comment
  (join-ext "welcome" "html")
  (join-ext "welcome" ".html")
  )

(defn path->fs-path
  "Convert a path into a file system path"
  ([path]
   (let [base (str (namespace path) "/" (name path))
         ext  (path-ext path)]
     (if ext (join-ext base ext) base)))
  ([path ext] (path->fs-path (with-ext path ext))))

(comment
  (path->fs-path (str->path "welcome#index"))
  (path->fs-path (str->path "welcome#index") "html")
  #path "welcome#index"
  (path->symbol #path "pbnj/paths#str->path")
  (path->var #path "pbnj/paths#str->path")
  )

(defn path->file
  ([path]
   (io/file (path->fs-path path)))
  ([path ext]
   (io/file (path->fs-path path ext)))
  ([parent path ext]
   (io/file parent (path->fs-path path ext))))

(defn exists?
  ([path]
   (.exists (path->file path)))
  ([path ext]
   (.exists (path->file path ext)))
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
  [path & {:keys [parents formats]
           :or {parents (path-parents path)
                formats (path-formats path)}}]
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
