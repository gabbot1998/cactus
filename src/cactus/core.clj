;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

(ns cactus.core
  (:gen-class)
(:require
            [clojure.core.async
             :as async
             :refer [ <! >! <!! >!!]
             :exclude [chan go]
             ]

             [cactus.async
             :as cactus.async
             :refer [go <<! chan]
             ]

             )
   )

(def chan-1 (chan))
(def chan-2 (chan))


(defn -main  [& args]

                                        ;implementera random access peek
                                        ;byta datastruktur
                                        ;skriva hårdkodad guard
                                        ;macros


                                        ; <?
                                        ; <°
                                        ; <*
                                        ; <-
                                        ; <=
                                        ; <%
                                        ; <)
                                        ; <}
                                        ; ◊
                                        ; <>
                                        ; ‡
                                        ; **

  (go
    (>! chan-1 2992929299292)
    (>! chan-1 3)
    (>! chan-1 2992929299292)
    (>! chan-1 3)
    (>! chan-1 3)
    )

  (go
    (println (<<! chan-1 6))
    )

  (while true)



                                        ;(guarded-actor chan)

  )
