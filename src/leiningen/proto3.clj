(ns leiningen.proto3
  (:require [clojure.java.io :as io]
            [leiningen.javac :refer [javac]]
            [leiningen.core.main :as main :refer [info]])
  (:import [com.github.os72.protocjar Protoc]))

(defn exit [message]
  (info message)
  (System/exit 1))

(defn target [project] (-> project :target-path io/file))
(defn dest [t] (io/file t "protosrc"))

(defn proto-path [project]
  (mapv #(.getAbsolutePath %)
        (-> project
            (get :proto-path "resources/proto")
            io/file
            file-seq)))

(defn setup-paths! [& args]
  (doseq [f args] (.mkdirs f)))

(defn proto3 [project & args]
  (info "Running proto3 ...")
  (let [target-dir (target project)
        dest-dir   (dest target-dir)
        [dir & proto-files] (proto-path project)]

    (setup-paths! target-dir dest-dir)

    (doseq [pf (filter #(.endsWith % ".proto") proto-files)]
      (info "Path to proto file:" pf)
      (Protoc/runProtoc (into-array String
                                    ["--include_std_types"
                                     (str "--proto_path=" dir)
                                     (str "--java_out=" (.getPath dest-dir))
                                     pf])))
    (javac (-> project
               (assoc :auto-clean false)
               (update-in [:java-source-paths] concat [(.getPath dest-dir)])
               (update-in [:javac-options] concat ["-Xlint:none"])))
    (-> project
               (assoc :auto-clean false)
               (update-in [:java-source-paths] concat [(.getPath dest-dir)])
               (update-in [:javac-options] concat ["-Xlint:none"]))))
    ;(exit "Finished running proto3")))

