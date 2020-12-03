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
     :refer [defactor entities actor con network defaction >>! guard defstate --]
     ]

     )
   )

(defactor feed-one [send] [] ==> [out]
  (defstate [fired true])
  (defaction ==> (guard @fired)
      (-- fired false)
      (>>! out send)

    )
  )

(defactor print-one [] [in] ==> []
  (defaction in [token] ==>
      (println token)
    )
  )



(defn -main  [& args]

  ;(con (feed out) (p0 in) {:intial-tokens [0]})
  (entities

    (actor feed (feed-one "wap"))

    (actor p0 (print-one ))
    (actor p1 (print-one ))
    (actor p2 (print-one ))



    (for [i (range 2)]
      `(actor ~(symbol (str "feed" i)) (feed-one ~i))
      )

    (network
      (con (feed :out) (p0 :in))
      (for [i (range 2)]
        `(con (~(symbol (str "feed" i)) :out) (~(symbol (str "print" i)) :in))
        )
      )
    )


  (while true )

 )
