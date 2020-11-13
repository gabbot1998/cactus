(ns cactus.buffer
  (:require [cactus.protocols :as cactus.impl]
            [clojure.core.async.impl.protocols :as impl]
            )
  (:import [java.util LinkedList Queue])
  )

(deftype FixedBuffer [^LinkedList buf ^long n]
  impl/Buffer
  (full? [this]
    (>= (.size buf) n))
  (remove! [this]
    (.removeLast buf))
  (add!* [this itm]
    (.addFirst buf itm)
    this)
  (close-buf! [this])
  clojure.lang.Counted
  (count [this]
    (.size buf))
  cactus.impl/Buffer
  (look [this index]
    (.get buf index))
    )

(defn fixed-buffer [^long n]
  (FixedBuffer. (LinkedList.) n))
