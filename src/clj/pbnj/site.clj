(ns pbnj.site
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def ^:dynamic *config-file* "site.edn")

(defn create-basis [& {:keys [dir]}]
  (let [f (-> dir io/file .getAbsoluteFile)]
    (if-not (.exists f)
      (throw (ex-info "Invalid directory" { :dir dir }))
      (let [base (-> f (io/file *config-file*) .getAbsoluteFile)]
        (if-not (.exists base)
          #:site{:root-dir f}
          (merge
           #:site{:root-dir f, :config base}
           (edn/read-string (slurp base))))))))

(defn build [site])
