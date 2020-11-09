;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

(ns cactus.core
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! go chan buffer close! thread]
             ]

             [actors.sw_cell
             :as sw-cell
             :refer [sw-cell]
             ]

             [actors.aligner
             :as aligner
             :refer [aligner]
             ]

             [actors.fan_out_actor
             :as fan-out-actor
             :refer [fan-out-actor]
             ]

             [actors.print_actor
             :as print-actor
             :refer [print-actor]
             ]

             [actors.controller
             :as controller
             :refer [controller]
             ]
             )
   )



(def chan-con-1-zero (chan 50))

(def chan-con-1 (chan 50))
(def chan-con-2 (async/chan 50))
(def chan-con-3 (async/chan 50))
(def chan-con-4 (async/chan 50))

(def chan-con-b (chan 50))
(def chan-con-b1 (chan 50))
(def chan-con-b2 (async/chan 50))
(def chan-con-b3 (async/chan 50))
(def chan-con-b4 (async/chan 50))

(def chan-1-2 (async/chan 50))
(def chan-2-3 (async/chan 50))
(def chan-3-4 (async/chan 50))

(def chan-4-print (async/chan 50))

(def chan-str-1 (chan 50))
(def chan-str-2 (chan 50))

(def chan-aln-1 (chan 50))
(def chan-aln-2 (chan 50))
(def chan-aln-3 (chan 50))
(def chan-aln-4 (chan 50))

(def chan-stop (chan 50))


(defn -main  [& args]
    (print-actor chan-4-print)

    (sw-cell chan-con-1 chan-con-b1  chan-con-1-zero chan-1-2 chan-aln-1 "0")
    (sw-cell chan-con-2 chan-con-b2  chan-1-2 chan-2-3 chan-aln-2 "1")
    (sw-cell chan-con-3 chan-con-b3  chan-2-3  chan-3-4 chan-aln-3 "2")
    (sw-cell chan-con-4 chan-con-b4  chan-3-4 chan-stop chan-aln-4 "3")

    (aligner chan-aln-1 chan-aln-2 chan-aln-3 chan-aln-4 chan-4-print)


    (fan-out-actor chan-con-b chan-con-b1 chan-con-b2 chan-con-b3 chan-con-b4)

    (>!! chan-str-1 "abb")
    (>!! chan-str-2 "aaa")

    (<!! (controller chan-str-1 chan-str-2 chan-con-1 chan-con-2 chan-con-3 chan-con-4 chan-con-b chan-con-1-zero))

 )
