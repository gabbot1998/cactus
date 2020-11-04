;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

(ns cactus.core
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! <! >!! <!! go chan buffer close! thread] ]))

(def chan-1 (async/chan))
(def chan-2 (async/chan))

(defn score [a b]
  (if (= a "") 0
    (if (= b "") 0
      (if (= a b) 5 -1 )
    )
  )
)

(def penalty 1)

(defn cell-action [nw n a b]
  (max
   (+ nw (score a b))
   (+ a (penalty))
   (+ b (penalty))
   0)
 )

(defn sw-cell [a b w]
  (let [nw (atom 0)  n (atom 0)]
  (go
    (while true
      (set! n  (cell-action nw n (<!! a) (<!! b)))
      (set! nw (<!! w))
  ))))



(defn -main  [& args]
  )
