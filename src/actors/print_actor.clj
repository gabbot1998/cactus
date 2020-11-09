(ns actors.print_actor
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))

(defn print-actor [chan]
    (go
      (loop [];;Set initial state
        (let [new-str (<! chan) ];;Wait for ports
          (let [] ;;Assign new local state and execute body
            ;(print "Value of last actor is: ")
            (println new-str)
            ;;Set output
            (recur );;Recur
            )
          )
        )
      )
  )
