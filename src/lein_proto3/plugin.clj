(ns lein-proto3.plugins
  (:require [leiningen.proto3 :refer [proto3]]
            [leiningen.javac :refer [javac]]
            [robert.hooke :refer [add-hook]]))

(defn hooks []
  (add-hook #'javac
            (fn [f & args]
              (proto3 (first args))
              (apply f args))))