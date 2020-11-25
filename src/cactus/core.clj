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
    :refer [size? go <<! chan]
    ]

   )
  )

(def chan-1 (chan [1337 1337 1377 1337]))
(def chan-2 (chan))


(defn -main  [& args]

  (>!! chan-1 2992929299292)
  (>!! chan-1 29299292)
  (>!! chan-1 29299292)
  (>!! chan-1 2992929299292)
  (>!! chan-1 2992929299292)
  (>!! chan-1 2992929299292)
  (>!! chan-1 3)
  (>!! chan-1 3)
  (>!! chan-1 29299292)
  (>!! chan-1 29299292)
  (>!! chan-1 29299292)

  (go
    (println (<! chan-1))
    (println (<! chan-1))
    (println (<! chan-1))
    (println (<! chan-1))
    (println (<! chan-1))
    (println (<! chan-1))
    )


  (while true);(guarded-actor chan)

  )
