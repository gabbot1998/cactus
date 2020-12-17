(ns cactus.async_ioc_macros
  (:require
   [clojure.core.async.impl.ioc-macros
    :as ioc
    :refer [return-chan aset-all! run-state-machine-wrapped VALUE-IDX STATE-IDX]
    :exclude [async-custom-terminators]
    ]
   [clojure.core.async.impl.protocols :as impl]
   [cactus.protocols :as cactus.impl]
   )
  (:import [java.util.concurrent.locks Lock])
  )

;; (defn- fn-handler
;;   [n f]
;;   cactus.impl/cactus.protocols.Handler/
;;   (size-depth [_] n)
;;   (fun [_] f)
;;   )
(defn- fn-handler
  [depth f]
  (reify
    Lock
    (lock [_])
    (unlock [_])

    impl/Handler
    (active? [_] true)
    (blockable? [_] true)
    (lock-id [_] 0)
    (commit [_] f)
    cactus.impl/Handler
    (fun [_] f)
    (size-depth [_] depth)))


(def async-custom-terminators
  {'clojure.core.async/<! `take!
   'clojure.core.async/>! `put!
   'cactus.async/<<! `peek!
   'cactus.async/size? `size
   'clojure.core.async/alts! 'clojure.core.async/ioc-alts!
   :Return `return-chan})

(defn take! [state blk c]
  (if-let [cb (cactus.impl/take! c (fn-handler 0
                             (fn [x]
                               (aset-all! state VALUE-IDX x STATE-IDX blk)
                               (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn put! [state blk c val]
  (if-let [cb (cactus.impl/put! c val (fn-handler 0
                                (fn [ret-val]
                                  (aset-all! state VALUE-IDX ret-val STATE-IDX blk)
                                  (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn size [state blk c n]
  (if-let [cb (cactus.impl/size c n (fn-handler n
                                    (fn [x]
                                      (aset-all! state VALUE-IDX x STATE-IDX blk)
                                      (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))


(defn peek! [state blk c index]
  (if-let [cb (cactus.impl/peek! c index (fn-handler 0
                                          (fn [x]
                                            (aset-all! state VALUE-IDX x STATE-IDX blk)
                                            (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))
