(ns actors.sw_cell_end
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

 (defn sw-cell-end [a b an w v aln-v name tot-rows]
    (go
      (doseq [i (range an)]
        (>! v 0)
      )
      (loop [nw 0 n 0 i 0 j 0];;Set initial state
        (let [new-a (<! a) new-b (<! b) new-w (<! w)] ;;Wait for ports
          (let [
                new-nw new-w
                new-n (cell-action nw n new-w new-a new-b)
                ] ;;Assign new local state and execute body
            (>! v new-n);;Set output
            (>! aln-v new-n)
            (if (= i (dec an))
              (do
                  (println j "/" tot-rows)
                  (recur 0 0 0 (inc j))
                )
              (do
                (recur new-nw new-n (inc i) (inc j))
                )
            )
          )
        )
      )
    )
  )
