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
(defn- fn-handler [f]
  f)
;; (defn- fn-handler
;;   [f]
;;   (reify
;;     Lock
;;     (lock [_])
;;     (unlock [_])

;;   impl/Handler
;;   (active? [_] true)
;;   (blockable? [_] true)
;;   (lock-id [_] 0)
;;   (commit [_] f)
;;   ;; cactus.impl/Handler
;;   ;; (peek? [_] isPeek)
;;   ;; (peek-depth [_] depth)
;;   )
;; )

(def async-custom-terminators
  {'clojure.core.async/<! `take!
   'clojure.core.async/>! `put!
   'cactus.async/<<! `peek!
   'cactus.async/size? `size
   'clojure.core.async/alts! 'clojure.core.async/ioc-alts!
   :Return `return-chan})

(defn take! [state blk c]
  (if-let [cb (impl/take! c (fn-handler
                             (fn [x]
                               (aset-all! state VALUE-IDX x STATE-IDX blk)
                               (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn put! [state blk c val]
  (if-let [cb (impl/put! c val (fn-handler
                                (fn [ret-val]
                                  (aset-all! state VALUE-IDX ret-val STATE-IDX blk)
                                  (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))


(defn size [state blk c n]
  (if-let [cb (cactus.impl/size c n (fn-handler
                                     (fn [x]
                                       (aset-all! state VALUE-IDX x STATE-IDX blk)
                                       (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))

(defn peek! [state blk c index]
  (if-let [cb (cactus.impl/peek! c index (fn-handler
                                          (fn [x]
                                            (aset-all! state VALUE-IDX x STATE-IDX blk)
                                            (run-state-machine-wrapped state))))]
    (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
        :recur)
    nil))
