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

(defactor print-one [wa] [in] ==> []
  (defaction in [token] ==>
      (println token)
    )
  )



(defn -main  [& args]

  ; (
  ;   ((actor printone (print-one "m"))
  ;   {:printone {:in (chan [1])}})
  ;   )

  (entities
    ;
    ; (actor feed (feed-one "wap"))
    ;
    (actor p0 (print-one "s" ))
    (actor p1 (print-one "s"))
    (actor p2 (print-one "d"))

    (for [i (range 2)]
      (actor (symbol (str "feed" i)) (feed-one i))
      )

    (network
      (con (feed :out) (p0 :in) {:initial-tokens [420]})
      (con (feed0 :out) (p1 :in) {:initial-tokens [420]})
      (con (feed1 :out) (p2 :in))
      ; (for [i (range 2)]
      ;   `(con (~(symbol (str "feed" i)) :out) (~(symbol (str "print" i)) :in))
      ;   )
      )
    )


  (while true )

 )
