(ns cactus.protocols)

(defprotocol ReadPort
  (peek! [port fn1-handler] "derefable val if peeked, nil if peek was enqueued")
  )

(defprotocol Buffer
(look [b] "return next item from buffer, called under chan mutex")
)
