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
    ;(println "incremented" a)
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

;#object[cactus.channels.DataFlowChannel 0x3b6a63eb cactus.channels.DataFlowChannel@3b6a63eb]

;Endpoint should exapnd to

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




(def a (feed-one "nice") )
(def b (pe ) )

;(to get the reference to an object, we use the {(keyword (str a))  a})
; {
;   :cactus.core$feed_one$fn__9609@1a5ea4cf {:ref #object[cactus.core$feed_one$fn__9609 0x1a5ea4cf cactus.core$feed_one$fn__9609@1a5ea4cf], :out #object[cactus.channels.DataFlowChannel 0x41030c21 cactus.channels.DataFlowChannel@41030c21]}
;   :cactus.core$incr$fn__9774@33e80a27 {:ref #object[cactus.core$incr$fn__9774 0x33e80a27 cactus.core$incr$fn__9774@33e80a27], :in #object[cactus.channels.DataFlowChannel 0x41030c21 cactus.channels.DataFlowChannel@41030c21], :out #object[cactus.channels.DataFlowChannel 0x3c5797c6 cactus.channels.DataFlowChannel@3c5797c6]}
;   :cactus.core$printer$fn__9848@7649d350 {:ref #object[cactus.core$printer$fn__9848 0x7649d350 cactus.core$printer$fn__9848@7649d350], :in #object[cactus.channels.DataFlowChannel 0x3c5797c6 cactus.channels.DataFlowChannel@3c5797c6]}
; }

(defn -main  [& args]

  ; (defnetwork
  ;   (let [feed0 (feed-one 10 ) netw (nw ) printer (printer )]
  ;   (list
  ;     (con (feed0 out) (netw    in))
  ;     (con (netw out) (endpoint out) )
  ;     (con (endpoint out-c) (printer in))
  ;     ;(con (feed1 out) (printer in) )
  ;     )
  ;     )
  ;   )

  ;

  (exec-network
    (let [feed (feed-one "19759823042038457923845")
          nw1 (nw1 )
          pr (printer )
          ]

          (list
            (con (feed out) (nw1 in))
            (con (nw1 output) (pr in))
            )
      )
    )

  ; (defnetwork
  ;   (let [incre (incr 1)
  ;         printer (printer )
  ;         feed (feed-one 0 )
  ;         ]
  ;
  ;     (list
  ;       (con (feed out) (incre in) )
  ;       (con (incre out) (endpoint out))
  ;       )
  ;     )
  ;   )

; {
;   :cactus.core$nw1$fn__10144@5fbb2e1c {:ref #object[cactus.core$nw1$fn__10144 0x5fbb2e1c cactus.core$nw1$fn__10144@5fbb2e1c], :out #object[cactus.channels.DataFlowChannel 0xb257072 cactus.channels.DataFlowChannel@b257072]},
;   :cactus.core$nw2$fn__10219@138d85f8 {:ref #object[cactus.core$nw2$fn__10219 0x138d85f8 cactus.core$nw2$fn__10219@138d85f8], :in #object[cactus.channels.DataFlowChannel 0xb257072 cactus.channels.DataFlowChannel@b257072]}
; }
  ;
;
; {
;   :cactus.core$feed_one$fn__9609@295e989 {:ref #object[cactus.core$feed_one$fn__9609 0x295e989 cactus.core$feed_one$fn__9609@295e989], :out #object[cactus.channels.DataFlowChannel 0x23b62cc3 cactus.channels.DataFlowChannel@23b62cc3]}
;   :cactus.core$nw1$fn__10212@504216ff {:ref #object[cactus.core$nw1$fn__10212 0x504216ff cactus.core$nw1$fn__10212@504216ff], :in #object[cactus.channels.DataFlowChannel 0x23b62cc3 cactus.channels.DataFlowChannel@23b62cc3], :output #object[cactus.channels.DataFlowChannel 0xf4e235e cactus.channels.DataFlowChannel@f4e235e]}
;   :cactus.core$printer$fn__9848@4ce4c097 {:ref #object[cactus.core$printer$fn__9848 0x4ce4c097 cactus.core$printer$fn__9848@4ce4c097], :in #object[cactus.channels.DataFlowChannel 0xf4e235e cactus.channels.DataFlowChannel@f4e235e]}
; }
;
;
;
; {
;   :cactus.core$feed_one@3bda821c {:ref #object[cactus.core$feed_one 0x3bda821c cactus.core$feed_one@3bda821c], :out #object[cactus.channels.DataFlowChannel 0x10b8be07 cactus.channels.DataFlowChannel@10b8be07]}
;   :cactus.core$relay$fn__10144@7a228301 {:ref #object[cactus.core$relay$fn__10144 0x7a228301 cactus.core$relay$fn__10144@7a228301], :in #object[cactus.channels.DataFlowChannel 0x10b8be07 cactus.channels.DataFlowChannel@10b8be07], :out #object[cactus.channels.DataFlowChannel 0x7e6c1d9a cactus.channels.DataFlowChannel@7e6c1d9a]}
;   :cactus.core$endpoint@78e545fe {:ref #object[cactus.core$endpoint 0x78e545fe cactus.core$endpoint@78e545fe], :output #object[cactus.channels.DataFlowChannel 0x7e6c1d9a cactus.channels.DataFlowChannel@7e6c1d9a]}
; }                                                                                                              :output #object[cactus.channels.DataFlowChannel 0xf4e235e cactus.channels.DataFlowChannel@f4e235e]

;Endpoint should take the token at port out and add it to the channel at (>! (connections-map :out))

; (defentity endpoint [] [output] ==> []
;   (defaction output [token] ==>
;     ;(>>! token output)
;     )
;   )
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
  ;   (let [feed-0 (feed-one 0 )
  ;         feed-1 (feed-one 1 )
  ;         pep (pe )
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
