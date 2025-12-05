(ns pbnj.paths)

(deftype Path [name])

(defn path-reader [string]
  (Path. string))
