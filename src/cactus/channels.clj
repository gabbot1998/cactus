(ns cactus.channels
  (:require
   [clojure.core.async.impl.protocols :as impl]
   [clojure.core.async.impl.channels :as channels :refer [box]]
   [cactus.protocols :as cactus.impl]
   [clojure.core.async.impl.mutex :as mutex]
   [cactus.buffer :as ring-buffer ] 
   [clojure.core.async.impl.dispatch :as dispatch])
  (:import [java.util.concurrent.locks Lock]
           [cactus.buffer ringbuffer])
  )

(deftype DataFlowChannel [^ringbuffer buf, ^Lock mutex, ^{:volatile-mutable true} depth, ^{:volatile-mutable true} sizehandler]


  cactus.impl/ReadPort
  (peek!
    [this i handler]
    (box (.peep buf i)))

  (size [this n handler]
    (.lock mutex)
    (set! sizehandler handler)
    (set! depth n)
    (if (<= n (.len buf))
      (do
        (.unlock mutex)
        (box true))
      (do
        (.unlock mutex)
        nil)))

  impl/ReadPort
  (take!
    [this handler]
    (do
      (.lock mutex)
      (let [val (box (.plop! buf))]
        (.unlock mutex)
        (box val))))

  impl/WritePort
  (put!
    [this e handler]
    (when (nil? val)
      (throw (IllegalArgumentException. "Can't put nil on channel")))
    (do
      (.lock mutex)
      (.offer! buf e)
      (if (not= nil depth)
        (if (= (inc (.size buf)) depth);;if we should awaken a peek
          (let [val 222]
            (.unlock mutex)
            (if (not= sizehandler nil)
              (dispatch/run (sizehandler true))
              nil))
          nil)
        nil)
      (.unlock mutex)
      (box true)))

  impl/Channel
  (close!
    [this])

  (closed?
    [this]
    false))

(defn chan [buf init]
  (let [dfc (DataFlowChannel. buf (mutex/mutex) nil nil)]
    (doseq [val init]
      (.put! dfc val nil))
    dfc)
  )
