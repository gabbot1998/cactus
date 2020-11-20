(ns cactus.buffer
  (:require [cactus.protocols :as cactus.impl]
            [clojure.core.async.impl.protocols :as impl]
            ;; [cactus.ringbuffer :as ringbuffer :refer  [ringbuffer ring-buffer]]
            )

  (:import java.util.ArrayList )
  )

(deftype ringbuffer [size
                     ^{:volatile-mutable true} n
                     ^{:volatile-mutable true} start
                     ^{:volatile-mutable true} end
                     ^{:volatile-mutable true} ^ArrayList buf
                     ^{:volatile-mutable true} capacity
                     wrapper-index]
  cactus.impl/RingBuffer
    (take!
    [this]
      (if (= (inc @start) @n)
          (let [return-val (.get @buf @start)]
            (.set @buf @start nil)
            (vreset! start 0)
            (vswap! capacity inc)
            return-val)
          (let [return-val (.get @buf @start)]
            (.set @buf @start nil)
            (println "buffer after take" @buf)
            (vswap! start inc)
            (vswap! capacity inc)
            return-val)
            )
         )

  (add!
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
                (println "buffer after add" @buf)
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
            (println "buffer after resizing add" @buf)
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
      (.get @buf (wrapper-index (+ @start i)))
     )
    (size
      [this]
      (- @n @capacity))
    )

;; initalization states for the ringbuff class

(defn ring-buffer [size]
  (let [^{:volatile-mutable true} n (volatile! size)
        ^{:volatile-mutable true} start (volatile! 0)
        ^{:volatile-mutable true} end (volatile! 0)
        ^{:volatile-mutable true} buf (volatile! (java.util.ArrayList. (range size)))
        ^{:volatile-mutable true} capacity (volatile! size)
        wrapper-index (fn [x] (let [m (mod x @n)]
                                     (if (< m 0) (+ m @n) m)))]
    (ringbuffer. size n start end buf capacity wrapper-index)
   )
  )

;;clojures own fixedbbuffer that we change a bit and make it take ring-buff
(deftype FixedBuffer [^ringbuffer buf ^long n]
  impl/Buffer
  (full? [this]
    false) ;;ringbuffer can never be full
  (remove! [this]
    (.take! buf))
  (add!* [this itm]
    (.add! buf itm)
    this)
  (close-buf! [this]);;????
  clojure.lang.Counted
  (count [this]
    (.size buf))
  cactus.impl/Buffer
  (look [this index]
    (.peep buf index))
    )

(defn fixed-buffer [^long n]
  (FixedBuffer. (ring-buffer 5) n)) ;;kan man skick med en long? default value 40 längd
