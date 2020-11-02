
(ns cactus.core
  (:require [clojure.core.async :as async])

  (:gen-class))


(defn action [arg]
        (+ 3 arg))

(defn actor []

  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (action 3)))
