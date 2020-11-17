(ns actors.stripe_actor
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))


 (defn stripe-actor [bs a n c1 c2 c3 c4] ; c1 - c4 are the channels that the actor sends out on. as is the vector containing the 4 characters that are the current stripe.
    (go
      (loop [];;Set initial state
        (let [local-B (<! bs) local-a (<! b)] ;;Wait for ports
          (let [] ;;Assign new local state and execute body
            (doseq [i (range n)]
              (>! c1 (nth local-B 0))
              (>! c1 (nth local-B 1))
              (>! c1 (nth local-B 2))
              (>! c1 (nth local-B 3))

              )
            (recur ) ;;Recur
          )
        )
      )
    )
  )
