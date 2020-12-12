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

(defactor pe [wa] [in] ==> []
  (defaction in [token] ==>
      (println token)
    )
  )

(def a (feed-one "nice" {}) )
(def b (pe "wap" {}) )


(defn -main  [& args]
  

  ;()
  ;(con ((feed-one "nice") :out) ((pe "penja" :in) ) )



  ; (entities
  ;   ;
  ;   ; (actor feed (feed-one "wap"))
  ;   ;
  ;   ; let [channel-2 (cactus.async/chan [420]) channel-1 (cactus.async/chan [420]) channel-0 (cactus.async/chan [])] ((((actor p1 (print-one s)) {:feed1 {:out channel-0}, :p2 {:in channel-0}, :feed0 {:out channel-1}, :p1 {:in channel-1}, :feed {:out channel-2}, :p0 {:in channel-2}, :number-of-channels 3, :channel-arguments {:channel-0 nil, :channel-1 {:initial-tokens [420]}, :channel-2 {:initial-tokens [420]}}})) (((actor p2 (print-one d)) {:feed1 {:out channel-0}, :p2 {:in channel-0}, :feed0 {:out channel-1}, :p1 {:in channel-1}, :feed {:out channel-2}, :p0 {:in channel-2}, :number-of-channels 3, :channel-arguments {:channel-0 nil, :channel-1 {:initial-tokens [420]}, :channel-2 {:initial-tokens [420]}}})) (clojure.core/println ((for [i (range 2)] (actor (symbol (str feed i)) (feed-one i)))))))
  ;
  ;   (actor p1 (print-one "s"))
  ;   (actor p2 (print-one "d"))
  ;
  ;   (for [i (range 2)]
  ;     (actor (symbol (str "feed" i)) (feed-one i))
  ;     )
  ;
    ; (network
      ; { :feedid {:out chan-0} :p0id {:in chan-0} :initial-token {:chan-0 [420]} }
      ; (con (feed :out) (p0 :in) {:initial-tokens [420]})
      ; (con (feed0 :out) (p1 :in) {:initial-tokens [420]})
      ; (con (feed1 :out) (p2 :in))
      ; (for [i (range 2)]
      ;   `(con (~(symbol (str "feed" i)) :out) (~(symbol (str "print" i)) :in))
      ;   )
      ; )
  ;   )

;The connections macros could expand to a lambda that calls the two actors lambdas and creates the channel with the

; ;;Någonsatans har vi en defactor scalar
; (defactor scalar)
; (defnetwork FIR [] [] ==> []
;   ;;Instantiates the actors. I.e. Returns lambdas
; (let [scalars (for [i  (range )] (scalar i))]
;
; ;For every actor instance creates the connection betwee the two. I.e. the functions are called with the correct channels instances.
; (for [x scalar]
;   (connect (x :port) (y :port))
;   )
;   )
; )
  (while true )

 )
