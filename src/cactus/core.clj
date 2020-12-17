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
     :refer [defentity con defaction >>! guard defstate -- exec-network defnetwork endport]
     ]

     )
   )

(defentity feed-one [send] [] ==> [out]
  (defstate [fired true])
  ;(println "fired")
  (defaction ==> (guard @fired)
      (-- fired false)
      ;(println "fired")
      (>>! out send)
    )

  )

(defentity pe [] [in-0 in-1] ==> []
  (defaction in-0 [token0] in-1 [token1] ==>
      (println token0 token1)
    )
  )

(defentity incr [i] [in] ==> [out]
  ;(println "incr" i "\n\n\n")
  (defaction in [a] ==>
    (println "incremented" a)
    (>>! out (inc a))
    )
  )

(defentity printer [] [in] ==> []
  ;(println "printer")
  (defaction in [token] ==>
      (println token)
    )
  )

; (defentity endpoint [s] [] ==> []
;   (println s)
;   (defaction ==>
;     )
;   )
;
; (def endpoint  (endpoint ))

; (defentity nw [] [in] ==> [out]
;   (defaction in [n] ==>
;     (defnetwork
;       (let [incrementers (for [i (range n)] (incr i ))
;             pr (printer )
;             feed (feed-one 0 )
;             out-a (endpoint "nice")
;             ]
;
;             (concat
;               (list
;                 (con (feed out) ((nth incrementers 0 nil) in) )
;
;                 )
;
;               (for [i (range (dec n))]
;                   (con ((nth incrementers i nil) out) ((nth incrementers (inc i) nil) in) )
;                 )
;                 (list   (con ((nth incrementers (dec n) nil) out) (out-a out)))
;               )
;             )
;         )
;     )
;   )

(defentity relay [] [in] ==> [out]
  (defaction in [a] ==>
      (>>! out a)
    )
  )

(defentity nw1 [] [in] ==> [output]
  (defnetwork in [x] ==>

      (let [feed (feed-one x)
            rel (relay )
            end (endport output)
            pr (printer )
           ]

        (list
          (con (feed out) (rel in))
          (con (rel out) (end out))
          )

      )
    )
  )

(defentity nw2 [] [in] ==> [output]
  (defaction in [x] ==>
    (exec-network
      (let [feed (feed-one x)
            rel (relay )
            end (endport output)
            ;ending (printboi (connections-map :output))
            pr (printer )
           ]
           ;(println x)
        ;(println  "The output is: " (connections-map :output))

        (list
          (con (feed out) (rel in))
          (con (rel out) (end out))
          )
        )
      )
    )
  )

(defn -main  [& args]

  ; (exec-network
  ;   (let [feed (feed-one "19759823042038457923845")
  ;         nw1 (nw1 )
  ;         pr (printer )
  ;         ]
  ;
  ;         (list
  ;           (con (feed out) (nw1 in))
  ;           (con (nw1 output) (pr in))
  ;           )
  ;     )
  ;   )


    (def n 500)

     (exec-network
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

  ;(go (println (<! c)))
  (while true))
