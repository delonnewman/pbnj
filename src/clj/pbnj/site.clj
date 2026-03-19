(ns pbnj.site
  (:require [clojure.java.io :as io]))

(defn create-basis [& {:keys [dir]}]
  (let [f (io/file dir)]
    (if (.exists f)
      {:site/root-dir (.getAbsoluteFile f)}
      (throw (ex-info "Invalid directory" { :dir dir })))))

(defn build [site])
