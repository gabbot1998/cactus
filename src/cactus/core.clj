
(ns cactus.core
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout put!]]))
(defn action [arg state]
        (+ arg state))

(def chan-1 (async/chan))
(def chan-2 (async/chan))

(defn actor [chan-1 chan-2]
  (go (>!! chan-2 (<!! chan-1)))
 )

(defn print-actor [chan-2]
    (go (println (<! chan-2)))
 )


(defn -main  [& args]
  (while true
  (actor chan-1 chan-2)
  (print-actor chan-2)
  (>!! chan-1 (Integer/parseInt (read-line) )
  )))
