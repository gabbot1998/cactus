(ns cactus.async_ioc_macros
    (:require

      [clojure.core.async.impl.ioc-macros
        :as ioc
        :refer [take! put! return-chan aset-all! run-state-machine-wrapped VALUE-IDX STATE-IDX]
        :exclude [async-custom-terminators]

      ]
      [clojure.core.async.impl.protocols :as impl]
      [cactus.protocols :as cactus.impl]

    )
   (:import [java.util.concurrent.locks Lock])
  )

(defn- fn-handler
  [f]
  (reify
   Lock
   (lock [_])
   (unlock [_])

   impl/Handler
   (active? [_] true)
   (blockable? [_] true)
   (lock-id [_] 0)
   (commit [_] f)))

(def async-custom-terminators
  {'clojure.core.async/<! `take!
   'clojure.core.async/>! `put!
   'cactus.async/<<! `peek!
   'clojure.core.async/alts! 'clojure.core.async/ioc-alts!
   :Return `return-chan})


 (defn peek! [state blk c index]
   (if-let [cb (cactus.impl/peek! c index (fn-handler
                                    (fn [x]
                                      (aset-all! state VALUE-IDX x STATE-IDX blk)
                                      (run-state-machine-wrapped state))))]
     (do (aset-all! state VALUE-IDX @cb STATE-IDX blk)
         :recur)
     nil))
