(ns actors.fan_out_actor
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]))


(defn fan-out-actor [c-in c-out-1 c-out-2 c-out-3 c-out-4]
 (go
   (loop []
     (let [token (<! c-in)]
         (>! c-out-1 token)
         (>! c-out-2 token)
         (>! c-out-3 token)
         (>! c-out-4 token)
         (recur )
       )
     )
   )
 )
