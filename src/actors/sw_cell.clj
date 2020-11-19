(ns actors.sw_cell
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))

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

 (defn sw-cell [a b an w v aln-v name]
    (go
      (loop [nw 0 n 0 i 0];;Set initial state
        (let [new-a (<! a) new-b (<! b) new-w (<! w)] ;;Wait for ports
          (let [
                new-nw new-w
                new-n (cell-action nw n new-w new-a new-b)
                ] ;;Assign new local state and execute body
            (>! v new-n);;Set output
            (>! aln-v new-n)
            (if (= i an)
              (do

                  (recur 0 0 0)
                )
              (do
                (recur new-nw new-n (inc i))
                )
            )


             ;;Recur
          )
        )
      )
    )
  )
