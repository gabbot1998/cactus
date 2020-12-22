(ns cactus.channels
  (:require
   [clojure.core.async.impl.protocols :as impl]
   [clojure.core.async.impl.channels :as channels :refer [box]]
   [cactus.protocols :as cactus.impl]
   [clojure.core.async.impl.mutex :as mutex]
   [cactus.buffer :as ring-buffer]
   [clojure.core.async.impl.dispatch :as dispatch])
  (:import [java.util.concurrent.locks Lock]
           [java.util LinkedList Queue Iterator]
           [cactus.buffer ringbuffer]))

(deftype DataFlowChannel [^ringbuffer buf, ^Lock mutex, ^LinkedList sizes]

  cactus.impl/ReadPort
  (peek!
    [this i handler]
    (.lock mutex)
    (let [val (box (.peep buf i))]
      (.unlock mutex)
      val))

  (size
    [this n handler]
    (.lock mutex)
    (if (<= n (.len buf))
      (do
        (.unlock mutex)
        (box true))
      (do
        (.add sizes handler)
        (.unlock mutex)
        nil)))

  impl/ReadPort
  (take!
    [this handler]
    (do
      (.lock mutex)
      (let [val (box (.plop! buf))]
        (.unlock mutex)
        val)))

  cactus.impl/WritePort
  (put!
    [this e handler]
    (.lock mutex)
    (.offer! buf e)
    (let [iter (.iterator sizes)]
      (loop [sizers []]
        (if (.hasNext iter)
          (let [elem (.next iter)]
            (if (<= (cactus.impl/size-depth elem) (.len buf))
              (do
                (let [func (cactus.impl/fun elem)]
                  (dispatch/run (fn [] (func true)))
                  (.remove iter)
                  (recur (conj sizers func)))))))))
    (.unlock mutex)
    (box true))

  impl/Channel
  (close!
    [this])

  (closed?
    [this]
    [this]
    false))

(defn chan [buf init]
  (let [dfc (DataFlowChannel. buf (mutex/mutex) (LinkedList.))]
    (doseq [val init]
      (.put! dfc val (fn [] 1)))
    dfc)
  )
