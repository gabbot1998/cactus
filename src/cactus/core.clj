;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

(ns cactus.core
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread] ]))

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
  (while true (loop
      [nw 0 n 0]
      (do
        (println "asadsdasds")
        (>!! v n)
        (println "hejejej")
        (recur (<!! w) (cell-action nw n (<!! a) (<!! b)))
        )
      )
    )
  )


(defn print-actor [chan]
    (go (while true
      (println (<!! chan))
    ))
  )


(defn controller [A B c1 c2 c3 c4 c5] ;;c5 is chanel to send b
      (doseq [i (range 4)]
      (do
        (if (= i 0) (>!! c5 "") (>!! c5 (nth B (- i 1))))
        (>!! c1 "")
        (>!! c2 (nth A 0))
        (>!! c3 (nth A 1))
        (>!! c4 (nth A 2))
      )
    )
 )


(def chan-con-1 (chan 10))
(def chan-con-2 (async/chan 10))
(def chan-con-3 (async/chan 10))
(def chan-con-4 (async/chan 10))
(def chan-con-5 (async/chan))
(def chan-1-2 (async/chan))
(def chan-2-3 (async/chan))
(def chan-3-4 (async/chan))
(def chan-4-aligner (async/chan))

(defn -main  [& args]
  (print-actor chan-con-3)
  (sw-cell chan-con-1 chan-con-2 chan-con-3 chan-con-4)
  (while true (do
    (>!! chan-con-1 "a")
    (>!! chan-con-2 "a")
    (>!! chan-con-4 5)
  ))



 )

  ;;(>!! chan-con-1 "wow")
  ;;(doseq [i (range 5)] (do (>!! chan-con-1 "m") (>!! chan-con-2 "s") (>!! chan-con-3 "d") (>!! chan-con-4 "a") (>!! chan-con-5 "v")))


  ;;(controller "abc" "def" chan-con-1 chan-con-2 chan-con-3 chan-con-4 chan-con-5)
