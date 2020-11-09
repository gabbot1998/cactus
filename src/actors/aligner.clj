(ns actors.aligner
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))

(defn aligner [c1 c2 c3 c4 out]
  (go
    (loop [ row 0
            matrix [[0 0 0 0]
                    [0 0 0 0]
                    [0 0 0 0]
                    [0 0 0 0]]
          ]

          (let [ci1 (<! c1) ci2 (<! c2) ci3 (<! c3) ci4 (<! c4)]
            (let [new-row (inc row) new-matrix (assoc matrix row [ci1 ci2 ci3 ci4]) ]
              (if (= row 3) (>! out new-matrix)
              (recur new-row new-matrix)
              )
            ;(if (= row 3) (println new-matrix) )
          )
        )
      )
    )
  )
