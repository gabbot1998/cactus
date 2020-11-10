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
(def penalty 0)



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
        (let [new-a (<! a) new-b (<! b) new-w (<! w)] ;;Wait for ports
          (let [
                new-nw new-w
                new-n (cell-action nw n new-w new-a new-b)
                ] ;;Assign new local state and execute body
            (>! v new-n);;Set output
            (>! aln-v new-n)

            (recur new-nw new-n (inc i)) ;;Recur
          )
        )
      )
    )
  )

(defn print-actor [chan]
    (go
      (loop [];;Set initial state
        (let [new-str (<! chan) ];;Wait for ports
            (let [] ;;Assign new local state and execute body

            (println new-str)
            ;;Set output
            (recur );;Recur
            )
          )
        )
      )
  )

;;(defn trace-back )

(defn fan-out-actor [c-in c-out-1 c-out-2 c-out-3 c-out-4]
  (go
    (loop []
      (let [token (<! c-in)]
          (>! c-out-1 token)
          (>! c-out-2 token)
          (>! c-out-3 token)
          (>! c-out-4 token)
          (recur )
        )
      )
    )
  )


(defn index-of-largest-elem-in-matrix [matrix]
  (let [flattenedMatrix (flatten matrix)
        index-of-max (+ 1 (.indexOf flattenedMatrix (apply max flattenedMatrix) ))
        ]
    (let [col (mod (.indexOf flattenedMatrix (apply max flattenedMatrix) ) (count (get matrix 0)))
          row (int (/ (.indexOf flattenedMatrix (apply max flattenedMatrix)) (count matrix)))
          ]
      [row col]
      )
    )
  )

(defn print-matrix [matrix]
  (doseq [row matrix] (println row))
  )

(defn trace-back [res-a res-b A B matrix [row col]]
  (let [n [(dec row) col]
        w [row (dec col)]
        nw [(dec row) (dec col)]]

  (if (or (= row 0) (= col 0) (= 0 (get (get matrix row) col)))

      (do
        [res-a res-b]

      )

      (do
        (let [nextDir
                (key (apply max-key val
                (hash-map
                :n (get (get matrix (first n)) (second n))
                :w (get (get matrix (first w)) (second w))
                :nw (get (get matrix (first nw)) (second nw))
                )))
              ]


          (if (= nextDir :nw)
            (do

              (recur (str (get A (second nw)) res-a) (str (get B (first nw)) res-b) A B matrix nw)
            )

            (if (= nextDir :w)
              (do

                (recur (str (get A (first w)) res-a) (str "-" res-b) A B matrix w)
                )

              (if (= nextDir :n)
              (do

                (recur (str "-" res-a) (str (get B (second n)) res-b) A B matrix n)
                )

              )
            )
            )
            )
          )
      )
    )
  )

  (defn aligner [A B c1 c2 c3 c4 out]
  (go
    (loop [ row 0
            matrix [[0 0 0 0]
                    [0 0 0 0]
                    [0 0 0 0]
                    [0 0 0 0]]
          ]

          (let [ci1 (<! c1) ci2 (<! c2) ci3 (<! c3) ci4 (<! c4)]
            (let [new-row (inc row) new-matrix (assoc matrix row [ci1 ci2 ci3 ci4]) ]
              (if (= new-row 4)
              (do
                (>!! out (trace-back "" "" A B new-matrix (index-of-largest-elem-in-matrix new-matrix)))
              )
              (recur new-row new-matrix))
              )
          )
        )
      )
   )



(defn controller [A B c1 c2 c3 c4 c5 w] ;;c51 - c54 is chanel to send b
      (go

        (loop [];;Set initial state
          (let [new-A (<! A) new-B (<! B)];;Wait for ports
            (let [] ;;Assign new local state and execute body
              (do

                (>! c1 "")
                (>! c2 (nth new-A 0))
                (>! c3 (nth new-A 1))
                (>! c4 (nth new-A 2))

                (>! c5 "")

                (>! w 0)




                (>! c1 "")
                (>! c2 (nth new-A 0))
                (>! c3 (nth new-A 1))
                (>! c4 (nth new-A 2))

                (>! c5 (nth new-B 0))

                (>! w 0)



                (>! c1 "")
                (>! c2 (nth new-A 0))
                (>! c3 (nth new-A 1))
                (>! c4 (nth new-A 2))

                (>! c5 (nth new-B 1))

                (>! w 0)


                (>! c1 "")
                (>! c2 (nth new-A 0))
                (>! c3 (nth new-A 1))
                (>! c4 (nth new-A 2))

                (>! c5 (nth new-B 2))

                (>! w 0)
                (recur );;Recur
              )
            )
          )
        )
      )
 )


(def chan-con-1-zero (chan 10))

(def chan-con-1 (chan 10))
(def chan-con-2 (chan 10))
(def chan-con-3 (chan 10))
(def chan-con-4 (chan 10))

(def chan-con-b (chan 10))
(def chan-con-b1 (chan 10))
(def chan-con-b2 (chan 10))
(def chan-con-b3 (chan 10))
(def chan-con-b4 (chan 10))

(def chan-1-2 (chan 10))
(def chan-2-3 (chan 10))
(def chan-3-4 (chan 10))

(def chan-4-print (async/chan 10))

(def chan-str-a (chan 10))
(def chan-str-b (chan 10))

(def chan-aln-1 (chan 10))
(def chan-aln-2 (chan 10))
(def chan-aln-3 (chan 10))
(def chan-aln-4 (chan 10))

(def chan-stop (chan 10))





(defn -main  [& args]

    (def A "HEJ")
    (def B "JHE")
    (print-actor chan-4-print)

    (sw-cell chan-con-1 chan-con-b1  chan-con-1-zero chan-1-2 chan-aln-1 "0")
    (sw-cell chan-con-2 chan-con-b2  chan-1-2 chan-2-3 chan-aln-2 "1")
    (sw-cell chan-con-3 chan-con-b3  chan-2-3  chan-3-4 chan-aln-3 "2")
    (sw-cell chan-con-4 chan-con-b4  chan-3-4 chan-stop chan-aln-4 "3")

    (aligner A B chan-aln-1 chan-aln-2 chan-aln-3 chan-aln-4 chan-4-print)


    (fan-out-actor chan-con-b chan-con-b1 chan-con-b2 chan-con-b3 chan-con-b4)

    (>!! chan-str-a A)
    (>!! chan-str-b B)

    (<!! (controller chan-str-a chan-str-b chan-con-1 chan-con-2 chan-con-3 chan-con-4 chan-con-b chan-con-1-zero))


 )
