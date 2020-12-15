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
     :refer [defactor entities actor con network defaction >>! guard defstate -- defnetwork vect]
     ]

     )
   )

(defactor feed-one [send] [] ==> [out]
  (defstate [fired true])
  ;(println "fired")
  (defaction ==> (guard @fired)
      (-- fired false)
      ;(println "fired")
      (>>! out send)
    )

  )

(defactor pe [] [in-0 in-1] ==> []
  (defaction in-0 [token0] in-1 [token1] ==>
      (println token0 token1)
    )
  )

(defactor incr [i] [in] ==> [out]
  ;(println "incr" i "\n\n\n")
  (defaction in [a] ==>
    (println "incremented" a)
    (>>! out (inc a))
    )
  )

(defactor printer [] [in] ==> []
  ;(println "printer")
  (defaction in [token] ==>
      (println token)
    )
  )

(def a (feed-one "nice") )
(def b (pe ) )

;(to get the reference to an object, we use the {(keyword (str a))  a})



(defn -main  [& args]

  ; (defnetwork
  ;
  ;   (list
  ;     (con ((feed-one "wap" {}) out) ((printer {}) in) )
  ;     (con ((feed-one "wap2" {}) out) ((printer {}) in) )
  ;     )
  ;   )


  ;
  ; (defnetwork
  ;   (let [incre (incr {})
  ;         printer (printer {})
  ;         feed (feed-one 0 {})
  ;         ]
  ;
  ;     (list
  ;       (con (feed out) (incre in) )
  ;       (con (incre out) (printer in))
  ;       )
  ;     )
  ;   )

  (def n 100)

  (defnetwork
    (let [incrementers (for [i (range n)] (incr i ))
          pr (printer )
          feed (feed-one 0 )
          ]

          (concat
            (list
              (con (feed out) ((nth incrementers 0 nil) in) )

              )

            (for [i (range (dec n))]
                (con ((nth incrementers i nil) out) ((nth incrementers (inc i) nil) in) )
              )
              (list   (con ((nth incrementers (dec n) nil) out) (pr in)))
            )
          )
      )

  ; (defnetwork
  ;   (let [feeders (for [i (range n)] (feed-one i {}))
  ;         printers (for [i (range n)] (printer {}))
  ;         ]
  ;
  ;         (for [i (range (count feeders))]
  ;            (con ((nth feeders i nil) out) ((nth printers i nil) in))
  ;            )
  ;           )
  ;         )

  ;The clause inside the network has to return a list of connections
  ; (defnetwork
  ;   (let [feed-0 (feed-one 0 {})
  ;         feed-1 (feed-one 1 {})
  ;         pep (pe {})
  ;        ]
  ;
  ;        (list
  ;          (con (feed-0 out) (pep in-0))
  ;          (con (feed-1 out) (pep in-1))
  ;        )
  ;     )
  ;   )

  ;(go (println (<! c)))
  (while true)

 )
