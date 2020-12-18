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

     [actors.cactus_actors
      :as cactus.actors.cactus_actors
      :refer [sw-cell sw-cell-printing align-actor controller-actor fanout-actor stripe-actor]
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

(defentity printer [prefix] [in] ==> []
  ;(println "printer")
  (defaction in [token] ==>
      (println prefix token)
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
            pr (printer "")
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
            pr (printer "")
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

(defentity buf [] [in] ==> []
  (defaction in [a] ==>

    )
  )

(defentity collector-cell [] [score vector] ==> [out]
  (defaction score [s] vector [res] ==>
    (>>! out (conj res s))
    )
  )

(defentity has-init-tokens [] [] ==> []
  (defstate [f false])
  (defaction ==> (guard @f)

    )
  )

; (defentity collector-cell-end [] [score vector] ==> [out]
;   (defaction score [s] vector [res] ==>
;     (println "The row is: " (conj res s))
;     (>>! out (conj res s))
;     )
;   )


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

  (println "started")
  (def A "JAKFDLSDMFF");Kan vara vilken som helst
  (def B "HAALOOOOKADFJLASJFDKASOE") ;En multipppel av width
  (def width 24)

  (def n 1)

  (println "B length " (count B))
  (println "A length " (count A))

  (defentity fanout-cell [] [in] ==> [sw-out next-fo]
    (defaction in [char] ==>
      (>>! sw-out char)
      (>>! next-fo char)
      )
    )

  (defentity stripe-cell [a-length] [vec] ==> [vec-out char]
    (defaction vec [x] ==>
      (>>! vec-out (rest x))
      (doseq [i (range a-length)]
        (>>! char (first x))
        )
      )
    )

; (defentity sw-cell [a-length] [a-chan b-chan west] ==> [value aligner-value]

  (exec-network
    (let [controller (controller-actor A B width)
          sp-cells (for [i (range width)] (stripe-cell (count A)) )
          fo-cells (for [i (range width)] (fanout-cell ) )
          sw-cells (for [i (range width)] (sw-cell (count A)) )
          col-cells (for [i (range width)] (collector-cell ))
          init (has-init-tokens )


          b (for [i (range width)] (buf ) )
          b0 (for [i (range width)] (buf ) )
          b1 (for [i (range width)] (buf ) )
          printers0 (for [i (range width)] (printer "From the sw-cells :" ) )

          buffer (buf )
          buffer0 (buf )
          b1000 (buf )
          pr0 (printer "From the last sp cell")
          pr1 (printer "The row is: ")
          pr2 (printer "The last sw cell aligner value")
          ; stripe (stripe-actor (count A))
          ; fanout (fanout-actor )
          ]

          (concat

          (list
            (con (controller chan-stripe) ((nth sp-cells 0) vec) )
            (con (controller chan-contr-fan-a) ((nth fo-cells 0) in) )
            )

          (for [i (range (dec width))]
            (con ((nth sp-cells i) vec-out) ((nth sp-cells (inc i)) vec))
            )

          (list
            (con ((nth sp-cells (dec width)) vec-out) (b1000 in))
            )




          (for [i (range (dec width))]
            (con ((nth fo-cells i) next-fo) ((nth fo-cells (inc i )) in) )
            )

          (list
            (con ((nth fo-cells (dec width)) next-fo) (buffer in) )
            )




          ;connectig the fo cells to the sw cells
          (for [i (range width)]
            (con ((nth fo-cells i) sw-out) ((nth sw-cells i) a-chan) )
            )

          ;Connecting the sp cells to the sw cells
          (for [i (range width)]
            (con ((nth sp-cells i) char) ((nth sw-cells i) b-chan) )
            )



          ;Connectig the sw cells to eachother
          (list
            (con ((nth sw-cells (dec width)) value) ((nth sw-cells 0) west) {:initial-tokens (vec (repeat (count A) 0))})
            )

          (for [i (range (dec width))]
            (con ((nth sw-cells i) value) ((nth sw-cells (inc i )) west) )
            )

          ; (for [i (range (dec width))]
          ;   (con ((nth sw-cells i) aligner-value) ((nth b0 (inc i )) in) )
          ;   )
          ;
          ; (list
          ;   (con ((nth sw-cells (dec width)) aligner-value) (pr1 in) )
          ;   )

          (for [i (range width)]
            (con ((nth sw-cells i) aligner-value) ((nth col-cells i) score) )
            )

          (list
            (con (init out) ((nth col-cells 0) vector) {:initial-tokens (vec (repeat (* (/ (count B) width) (count A)) []))})
            )

          (for [i (range (dec width))]
            (con ((nth col-cells i) out) ((nth col-cells (inc i)) vector))
            )

          (list
            (con ((nth col-cells (dec width)) out) (pr1 in))
            )

        )
      )
      )



  ; (exec-network
  ;   (let [controller (controller-actor A B width)
  ;         stripe (stripe-actor (count A))
  ;         fanout (fanout-actor )
  ;
  ;         swcells (for [i (range n)] (sw-cell (count A)) )
  ;
  ;         sw-end (sw-cell-printing (count A) (* (/ (count B) width ) (count A)) )
  ;
  ;         ; sw0 (sw-cell (count A))
  ;         ; sw1 (sw-cell (count A))
  ;         ; sw2 (sw-cell (count A))
  ;         ; sw3 (sw-cell-printing (count A) (* (/ (count B) width ) (count A)) )
  ;
  ;         aligner (align-actor A B width)
  ;        ]
  ;
  ;        (concat
  ;
  ;        (for [i (range (count swcells))]
  ;          (con (stripe (symbol (str "chan-" i))) ((nth swcells i nil) b-chan) )
  ;        )
  ;
  ;        (for [i (range (count swcells))]
  ;          (con (fanout (symbol (str "chan-" i))) ((nth swcells i nil) a-chan) )
  ;        )
  ;
  ;        (list
  ;          (con (stripe chan-3) (sw-end b-chan) )
  ;          )
  ;
  ;        (list
  ;          (con (fanout chan-3) (sw-end a-chan) )
  ;          )
  ;
  ;        (list
  ;         (con (controller chan-contr-fan-a) (fanout in-chan) )
  ;         (con (controller chan-stripe) (stripe b-chan) )
  ;
  ;
  ;         ; (con (stripe chan-0) (sw0 b-chan) )
  ;         ; (con (stripe chan-1) (sw1 b-chan) )
  ;         ; (con (stripe chan-2) (sw2 b-chan) )
  ;         ; (con (stripe chan-3) (sw3 b-chan) )
  ;
  ;         ; (con (fanout chan-0) (sw0 a-chan) )
  ;         ; (con (fanout chan-1) (sw1 a-chan) )
  ;         ; (con (fanout chan-2) (sw2 a-chan) )
  ;         ; (con (fanout chan-3) (sw3 a-chan) )
  ;
  ;         (con ((nth swcells 0 nil) value) ((nth swcells 1 nil) west) )
  ;         (con ((nth swcells 1 nil) value) ((nth swcells 2 nil) west) )
  ;         (con ((nth swcells 2 nil) value) (sw-end west) )
  ;         (con (sw-end value) ((nth swcells 0 nil) west) {:initial-tokens (vec (repeat (count A) 0))} )
  ;
  ;         ; (con (sw0 value) (sw1 west) )
  ;         ; (con (sw1 value) (sw2 west) )
  ;         ; (con (sw2 value) (sw3 west) )
  ;         ; (con (sw3 value) (sw0 west) {:initial-tokens (vec (repeat (count A) 0))} )
  ;
  ;
  ;         ; (con (sw0 aligner-value) (aligner chan-0))
  ;         ; (con (sw1 aligner-value) (aligner chan-1))
  ;         ; (con (sw2 aligner-value) (aligner chan-2))
  ;         ; (con (sw3 aligner-value) (aligner chan-3))
  ;       )
  ;
  ;       (for [i (range (count swcells))]
  ;         (con ((nth swcells i nil) aligner-value) (aligner (symbol (str "chan-" i)) ))
  ;       )
  ;
  ;       (list
  ;         (con (sw-end aligner-value) (aligner chan-3))
  ;         )
  ;
  ;       )
  ;
  ;     )
  ;   )

   ;(def n 20000)
   ; (exec-network
   ;   (let [incrementers (for [i (range n)] (incr i ))
   ;         pr (printer )
   ;         feed (feed-one 0 )
   ;         ]
   ;
   ;         (concat
   ;           (list
   ;             (con (feed out) ((nth incrementers 0 nil) in) )
   ;
   ;             )
   ;
   ;           (for [i (range (dec n))]
   ;               (con ((nth incrementers i nil) out) ((nth incrementers (inc i) nil) in) )
   ;             )
   ;             (list   (con ((nth incrementers (dec n) nil) out) (pr in)))
   ;           )
   ;         )
   ;     )

  ;(go (println (<! c)))
  (while true))
