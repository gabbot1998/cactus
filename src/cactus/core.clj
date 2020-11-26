(ns cactus.core
  (:gen-class)
  (:require
   [clojure.core.async
    :as async
    :refer [ <! >! <!! >!!]
    :exclude [chan go]
    ]

   [cactus.async
    :as cactus.async
    :refer [size? go <<! chan]
    ]

     [cactus.actor_macros
     :as cactus.actors
     :refer [defactor entities actor connection network]
     ]

     )
   )




; (defactor print-actor [] [in] ==> []
;   (defaction :in [x] ==>
;     (println x)
;      )
;   )

; (defmacro actor
;   )

; (defn print-actor [in]
;   (go-loop [];No initial state
;     (let [
;           x (<<! in 0)
;         ]
;         (if true; Default true guard
;           ;True
;           (do
;             (consume-tokens [in 1])
;             (println x)
;             (recur )
;             )
;           ;False
;           (do
;             (recur )
;             )
;         )
;       )
;     )
;   )

; (defactor print-two-actor [] [in-1 in-2] ==> []
;     (defaction in-1: [x] in-2: [y]  ==>
;       (println x)
;       (println y)
;       )
;     )
;
; (defactor feeder-actor [string n] [] ==> [out]
;   (defaction [] ==>
;     (doseq [i (range n)]
;       (out: string)
;       )
;     )
;   )

(defactor feed-actor [str n] [] ==> [out]
  (go
    (doseq [i (range n)]
      (>! (connections-map :out) str)
      )
    )
  )

  (defactor feed-once [str] [] ==> [out]
    (go
        (>! (connections-map :out) str)
      )
    )

(defactor print-actor [] [in] ==> []
  (go
    (while (< (size? (connections-map :in)) 1))
    (println (<! (connections-map :in)))
    )
  )

(defactor print-two-actor [] [in-0 in-1] ==> []
  (go
    (println "consuming")
    (loop []
      (println
      (<! (connections-map :in-0))
      (<! (connections-map :in-1))
      )
    (recur )
    )
    )
  )

(defn -main  [& args]

  (entities
    (actor feeder-0 (feed-once "Printing 0"))
    (actor printer (print-actor ))

    (network
      (connection (feeder-0 :out) (printer :in) )
      )
    )


  (while true )

 )
