(ns cactus.channels
  (:require
   [clojure.core.async.impl.protocols :as impl]
   [clojure.core.async.impl.channels :as channels :refer [box]]
   [cactus.protocols :as cactus.impl]
   [clojure.core.async.impl.mutex :as mutex]
   [cactus.buffer :as ring-buffer ] )
  (:import [java.util.concurrent.locks Lock]
           [cactus.buffer ringbuffer])
  )

(deftype DataFlowChannel [^ringbuffer buf, ^Lock mutex]

  cactus.impl/ReadPort
  (peek!
    [this i handler]
    (box (.peep buf i))
    )

  (size [this handler]
    (box (.len buf))
    )

  impl/ReadPort
  (take!
    [this handler]
    (do
      (.lock mutex)
      (let [val (.plop! buf)]
      (.unlock mutex)
      (box val))))


  impl/WritePort
  (put!
    [this e handler]
    (do
      (.lock mutex)
      (.offer! buf e)
      (.unlock mutex)
      (box true))
    )

  impl/Channel
  (close!
    [this])

  (closed?
    [this]
    false)
  )

(defn chan [buf init]
  (let [dfc (DataFlowChannel. buf (mutex/mutex))]
    (doseq [val init]
      (.put! dfc val nil))
    dfc)
  )
