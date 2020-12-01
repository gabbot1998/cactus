(ns actors.cactus_actors
  (:gen-class)
  (:require

   [clojure.core.async
    :as async
    :refer [<! >!]
    ]

   [cactus.async
    :as cactus.async
    :refer [size? go <<! chan]
    ]

     [cactus.actor_macros
     :as cactus.actors
     :refer [defactor entities actor connection network defaction >>! guard defstate --]
     ]

     )
   )

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

(defactor sw-cell-printing [a-length tot-rows] [a-chan b-chan west] ==> [value aligner-value]
  (defstate [nw 0 n 0 i 0 j 0])
  (defaction a-chan [a] b-chan [b] west [new-west] ==>
    (let [new-nw new-west
          new-n (cell-action @nw @n new-west a b)
         ]
         (println @j " / " tot-rows)
         (-- j (inc @j))
         (>>! value new-n)
         (>>! aligner-value new-n)
         (if (= i (dec a-length))
           (do
             (-- nw 0)
             (-- n 0)
             (-- i 0)
             )
           (do
             (-- nw new-nw)
             (-- n new-n)
             (-- i (inc @i))
             )
         )
         )

    )
  )

(defactor sw-cell [a-length] [a-chan b-chan west] ==> [value aligner-value]
  (defstate [nw 0 n 0 i 0])
  (defaction a-chan [a] b-chan [b] west [new-west] ==>
    (let [new-nw new-west
          new-n (cell-action @nw @n new-west a b)
         ]
         ;(println new-n)
         (>>! value new-n)
         (>>! aligner-value new-n)
         (if (= @i (dec a-length))
           (do
             (-- nw 0)
             (-- n 0)
             (-- i 0)
             )
           (do
             (-- nw new-nw)
             (-- n new-n)
             (-- i (inc @i))
             )
           )
         )

    )
  )

(defactor stripe-actor [a-length] [b-chan] ==> [chan-0 chan-1 chan-2 chan-3 ]
  (defaction b-chan [bs] ==>
    ;(println "stripeactor sends: " bs)
    (doseq [i (range a-length)]
      (>>! chan-0 (nth bs 0))
      (>>! chan-1 (nth bs 1))
      (>>! chan-2 (nth bs 2))
      (>>! chan-3 (nth bs 3))
      )
    )
  )

(defactor fanout-actor [] [in-chan] ==> [chan-0 chan-1 chan-2 chan-3 ]
  (defaction in-chan [in] ==>
    ;(println "fanout sends: " in)
    (>>! chan-0 in)
    (>>! chan-1 in)
    (>>! chan-2 in)
    (>>! chan-3 in)
    )
  )

(defactor align-actor [A B stripe-width] [chan-0 chan-1 chan-2 chan-3] ==> [out]
  (defstate [row 0
             number-of-rows (* (/ (count B) stripe-width) (count A))
             temp-matrix (vec (repeat @number-of-rows (vec (repeat stripe-width 0))))
            ]
            )
  (defaction chan-0 [c0] chan-1 [c1] chan-2 [c2] chan-3 [c3] ==>
    (let [new-row (inc @row)
          new-temp-matrix (assoc @temp-matrix @row [c0 c1 c2 c3])
         ]
    (if (= @row (dec @number-of-rows))
      (do
        (println (index-of-largest-elem-in-matrix (padd (fill-matrix A B new-temp-matrix))))
        (println (trace-back "" "" A B (padd (fill-matrix A B new-temp-matrix)) (index-of-largest-elem-in-matrix (padd (fill-matrix A B new-temp-matrix))) ))
        ;(>>! out (padd (fill-matrix A B new-temp-matrix)))
        )
    )

      (-- row new-row)
      (-- temp-matrix new-temp-matrix)
    )
  )
)

(defactor controller-actor [A B width] [] ==> [chan-contr-fan-a chan-stripe]
  (defstate [fired false])
  (defaction ==> (guard (not @fired))
      (println @fired)
      (doseq [i (range (/ (count B) width))]
        ;(println "Sent: " (subs B (* width i) (* width (inc i))) " to stripe")
        (>>! chan-stripe (subs B (* width i) (* width (inc i))))
        (doseq [j (range (count A))]
          ;(println "Sent: " (nth A j) " to fanout.")
          (>>! chan-contr-fan-a (nth A j))
          )
        )

      (-- fired true)
    )
  )
