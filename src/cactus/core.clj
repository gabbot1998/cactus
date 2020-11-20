;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

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

             [actors.print_actor
             :as print-actor
             :refer [print-actor]
             ]

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

             )
   )

(def chan-1 (chan 50))
(def chan-2 (chan 50))






(defn -main  [& args]

    ;implementera random access peek
    ;byta datastruktur
    ;skriva hårdkodad guard
    ;macros

    ; <?
    ; <°
    ; <*
    ; <-
    ; <=
    ; <%
    ; <)
    ; <}
    ; ◊
    ; <>
    ; ‡
    ; **


    (go
      (>! chan-1 333)
      (println "we grab one", (<! chan-1))
      (>! chan-1 1337)
      (>! chan-1 122)
      (>! chan-1 1333333)
      (>! chan-1 17271)
      (println "we grab elite", (<! chan-1))
      (println "we grabd", (<! chan-1))
      (>! chan-1 17271)
      (>! chan-1 17271)
      (>! chan-1 17271)
      (>! chan-1 17271)
      (>! chan-1 17271)
      (>! chan-1 17271)
      (>! chan-1 17271)
      (>! chan-1 17271)
      (println "we grab after huge oversize", (<! chan-1))
      (println "we grab after huge oversize", (<! chan-1))
      (println "we grab after huge oversize", (<! chan-1))
      (println "we grab after huge oversize", (<! chan-1))
      (println "we grab after huge oversize", (<! chan-1))
      (println "we grab after huge oversize", (<! chan-1))
      (println "we grab after huge oversize", (<! chan-1))
      (println "we grab after huge oversize", (<! chan-1))
      (>! chan-1 "tjene")
      (>! chan-1 "tjeneneen")
      (println "we should get 17271", (<! chan-1))
      (println "we should get 17271", (<! chan-1))
      (println "shoudl get tjene", (<! chan-1))
      )





    (while true)



    ;(guarded-actor chan)

 )
