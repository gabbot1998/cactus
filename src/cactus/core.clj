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

     [cactus.actor_macros
     :as cactus.actors
     :refer [defactor entities actor connection network defaction >>! guard]
     ]

     )
   )

(defactor guarded-actor [] [] ==> []
  (defaction ==> (guard true)
    (println "This actor always fires")
    )
  )

(defactor print-two-actor [] [X Y] ==> []
  (defaction X [x] Y [y] ==>
      (println x y)
    )
  )

(defactor print-actor [] [in-0] ==> []
  (defaction in-0 [a b c] ==>
    (println "a, b, c: " a ", " b ", " c)
    )
  )

(defactor has-initial-tokens [] [] ==> [out]
  (defaction ==>
    )
  )

(defactor arg-actor [a b c] [] ==> [out]
  (defaction ==>
    ;(println a b c)
    )
  )

(defactor has-two-actions [] [in-0 in-1] ==> [out-0 out-1]
  (defaction in-0 [a] ==>
      (println "output on out-0")
      (>>! out-0 a)
    )
  (defaction in-1 [b] ==>
      (println "output on out-1")
      (>>! out-1 b)
    )
  )


(defn -main  [& args]

  (entities
    (actor feeder (has-initial-tokens ))
    (actor printer (print-actor ))
    ;(actor takes-arguments (arg-actor "hej" 2 ["wow" "hello"]))

    (network
      (connection (feeder :out) (printer :in-0) {:initial-tokens [1 2 3]})
      )
    )


  (while true )

 )
