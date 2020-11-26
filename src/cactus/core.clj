(ns cactus.core
  (:gen-class)
(:require
            [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! buffer close! thread]
             :exclude [chan go]
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

             ; [actors.print_actor
             ; :as print-actor
             ; :refer [print-actor]
             ; ]

             [actors.guarded_actor
             :as guarded_actor
             :refer [guarded-actor]
             ]

             [actors.controller
             :as controller
             :refer [controller]
             ]

             [actors.dataflow_channel
             :as dataflow-channel
             :refer []
             ]

             [cactus.async
             :as cactus.async
             :refer [go <<! chan]
             ]

             [cactus.actor_macros
             :as cactus.actors
             :refer [defactor entities]
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

(defactor feed-actor [str] [] ==> [out]
  (go
    (doseq [i (range 100)]
      (>! (connections-map :out) str)
      )
    )
  )

(defactor print-actor [] [in] ==> []
  (go
    (println (<! (connections-map :in)))
    )
  )

(defactor print-two-actor [] [in-0 in-1] ==> []
  (go
    (loop []
    (println (<! (connections-map :in-0)) (<! (connections-map :in-1)))
    (recur )
    )
    )
  )




(defn -main  [& args]

  (entities
    ('actor feeder-0 (feed-actor "Printing 0" ))
    ('actor feeder-1 (feed-actor "Printing 1" ))

    ('actor print-two   (print-two-actor ))

    (network
      (connection (feeder-0 :out) (print-two :in-0))
      (connection (feeder-1 :out) (print-two :in-1))
      )
    )

  (while true )

 )
