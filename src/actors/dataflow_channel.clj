(ns
  actors.dataflow-channel
  (:require [clojure.core.async.impl.protocols :as impl]
            [clojure.core.async.impl.dispatch :as dispatch]
            [clojure.core.async.impl.mutex :as mutex]
            [clojure.core.async.impl.channels :as channels :require [ManyToManyChannel]])
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

(defn chan
  ([buf] (chan buf nil))
  ([buf xform] (chan buf xform nil))
  ([buf xform exh]
     (ManyToManyChannel.
      (LinkedList.) (LinkedList.) buf (atom false) (mutex/mutex)
      (let [add! (if xform (xform impl/add!) impl/add!)]
        (fn
          ([buf]
             (try
               (add! buf)
               (catch Throwable t
                 (handle buf exh t))))
          ([buf val]
             (try
               (add! buf val)
               (catch Throwable t
                 (handle buf exh t)))))))))
