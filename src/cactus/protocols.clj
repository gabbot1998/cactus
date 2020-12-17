(ns cactus.protocols)


(defprotocol ReadPort
  (peek! [port index fn1-handler] "derefable val if peeked, nil if peek was enqueued")
  (take! [port fn1-handler])
  (size [port n fn1-handler]))

(defprotocol WritePort
(put! [port element fn1-handler]) 
)

(defprotocol Handler
  (size-depth [h])
  (fun [h])
  )

(defprotocol RingBuffer
  (plop! [b])
  (offer! [b e])
  (peep [b i])
  (len [b])
  )

;; (defprotocol Buffer
;; (look [b i handler])
;; (take! [b handler])
;; (add! [b e handler])
;; )
