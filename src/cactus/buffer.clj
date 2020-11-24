(ns cactus.buffer
  (:require [cactus.protocols :as cactus.impl])
  (:import [java.util ArrayList])
  )

(deftype ringbuffer [size
                     ^{:volatile-mutable true} n
                     ^{:volatile-mutable true} start
                     ^{:volatile-mutable true} end
                     ^{:volatile-mutable true} ^ArrayList buf
                     ^{:volatile-mutable true} capacity
                     wrapper-index]
  cactus.impl/RingBuffer
    (plop!
    [this]
      (if (= (inc @start) @n)
          (let [return-val (.get @buf @start)]
            (.set @buf @start nil)
            (vreset! start 0)
            (vswap! capacity inc)
            return-val)
          (let [return-val (.get @buf @start)]
            (.set @buf @start nil)
            (vswap! start inc)
            (vswap! capacity inc)
            return-val)
            )
         )

  (offer!
    [this e]
      (if (= @capacity @n)
        (do (.set @buf 0 e)
            (vswap! capacity dec)
            (vreset! start 0)
            (vreset! end 0)
            )
        (if (not= @capacity 0)
          (if (= (inc @end) @n)
              (do
                (vreset! end 0)
                (.set @buf @end e)
                (vswap! capacity dec)
                )
              (do
                (.set @buf (inc @end) e)
                (vswap! end inc)
                (vswap! capacity dec)
                ))
          (do
            (let [new-buf (java.util.ArrayList. (range (* 2 @n)))]
              (doseq [i (range @n)]
                (.set new-buf i (.get @buf (wrapper-index (+ @start i)))))
              (.set new-buf @n e)
              (vreset! buf new-buf)
              )
            (vreset! start 0)
            (vreset! end @n)
            (vreset! capacity @n)
            (vswap! capacity dec)
            (vswap! n * 2))
                )
              )
            )
    (peep
      [this i]
      (if (> (inc i) (- @n @capacity))
        (throw (Exception. "peep too deep"))
        (.get @buf (wrapper-index (+ @start i)))
      )
     )
    (size
      [this]
      (- @n @capacity))
    )

;; initalization states for the ringbuff class




