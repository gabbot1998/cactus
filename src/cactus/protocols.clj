(ns cactus.protocols)

(defprotocol ReadPort
  (peek! [port index fn1-handler] "derefable val if peeked, nil if peek was enqueued")
  )

(defprotocol Buffer
  (look [buf i])
  )

(defprotocol RingBuffer
  (take! [buf] "return next item from buffer, called under chan mutex")
  (add! [buf i])
  (peep [buf i]);;named peep to not overwrite clojures own peep
  (size [buf])
  )


