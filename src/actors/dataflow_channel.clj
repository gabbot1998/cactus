(ns
  actors.dataflow-channel
  (:refer-clojure :exclude [reduce transduce into merge map take partition
                            partition-by bounded-count])
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.channels :as channels]
            [clojure.core.async.impl.buffers :as buffers]
            [clojure.core.async.impl.timers :as timers]
            [clojure.core.async.impl.dispatch :as dispatch]
            [clojure.core.async.impl.ioc-macros :as ioc]
            [clojure.core.async.impl.mutex :as mutex]
            [clojure.core.async.impl.concurrent :as conc]
            )
  (:import [java.util LinkedList Queue Iterator]
           [java.util.concurrent.locks Lock]
           [clojure.core.async.impl.channels ManyToManyChannel]
           ))

(defn- ex-handler [ex]
 (-> (Thread/currentThread)
     .getUncaughtExceptionHandler
     (.uncaughtException (Thread/currentThread) ex))
 nil)

(defn- handle [buf exh t]
 (let [else ((or exh ex-handler) t)]
   (if (nil? else)
     buf
     (impl/add! buf else))))

(defn buffer
 "Returns a fixed buffer of size n. When full, puts will block/park."
 [n]
 (assert (pos? n) "fixed buffers must have size > 0")
 (buffers/fixed-buffer n))

(defn dropping-buffer
 "Returns a buffer of size n. When full, puts will complete but
 val will be dropped (no transfer)."
 [n]
 (buffers/dropping-buffer n))

(defn sliding-buffer
 "Returns a buffer of size n. When full, puts will complete, and be
 buffered, but oldest elements in buffer will be dropped (not
 transferred)."
 [n]
 (buffers/sliding-buffer n))

(defn unblocking-buffer?
 "Returns true if a channel created with buff will never block. That is to say,
  puts into this buffer will never cause the buffer to be full. "
 [buff]
 (extends? impl/UnblockingBuffer (class buff)))

 (defn O> [port]
   (assert nil "Waddap") )

(defprotocol Printer
  (printer [_])
  )

(deftype Channel [^Queue buf mutex]
  impl/WritePort
  (put! [this val handler]
      (.lock mutex)
      (impl/add! buf val)
      (.unlock mutex)
    )

  impl/ReadPort
  (take! [this handler]
      (.lock mutex)
      (let [val (impl/remove! buf)]
        (.unlock mutex)
        val
      )


    )

  Printer
  (printer [this]
    (impl/remove! buf)
    )

  )


(defn chan
  [buf]
  (Channel. buf (mutex/mutex))
  )

(defn dataflow-chan
 "Nice channel bro"
 ([] (dataflow-chan nil))
 ([buf-or-n]
   (chan (if (number? buf-or-n) (buffer buf-or-n) buf-or-n))
   )
 )
