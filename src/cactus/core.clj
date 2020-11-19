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
      (>! chan-1 3)
      (>! chan-1 2)
      )


    (go
      (println (<<! chan-1 1 ) )
      (println (<<! chan-1 1 ) )
      (println "wow")
      )

    (go
      (println (<<! chan-1 1) )
      (println "nice")
      )






    (while true)



    ;(guarded-actor chan)

 )
