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

(defn sw-cell [a b v w]
  (let [nw (atom 0)  n (atom 0)]
  (go
    (while true
      (set! n  (cell-action nw n (<!! a) (<!! b)))
      (set! nw (<!! w))
      (>!! v n)
  ))))

(defn controller [A B c1 c2 c3 c4 c5 ] ;;c5 is chanel to send b
      (for (range (+ (size B)))
        (>!! c5 b)
        )
      (>!! c1 "")
      (>!! c2 (nth A 0))
      (>!! c3 (nth A 1))
      (>!! c4 (nth A 2))
    )
 )


(def chan-controller-1 (async/chan))
(def chan-controller-2 (async/chan))
(def chan-controller-3 (async/chan))
(def chan-controller-4 (async/chan))
(def chan-1-2 (async/chan))
(def chan-2-3 (async/chan))
(def chan-3-4 (async/chan))
(def chan-4-aligner (async/chan))

(defn -main  [& args]

  )
