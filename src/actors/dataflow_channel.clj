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

(defn dataflow-chan
 "Creates a channel with an optional buffer, an optional transducer
 (like (map f), (filter p) etc or a composition thereof), and an
 optional exception-handler.  If buf-or-n is a number, will create
 and use a fixed buffer of that size. If a transducer is supplied a
 buffer must be specified. ex-handler must be a fn of one argument -
 if an exception occurs during transformation it will be called with
 the Throwable as an argument, and any non-nil return value will be
 placed in the channel."
 ([] (dataflow-chan nil))
 ([buf-or-n] (dataflow-chan buf-or-n nil))
 ([buf-or-n xform] (dataflow-chan buf-or-n xform nil))
 ([buf-or-n xform ex-handler]
    (when xform (assert buf-or-n "buffer must be supplied when transducer is"))
    (channels/chan (if (number? buf-or-n) (buffer buf-or-n) buf-or-n) xform ex-handler)))
