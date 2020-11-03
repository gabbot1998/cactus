;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

(ns cactus.core
  (:gen-class)
  (:require [clojure.core.async
             :as async
             :refer [>! <! >!! <!! go chan buffer close! thread


(defn action [arg state]
        (+ arg state))

(def chan-1 (async/chan))
(def chan-2 (async/chan))

(defn actor [chan-1 chan-2]
  (let [action ()] )
  (go (>!! chan-2 (<!! chan-1)))
 )

(defn score [a b]
  (if (= a "") 0
    (if (= b "") 0
      (if (= a b) 5 -1 )
    )
  )
)

(defn penalty 1)


(defn sw-cell [a b w]
  (go (
    (def ^:dynamic state '(0 0));;(n nw w)
    (def ^:dynamic v1 0)
    (def ^:dynamic v2 0)
    (def ^:dynamic v3 0)

    (binding [
      v1 (+ (second state ) (score (<!! a) (<!! b))
      v2 (+ (first state) (penalty))
      v3 (+ (first state) (penalty))
      ])

      (binding
        [state '(
            (first state)
            (<!! w)
            ;; (binding state '(n nw))
          )
        ]
      )
      (binding [state '(
        (max
          (+ (second state ) (score (<!! a) (<!! b)));;nw + score(i, j)
          (+ (first state) (penalty));;w + delta
          (+ (first state) (penalty));;n + delta
          0
        )
        (second state)
        )])
    ))
  )


(defn -main  [& args]
  (while true
  (actor chan-1 chan-2)
  (print-actor chan-2)
  (>!! chan-1 (Integer/parseInt (read-line) )
  )))
