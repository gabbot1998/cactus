(ns actors.controller
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))


             (defn controller [A B c1 c2 c3 c4 c5 w] ;;c51 - c54 is chanel to send b
                   (go

                     (loop [];;Set initial state
                       (let [new-A (<! A) new-B (<! B)];;Wait for ports
                         (let [] ;;Assign new local state and execute body
                           (do
                             ;(println "round 1")
                             (>! c1 "")
                             (>! c2 (nth new-A 0))
                             (>! c3 (nth new-A 1))
                             (>! c4 (nth new-A 2))

                             (>! c5 "")

                             (>! w 0)

                             ;(println "round 2")


                             (>! c1 "")
                             (>! c2 (nth new-A 0))
                             (>! c3 (nth new-A 1))
                             (>! c4 (nth new-A 2))

                             (>! c5 (nth new-B 0))

                             (>! w 0)

                             ;(println "round 3")

                             (>! c1 "")
                             (>! c2 (nth new-A 0))
                             (>! c3 (nth new-A 1))
                             (>! c4 (nth new-A 2))

                             (>! c5 (nth new-B 1))

                             (>! w 0)
                             ;(println "round 4")

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
