(ns cactus.core
  (:gen-class)
  (:require

   [clojure.core.async
    :as async
    :refer [<! >!]
    ]

   [cactus.async
    :as cactus.async
    :refer [size? go <<! chan]
    ]

    [actors.cactus_actors
    :as cactus.actors.cactus_actors
    :refer [sw-cell sw-cell-printing align-actor controller-actor fanout-actor stripe-actor]
    ]

     [cactus.actor_macros
     :as cactus.actors
     :refer [defactor entities actor connection network defaction >>! guard defstate --]
     ]

     )
   )

(defactor has-init [] [] ==> []
  (defaction ==> (guard true)
    )
  )


(defn -main  [& args]
    (println "started")
    (def A "JDSALASDFSF");Kan vara vilken som helst
    (def B "HEJAHEJAJASJDLJDWASS") ;En multipppel av 4. I det här fallet 16.
    (def width 4)

    (println (count B))

    (entities
      (actor controller (controller-actor A B width))
      (actor stripe (stripe-actor (count A)))

      (actor fanout (fanout-actor ))

      (actor sw0 (sw-cell (count A)))
      (actor sw1 (sw-cell (count A)))
      (actor sw2 (sw-cell (count A)))
      (actor sw3 (sw-cell-printing (count A) (* (/ (count B) width ) (count A)) ) )

      (actor aligner (align-actor A B width))

      (network
        (connection (controller :chan-contr-fan-a) (fanout :in-chan) )
        (connection (controller :chan-stripe) (stripe :b-chan) )

        (connection (stripe :chan-0) (sw0 :b-chan) )
        (connection (stripe :chan-1) (sw1 :b-chan) )
        (connection (stripe :chan-2) (sw2 :b-chan) )
        (connection (stripe :chan-3) (sw3 :b-chan) )

        (connection (fanout :chan-0) (sw0 :a-chan) )
        (connection (fanout :chan-1) (sw1 :a-chan) )
        (connection (fanout :chan-2) (sw2 :a-chan) )
        (connection (fanout :chan-3) (sw3 :a-chan) )

        (connection (sw0 :value) (sw1 :west) )
        (connection (sw1 :value) (sw2 :west) )
        (connection (sw2 :value) (sw3 :west) )
        (connection (sw3 :value) (sw0 :west) {:initial-tokens (vec (repeat (count A) 0))} )

        (connection (sw0 :aligner-value) (aligner :chan-0))
        (connection (sw1 :aligner-value) (aligner :chan-1))
        (connection (sw2 :aligner-value) (aligner :chan-2))
        (connection (sw3 :aligner-value) (aligner :chan-3))

        )
      )

  (while true )

 )
