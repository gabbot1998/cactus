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

(def penalty 0)

(defn cell-action [nw n a b]
  (max
   (+ nw (score a b))
   (+ nw penalty)
   (+ n penalty)
   0)
 )

 (defn sw-cell [a b w v]
    (go
      (loop [nw 0 n 0];;Set initial state
        (let [new-a (<!! a) new-b (<!! b) new-w (<!! w)] ;;Wait for ports
          (let [new-nw new-w new-n (cell-action nw n new-a new-b)] ;;Assign new local state and execute body
            (>!! v new-n);;Set output
            (recur new-nw new-n) ;;Recur
          )
        )
      )
    )
  )

(defn print-actor [chan]
    (go
      (loop [];;Set initial state
        (let [new-str (<!! chan) ];;Wait for ports
          (let [] ;;Assign new local state and execute body
            (println new-str)
            ;;Set output
            (recur );;Recur
            )
          )
        )
      )
  )


(defn controller [A B c1 c2 c3 c4 c5 w] ;;c5 is chanel to send b
      (go
        (loop [A-str "" B-str ""];;Set initial state
          (let [new-A (<!! A) new-B (<!! B)];;Wait for ports
            (let [new-A-str new-A
                  new-B-str new-B
                  ] ;;Assign new local state and execute body
              (do
                (doseq [b B]

                  )
                (>!! )
                )


            )
          )

          )
        )
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
(def chan-con-1b (chan 10))
(def chan-con-1-zero (chan 10))
(def chan-con-2 (async/chan 10))
(def chan-con-2b (async/chan 10))
(def chan-con-3 (async/chan 10))
(def chan-con-3b (async/chan 10))
(def chan-con-4 (async/chan 10))
(def chan-con-4b (async/chan 10))
(def chan-1-2 (async/chan) 10)
(def chan-2-3 (async/chan) 10)
(def chan-3-4 (async/chan) 10)
(def chan-4-aligner (async/chan) 10)
(def chan-4-print (async/chan) 10)

(defn -main  [& args]
  (print-actor chan-con-3)
  (<!! (controller "heja" "jeja" chan-con-1 chan-con-2 chan-con-3 chan-con-4 chan-con-1b ))
  (<!! (sw-cell chan-con-1 chan-con-1b  chan-con-1-zero chan-1-2 ))
  (<!! (sw-cell chan-con-2 chan-con-2b  chan-1-2 chan-2-3 ))
  (<!! (sw-cell chan-con-3 chan-con-3b  chan-2-3  chan-3-4))
  (<!! (sw-cell chan-con-4 chan-con-4b  chan-3-4 chan-4-print))

 )

  ;;(>!! chan-con-1 "wow")
  ;;(doseq [i (range 5)] (do (>!! chan-con-1 "m") (>!! chan-con-2 "s") (>!! chan-con-3 "d") (>!! chan-con-4 "a") (>!! chan-con-5 "v")))


  ;;(controller "abc" "def" chan-con-1 chan-con-2 chan-con-3 chan-con-4 chan-con-5)
