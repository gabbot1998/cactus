(ns
   cactus.async
   (:require [clojure.core.async.impl.dispatch :as dispatch]
             [clojure.core.async
              :as async
              :refer []
              ]
              [cactus.buffer :as buffer]
              [clojure.core.async.impl.ioc-macros :as ioc]
              [cactus.async_ioc_macros :as cactus.ioc]
              [cactus.channels :as channels]

    )

  )

(defn buffer
  "Returns a fixed buffer of size n. When full, puts will block/park."
  [n]
  (assert (pos? n) "fixed buffers must have size > 0")
  (buffer/fixed-buffer n))

(defn chan
  "Creates a channel with an optional buffer, an optional transducer
  (like (map f), (filter p) etc or a composition thereof), and an
  optional exception-handler.  If buf-or-n is a number, will create
  and use a fixed buffer of that size. If a transducer is supplied a
  buffer must be specified. ex-handler must be a fn of one argument -
  if an exception occurs during transformation it will be called with
  the Throwable as an argument, and any non-nil return value will be
  placed in the channel."
  ([] (chan nil))
  ([buf-or-n] (chan buf-or-n nil))
  ([buf-or-n xform] (chan buf-or-n xform nil))
  ([buf-or-n xform ex-handler]
     (when xform (assert buf-or-n "buffer must be supplied when transducer is"))
     (channels/chan (if (number? buf-or-n) (buffer buf-or-n) buf-or-n) xform ex-handler)))

(defn <<!
  "peeks a val from port. Must be called inside a (go ...) block. Will
  return nil if closed. Will park if nothing is available."

  [port index]
   (assert nil "<<! used not in (go ...) block"))

(defmacro go
  "Asynchronously executes the body, returning immediately to the
  calling thread. Additionally, any visible calls to <!, >! and alt!/alts!
  channel operations within the body will block (if necessary) by
  'parking' the calling thread rather than tying up an OS thread (or
  the only JS thread when in ClojureScript). Upon completion of the
  operation, the body will be resumed.
  go blocks should not (either directly or indirectly) perform operations
  that may block indefinitely. Doing so risks depleting the fixed pool of
  go block threads, causing all go block processing to stop. This includes
  core.async blocking ops (those ending in !!) and other blocking IO.
  Returns a channel which will receive the result of the body when
  completed"
  [& body]
  (let [crossing-env (zipmap (keys &env) (repeatedly gensym))]
    `(let [c# (chan 1)
           captured-bindings# (clojure.lang.Var/getThreadBindingFrame)]
       (dispatch/run
         (^:once fn* []
          (let [~@(mapcat (fn [[l sym]] [sym `(^:once fn* [] ~(vary-meta l dissoc :tag))]) crossing-env)
                f# ~(ioc/state-machine `(do ~@body) 1 [crossing-env &env] cactus.ioc/async-custom-terminators)
                state# (-> (f#)
                           (ioc/aset-all! ioc/USER-START-IDX c#
                                          ioc/BINDINGS-IDX captured-bindings#))]
            (ioc/run-state-machine-wrapped state#))))
       c#)))
