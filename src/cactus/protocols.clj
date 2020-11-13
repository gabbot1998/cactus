(ns cactus.protocols)

(defprotocol ReadPort
  (peek! [port index fn1-handler] "derefable val if peeked, nil if peek was enqueued")
  )

(defprotocol Buffer
(look [b i] "return next item from buffer, called under chan mutex")
)
