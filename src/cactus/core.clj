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

             [actors.stripe_actor
             :as stripe-actor
             :refer [stripe-actor]
             ]

             )
   )

(def chan-1 (chan 50))
(def chan-2 (chan 50))

(def chan-cont-stripe (chan 10))

(def chan-sw-1-2 (chan 10))
(def chan-sw-2-3 (chan 10))
(def chan-sw-3-4 (chan 10))
(def chan-sw-4-1 (chan 10))


(def chan-stripe-sw-1 (chan 10))
(def chan-stripe-sw-2 (chan 10))
(def chan-stripe-sw-3 (chan 10))
(def chan-stripe-sw-4 (chan 10))

(def chan-contr-stripe (chan 10))

(def chan-contr-fan (chan 10))

(def chan-fan-sw-1 (chan 10))
(def chan-fan-sw-2 (chan 10))
(def chan-fan-sw-3 (chan 10))
(def chan-fan-sw-4 (chan 10))









(defn -main  [& args]

  (def A "heja")
  (def B "bbbbbbbbhejabbbb") ;En multipppel av 4. I det här fallet 16.
  (def width 4)

  (go (>! chan-1 A))
  (go (>! chan-2 B))

  (go (>! chan-cont-stripe "heja"))

  (controller chan-1 chan-2 chan-contr-fan chan-contr-stripe)

  (stripe-actor chan-contr-stripe (count A) chan-stripe-sw-1 chan-stripe-sw-2 chan-stripe-sw-3 chan-stripe-sw-4)

  (fan-out-actor chan-contr-fan chan-fan-sw-1 chan-fan-sw-2 chan-fan-sw-3 chan-fan-sw-4)


  (sw-cell chan-fan-sw-1 chan-stripe-sw-1 chan-sw-1-2 chan-sw-out-1 )
  (sw-cell chan-fan-sw-2 chan-stripe-sw-2 chan-sw-2-3 chan-sw-out-2 )
  (sw-cell chan-fan-sw-3 chan-stripe-sw-3 chan-sw-3-4 chan-sw-out-3 )
  (sw-cell-end chan-fan-sw-4 chan-stripe-sw-4 chan-sw-4-1 chan-sw-out-4 )


  (print-actor chan-stripe-sw-1)
  (print-actor chan-stripe-sw-2)
  (print-actor chan-stripe-sw-3)
  (print-actor chan-stripe-sw-4)


  (while true )





 )
