(ns pbnj.util
  (:require [clojure.java.io :as io]))

(defn current-dir []
  (when-let [f *file*]
    (-> f io/file .getAbsoluteFile .getParent)))

(comment
  (current-dir)
  )
