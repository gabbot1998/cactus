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

(defactor print-two-actro [] [X Y] ==> []
  (defaction X [x] Y [y] ==>
      (println x y)
    )
  )

(defactor print-actor [name] [in] ==> []
  (defaction in [a] ==>
    (println "From " name " got value: " a)
    )
  )

(defactor has-initial-tokens [] [] ==> [out]
  (defaction ==>
    )
  )

(defactor arg-actor [a b c] [] ==> [out]
  (defaction ==> (guard true )
    (println a b c)
    )
  )

(defactor has-two-actions [] [in-0 in-1 in-2] ==> [out-0 out-1 out-2]
  (defaction in-0 [a] ==> (guard true)
      (println "output on out-0")
      (>>! out-0 a)
    )

  (defaction in-1 [b] ==>
      (println "output on out-1")
      (>>! out-1 b)

      ; (loop [n 0]
      ;   (let [n (+ n 1)]
      ;     (let [n (+ n 1)]
      ;       (recur n)
      ;       )
      ;     )
      ;   )
      ; (:= n 0)
      ; (:= n (n + 1))
    )
  (defaction in-2 [b] ==>
      (println "output on out-2")
      (>>! out-2 b)
    )
  )

  ; (defactor has-two-actions [] [in-0 in-1] ==> [out-0 out-1]
  ;   )

(defn -main  [& args]

(entities
  (actor t (has-two-actions))
  (actor f0 (has-initial-tokens))
  (actor f1 (has-initial-tokens))
  (actor f2 (has-initial-tokens))

  (actor p0 (print-actor "Actor 0"))
  (actor p1 (print-actor "Actor 1"))
  (actor p2 (print-actor "Actor 2"))

  (network
    (connection (f0 :out) (t :in-0) {:initial-tokens ["Value for 0"]})
    (connection (f1 :out) (t :in-1) {:initial-tokens ["Value for 1"]})
    (connection (f2 :out) (t :in-2) {:initial-tokens ["Value for 2"]})

    (connection (t :out-0) (p0 :in) )
    (connection (t :out-1) (p1 :in))
    (connection (t :out-2) (p2 :in))
    )
  )

  ; (entities
  ;   (actor feeder (has-initial-tokens ))
  ;   (actor printer (print-actor ))
  ;   (actor takes-arguments (arg-actor "hej" 2 ["wow" "hello"]))
  ;
  ;   (network
  ;     (connection (feeder :out) (printer :in-0) {:initial-tokens [1 2 3]})
  ;     )
  ;   )


  (while true )

 )
