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
(def match 8)
(def mismatch -3)
(def penalty -2)



(defn score [a b]
  (if (= a "") 0
    (if (= b "") 0
      (if (= a b) match mismatch)
    )
  )
)



(defn cell-action [nw n w a b]
  (max
   (+ nw (score a b))
   (+ w penalty)
   (+ n penalty)
   0)
 )

 (defn sw-cell [a b w v aln-v name]
    (go
      (loop [nw 0 n 0 i 0];;Set initial state
        (let [new-a (<!! a) new-b (<!! b) new-w (<!! w)] ;;Wait for ports
          (let [
                new-nw new-w
                new-n (cell-action nw n new-w new-a new-b)
                ] ;;Assign new local state and execute body
            (>!! v new-n);;Set output
            (>!! aln-v new-n)
            ;(println (str i name ": " new-n "\n\n"))
            (recur new-nw new-n (inc i)) ;;Recur
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
            ;(print "Value of last actor is: ")
            (println new-str)
            ;;Set output
            (recur );;Recur
            )
          )
        )
      )
  )

;;(defn trace-back )

(first (apply min-key second (map-indexed vector [1 2 4 0 5])))

(defn aligner [c1 c2 c3 c4 out]
  (go
    (loop [ row 0
            matrix [[0 0 0 0]
                    [0 0 0 0]
                    [0 0 0 0]
                    [0 0 0 0]]
          ]

          (let [ci1 (<!! c1) ci2 (<!! c2) ci3 (<!! c3) ci4 (<!! c4)]
            (let [new-row (inc row) new-matrix (assoc matrix row [ci1 ci2 ci3 ci4]) ]
              (if (= row 3) (>!! out new-matrix)
              (recur new-row new-matrix)
              )
            ;(if (= row 3) (println new-matrix) )
          )
        )
      )
    )
  )


(defn controller [A B c1 c2 c3 c4 c51 c52 c53 c54 w] ;;c51 - c54 is chanel to send b
      (go

        (loop [];;Set initial state
          (let [new-A (<!! A) new-B (<!! B)];;Wait for ports
            (let [] ;;Assign new local state and execute body
              (do
                ;(println "round 1")
                (>!! c1 "")
                (>!! c2 (nth new-A 0))
                (>!! c3 (nth new-A 1))
                (>!! c4 (nth new-A 2))
                (>!! c51 "")
                (>!! c52 "")
                (>!! c53 "")
                (>!! c54 "")
                (>!! w 0)

                ;(println "round 2")


                (>!! c1 "")
                (>!! c2 (nth new-A 0))
                (>!! c3 (nth new-A 1))
                (>!! c4 (nth new-A 2))
                (>!! c51 (nth new-B 0))
                (>!! c52 (nth new-B 0))
                (>!! c53 (nth new-B 0))
                (>!! c54 (nth new-B 0))
                (>!! w 0)

                ;(println "round 3")

                (>!! c1 "")
                (>!! c2 (nth new-A 0))
                (>!! c3 (nth new-A 1))
                (>!! c4 (nth new-A 2))
                (>!! c51 (nth new-B 1))
                (>!! c52 (nth new-B 1))
                (>!! c53 (nth new-B 1))
                (>!! c54 (nth new-B 1))
                (>!! w 0)
                ;(println "round 4")

                (>!! c1 "")
                (>!! c2 (nth new-A 0))
                (>!! c3 (nth new-A 1))
                (>!! c4 (nth new-A 2))
                (>!! c51 (nth new-B 2))
                (>!! c52 (nth new-B 2))
                (>!! c53 (nth new-B 2))
                (>!! c54 (nth new-B 2))
                (>!! w 0)


                (recur );;Recur
              )
            )
          )
        )
      )
 )


(def chan-con-1-zero (chan 10))

(def chan-con-1 (chan 10))
(def chan-con-2 (async/chan 10))
(def chan-con-3 (async/chan 10))
(def chan-con-4 (async/chan 10))

(def chan-con-b1 (chan 10))
(def chan-con-b2 (async/chan 10))
(def chan-con-b3 (async/chan 10))
(def chan-con-b4 (async/chan 10))

(def chan-1-2 (async/chan 10))
(def chan-2-3 (async/chan 10))
(def chan-3-4 (async/chan 10))

(def chan-4-print (async/chan 10))

(def chan-str-1 (chan 10))
(def chan-str-2 (chan 10))

(def chan-aln-1 (chan 10))
(def chan-aln-2 (chan 10))
(def chan-aln-3 (chan 10))
(def chan-aln-4 (chan 10))

(def chan-stop (chan 10))


(defn -main  [& args]
  (print-actor chan-4-print)

  (sw-cell chan-con-1 chan-con-b1  chan-con-1-zero chan-1-2 chan-aln-1 "0")
  (sw-cell chan-con-2 chan-con-b2  chan-1-2 chan-2-3 chan-aln-2 "1")
  (sw-cell chan-con-3 chan-con-b3  chan-2-3  chan-3-4 chan-aln-3 "2")
  (sw-cell chan-con-4 chan-con-b4  chan-3-4 chan-stop chan-aln-4 "3")

  (aligner chan-aln-1 chan-aln-2 chan-aln-3 chan-aln-4 chan-4-print)

  (>!! chan-str-1 "abb")
  (>!! chan-str-2 "aaa")

  (<!! (controller chan-str-1 chan-str-2 chan-con-1 chan-con-2 chan-con-3 chan-con-4 chan-con-b1 chan-con-b2 chan-con-b3 chan-con-b4 chan-con-1-zero))


 )
