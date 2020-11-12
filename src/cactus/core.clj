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
             :refer [>! go-loop <! >!! <!! go buffer close! thread]
             :exclude [chan]
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

             [actors.dataflow-channel
             :as dataflow-channel
             :refer [dataflow-chan]
             ]

             )
   )


(def chan-1 (dataflow-chan 50))



(defn -main  [& args]

    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (.put! chan-1 "wowowowow" "waddap"))
    (go (println (.take! chan-1 "waddap")))
    (go (println (.take! chan-1 "waddap")))
    (go (println (.take! chan-1 "waddap")))

    (while true)



    ;(guarded-actor chan)

 )
