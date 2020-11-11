;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

(ns cactus.core
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]

             [actors.sw_cell
             :as sw-cell
             :refer [sw-cell]
             ]

             [actors.aligner
             :as aligner
             :refer [aligner]
             ]

             [actors.fan_out_actor
             :as fan-out-actor
             :refer [fan-out-actor]
             ]

             [actors.print_actor
             :as print-actor
             :refer [print-actor]
             ]

             [actors.controller
             :as controller
             :refer [controller]
             ]
             )
   )

  (defn peek [chan index]
    ;returns the first element in the queue
    )

  (defn consume [chan]
    ;returns the elemnt and removes
    )

  (defn guard-check [])

    hej: action A: [ a, b ] repeat 3 ==> Out: [element]
    guard:
      (add-all-values a b) = 20;
      end
    do out: [a + b]
    end


   (defn guarded-actor [chan1]
       (go

         (loop [ guard-vars1 []  guard-vars2 [] ];
          (let [new-guard-vars1 (peek chan1 1) new-guard-vars2 (peek chan1 1)])
            (if (= (count guard-vars2) repeat)
              ;check guard and potential fire, else
              (if (guard-check guard-vars)
                ;fire action
                ;body
                (do
                  (let [new-str (consume chan)];;Wait for ports
                    (let [] ;;Assign new local state and execute body
                      ;(print "Value of last actor is: ")
                      (println new-str)
                      ;;Set output
                      (recur );;Recur
                      )
                    )
                  )
                  ;dont fire action
              )

          (recur (reduce guard-vars1 new-guard-vars1) (reduce guard-vars2 new-guard-vars2))
          )

          [a] -> ([a] -> [a]) -> [a] -> [a]

          [] [1] [1] [3] [1, 3] [5] [1, 3, 5]

          0 + [1 2 3 ] -> 6

           )
        ;start of body

         )
     )



(def chan (chan 50))



(defn -main  [& args]
    (println "Time to make an actor")

    (guarded-actor chan)

 )
