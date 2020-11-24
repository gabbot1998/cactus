(ns cactus.channels
  (:require
   [clojure.core.async.impl.protocols :as impl]
   [clojure.core.async.impl.channels :as channels :refer [box assert-unlock]]
   [cactus.protocols :as cactus.impl]
   [clojure.core.async.impl.dispatch :as dispatch]
   [clojure.core.async.impl.mutex :as mutex]
   [cactus.buffer :as ring-buffer ] )
  (:import [java.util.concurrent.locks Lock]
           [cactus.buffer ringbuffer]
           )
  )

;; (defmacro did-execute? [body]
;;   `(if (not (= false (try)))
;;      ~body)
;;   (catch Exception e# false
;;          true
;;          false))


(deftype DataFlowChannel [^ringbuffer buf, ^Lock mutex, ^{:volatile-mutable true} depth, ^{:volatile-mutable true} peekhandler]

  cactus.impl/ReadPort
  (peek!
    [this i handler]
    (.lock mutex)
    (set! peekhandler handler)
    (set! depth i)
    (if (< i (.size buf))
      (do
        (println "varf")
        (println, "size is" (.size buf))
        (.unlock mutex)
        (box (.peep buf i)))
      (do
        (.unlock mutex)
        nil))
    )

  impl/ReadPort
  (take!
    [this handler]
    (box (.plop! buf)))

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
            (if (not= peekhandler nil)
              (dispatch/run (peekhandler val))
              nil)
            )
          nil)
        nil)
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


(defn chan [buf]
(DataFlowChannel. buf (mutex/mutex) nil nil)
)
