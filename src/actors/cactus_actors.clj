(ns actors.cactus_actors
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
     :refer [defactor entities actor connection network defaction >>! guard defstate --]
     ]

     )
   )

(def match 8)
(def mismatch -3)
(def penalty -2)

(defn score [a b]
 (if (= a "") 0
   (if (= b "") 0
     (if (= a b) match mismatch)
   )
 )
)

(defn cell-action [nw n w a b]
 (max
  (+ nw (score a b))
  (+ w penalty)
  (+ n penalty)
  0)
)

(defactor sw-cell [a-length] [a-chan b-chan west] ==> [value aligner-value]
  (defstate [nw 0 n 0 i 0])
  (defaction a-chan [a] b-chan [b] west [new-west] ==>
    (let [new-nw new-west
          new-n (cell-action @nw @n new-west a b)
         ]
         (>>! value new-n)
         (>>! aligner-value new-n)
         (if (= i (dec a-length))
           (do
             (-- nw 0)
             (-- n 0)
             (-- i 0)
             )
           (do
             (-- nw new-nw)
             (-- n new-n)
             (-- i (inc @i))
             )
         )
         )

    )
  )
