(ns actors.controller
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))


             (defn controller [A B n chan-first-sw chan-stripe] ;;c51 - c54 is chanel to send b
                   (go
                     (loop [];;Set initial state
                       (let [new-A (<! A) new-B (<! B)];;Wait for ports
                         (let [] ;;Assign new local state and execute body
                           (do
                             (doseq [i (range (/ (count new-B) n))]
                                (>! chan-stripe (subs new-B (* n i) (* n (+ i 1)))); pick out the strings

                                (doseq [j (range (count new-A))]
                                  (>! chan-first-sw (nth new-A j))

                                )
                               )

                             (recur );;Recur
                           )
                         )
                       )
                     )
                   )
              )
