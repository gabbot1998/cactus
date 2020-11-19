(ns actors.aligner
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))

             (def match 8)
             (def mismatch -3)
             (def penalty -2)

(defn index-of-largest-elem-in-matrix [matrix]
 (let [flattenedMatrix (flatten matrix)
       index-of-max (.indexOf flattenedMatrix (apply max flattenedMatrix) )
       ]
   (let [col (mod index-of-max (count (get matrix 0)))
         row (int (/ index-of-max (count (get matrix 0))))
         ]
     [row col]
     )
   )
 )

(defn print-matrix [matrix]
 (doseq [row matrix] (println row))
 )

(defn trace-back [res-a res-b A B matrix [row col]]; row col is the index of largest element in matrix
 (let [n [(dec row) col]
       w [row (dec col)]
       nw [(dec row) (dec col)]]
  ;(println "recured")
 (if (or (= row 0) (= col 0) (= 0 (get (get matrix row) col)))

     (do
       ;(println "returning value")
       [res-a res-b]

     )

     (do
       ;(println row col)
       (let [
             value (get (get matrix row) col)
             use-n (+ (get (get matrix (first n)) (second n)) penalty)
             use-w (+ (get (get matrix (first w)) (second w)) penalty)
             use-nw-match (+ (get (get matrix (first nw)) (second nw)) match)
             use-nw-mismatch (+ (get (get matrix (first nw)) (second nw)) mismatch)
             ]

             (if (= use-n value)
               (recur (str (get A (dec row)) res-a) (str "-" res-b) A B matrix n)

               (if (= use-w value)
                 (recur (str "-" res-a) (str (get B (dec col)) res-b) A B matrix w)

                 (if (or (= use-nw-match value) (= use-nw-mismatch value))
                   (recur (str (get A (dec row)) res-a) (str (get B (dec col)) res-b) A B matrix nw)

               )
             )

           )
         )
     )
   )
 )
 )

;vector has to be of length k * width where k is an integer
(defn modify-row [mult vector new-part] ;Multiple of 4, ie col, the original vector, and the 4 new values
(let [
      width (count new-part)
      mult-index (* mult width)
      start (subvec vector 0 mult-index)
      ]
      (if (>= (+ mult-index width) (count vector)) ;Check that the end part is not the empty vector as that would throw an error!
        (vec (concat start new-part))
        (let [end (subvec vector (+ width mult-index))]
          (vec (concat start new-part end))
        )
      )
  )
)

(defn fill-matrix [A B values]
  (loop [
        matrix-rows (count A)
        matrix (vec (repeat matrix-rows (vec (repeat (count B) 0))))
        row 0
        col 0
        ]

        (let [
             row-in-matrix (mod row matrix-rows)
             new-matrix (assoc matrix row-in-matrix (modify-row col (nth matrix row-in-matrix) (nth values row)))
             ]

             (if (= (inc row) (count values))

                new-matrix

                (if (and (= (mod (inc row) matrix-rows) 0) (>= (inc row) matrix-rows))

                   (recur
                     matrix-rows
                     new-matrix
                     (inc row)
                     (inc col)
                     )

                   (recur
                     matrix-rows
                     new-matrix
                     (inc row)
                     col
                     )
                  )
             )

         )
      )
  )

  (defn padd [matrix]
    (let [
          first-row (vec (repeat (+ 1 (count (nth matrix 0))) 0))
          ]


          (loop
            [
            new-matrix matrix
            row 0
            rows-in-matrix (count matrix)
            ]
            (if (= (inc row) rows-in-matrix)
              (vec (concat [first-row]
                (assoc new-matrix row (vec (concat [0] (nth new-matrix row))))
                ))
              (recur
                (assoc new-matrix row (vec (concat [0] (nth new-matrix row))))
                (inc row)
                rows-in-matrix
              )

            )

        )
      )
    )


(defn aligner [A B n c1 c2 c3 c4 out]
  (let [
    number-of-rows (* (/ (count B) n) (count A))
    matrix (vec (repeat (count A) (vec (repeat (count B) 0))))
  ]
  (go
    (loop [ row 0
            temp-matrix (vec (repeat number-of-rows (vec (repeat 4 0))))

          ]
          ;(println "The number of rows are" number-of-rows)
          (let [ci1 (<! c1) ci2 (<! c2) ci3 (<! c3) ci4 (<! c4)]
            (let [new-row (inc row)
                  new-temp-matrix (assoc temp-matrix row [ci1 ci2 ci3 ci4])
                  ]

              (if (= row (- number-of-rows 1))
                (do

                  (println (index-of-largest-elem-in-matrix (padd (fill-matrix A B new-temp-matrix))))
                  (println (trace-back "" "" A B (padd (fill-matrix A B new-temp-matrix)) (index-of-largest-elem-in-matrix (padd (fill-matrix A B new-temp-matrix))) ))
                  ;(>! out (padd (fill-matrix A B new-temp-matrix)))
                  )

              )

              (recur new-row new-temp-matrix)
              ;)
            ;(if (= row 3) (println new-temp-matrix) )
          )

      )
    )
  )
  )
  )
