(ns actors.aligner
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))

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
                  (>! out (fill-matrix A B new-temp-matrix))
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
