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

(defactor print-actor [] [in-0 in-1] ==> []
  (defaction in-0 [a b c] in-1 [d] ==> (guard (= a "hej"))
    (println "a, b, d: " a ", " b ", " d)
    )

  (defaction in-0 [d e f] ==>
    (println "Cosnuming three on in-0")
    )

  )

(defactor feed-once [x y] [] ==> [out]
  (defaction ==>
      (>>! out x)
      (>>! out y)
      (>>! out x)
      (>>! out x)
      (>>! out x)
      (while true)
    )
  )

(defn -main  [& args]

  (entities
    (actor feeder-0 (feed-once "hej" "second"))
    (actor feeder-1 (feed-once "Supposed to be d" "Supposed to be d"))
    (actor printer (print-actor ))

    (network
      (connection (feeder-0 :out) (printer :in-0) )
      (connection (feeder-1 :out) (printer :in-1) )
      )
    )


  (while true )

 )
