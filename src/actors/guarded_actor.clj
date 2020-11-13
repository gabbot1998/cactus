(ns actors.guarded_actor
  (:gen-class)
  (:require
    [clojure.core.async
     :as async
     :refer [>! go-loop <! >!! <!! chan buffer close! thread]
     :exclude [go]
     ]

    [cactus.async
    :as cactus.async
    :refer [<<! go]
    ]

             ))

(defn guarded-actor [c-in c-out]
  (go
    (loop []
    (let [guard-var-1 (<<! c-in 1)]
      (if (> guard-var-1 0)
        (do
          (let [var-1 (<! c-in)]
              (println (str "värdet är större än noll: "))
            )
          )
        (do
          (let [var-1 (<! c-in) ]
            (println (str "värdet är mindre eller lika med noll: "))
            )
          )

        )
        (recur)
        )
      )
      )
  )
