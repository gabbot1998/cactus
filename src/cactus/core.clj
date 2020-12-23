(ns cactus.core
  (:gen-class)

  (:require

   [clojure.core.async
    :as async
    :refer [<! >!]
    ]

   [cactus.async
    :as cactus.async
    :refer [size? go <<! chan]
    ]

     [cactus.actor_macros
     :as cactus.actors
     :refer [defentity con defaction >>! guard defstate -- exec-network defnetwork endport]
     ]

     [actors.cactus_actors
      :as cactus.actors.cactus_actors
      :refer [sw-cell sw-cell-printing align-actor controller-actor fanout-actor stripe-actor]
      ]

      [cactus.matrix
      :as matrix
      :refer [cm]
      ]
      [clojure.string :as str]

     )
   )
(import java.util.Date)

(defn append-to-file
  "Uses spit to append to a file specified with its name as a string, or
   anything else that writer can take as an argument.  s is the string to
   append."
  [file-name s]
  (spit file-name s :append true))

(defentity feed-one [send] [] ==> [out]
  (defstate [fired true])
  ;(println "fired")
  (defaction ==> (guard @fired)
      (-- fired false)
      ;(println "fired")
      (>>! out send)
    )

  )

(defentity finnish-line [width A-len B-len] [in] ==> []
  (defstate [index 0 target (* A-len (/ B-len width) )])
  (defaction in [r] ==>
    (-- index (inc @index))
    (when (= @index @target)
      (def date (.getTime (java.util.Date.)))
      (append-to-file "res.txt" (str "\n" date ))
      (System/exit 0)
      )
    )
  )

(defentity incr [i] [in] ==> [out]
  ;(println "incr" i "\n\n\n")
  (defaction in [a] ==>
    (println "incremented" a)
    (>>! out (inc a))
    )
  )

(defentity printer [prefix] [in] ==> []
  ;(println "printer")
  (defaction in [token] ==>
      (println prefix token)
    )
  )

(defentity relay [] [in] ==> [out]
  (defaction in [a] ==>
      (>>! out a)
    )
  )

(defentity nw1 [] [in] ==> [output]
  (defnetwork in [x] ==>

      (let [feed (feed-one x)
            rel (relay )
            end (endport output)
            pr (printer "")
           ]

        (list
          (con (feed out) (rel in))
          (con (rel out) (end out))
          )

      )
    )
  )

(defentity nw2 [] [in] ==> [output]
  (defaction in [x] ==>
    (exec-network
      (let [feed (feed-one x)
            rel (relay )
            end (endport output)
            ;ending (printboi (connections-map :output))
            pr (printer "")
           ]
           ;(println x)
        ;(println  "The output is: " (connections-map :output))

        (list
          (con (feed out) (rel in))
          (con (rel out) (end out))
          )
        )
      )
    )
  )

(defentity buf [] [in] ==> []
  (defaction in [a] ==>

    )
  )

(defentity collector-cell [] [score vector] ==> [out]
  (defaction score [s] vector [res] ==>
    (>>! out (conj res s))
    )
  )

(defentity verifying-cell [cm A-len B-len width] [in] ==> []
  (defstate [row 0 col 0])
  (defaction in [r] ==>
    (if (= A-len @row)
      (do
        (-- row 0)
        (-- col (+ @col width))
        )
      (do
        ;(-- col (+ col (count r)))
        )
        )
      (doseq [i (range (count r))]
        (if (= (nth r i) (nth (nth cm @row) (+ @col i)))
          (do
            (println "This element was correct" @row (+ @col i) )
            ; (println "from the matrix" (nth (nth cm @row) (+ @col i)))
            ; (println "From the circuit" (nth r i))
            )
          (do
            (println "This element was incorrect" @row (+ @col i))
            ; (println "from the matrix" (nth (nth cm @row) (+ @col i)))
            ; (println "From the circuit" (nth r i))
            )
          )
        )

      (-- row (inc @row))

    )
  )

(defentity has-init-tokens [] [] ==> []
  (defstate [f false])
  (defaction ==> (guard @f)

    )
  )

(defn parse-int [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))

(defn mean-pairs [list]
  (loop [first (Long/valueOf (nth list 0))
         second (Long/valueOf (nth list 1))
         rest-list list
         accumulator 0
         number 10
        ]

        (if (= number 1)
          (println (double (/ accumulator 10)))
          (recur (Long/valueOf (nth (rest (rest rest-list)) 0)) (Long/valueOf (nth (rest (rest rest-list)) 1)) (rest (rest rest-list)) (+ accumulator (- second first)) (dec number))
          )
    )
  )

(defn -main  [& args]
  (def s (slurp "res.txt"))
  (def sp (filter (fn [x] (not= "" x)) (str/split s #"\n")))
  (def s1 (take 20 sp))
  (def s2 (take 20 (drop 20 sp)))
  (def s3 (take 20 (drop 40 sp)))
  (def s4 (take 20 (drop 60 sp)))
  (def s5 (take 20 (drop 80 sp)))
  (def s6 (take 20 (drop 100 sp)))
  (def s7 (take 20 (drop 120 sp)))
  (def s8 (take 20 (drop 140 sp)))
  (def s9 (take 20 (drop 160 sp)))
  (def s10 (take 20 (drop 180 sp)))

  (mean-pairs s1)
  (mean-pairs s2)
  (mean-pairs s3)
  (mean-pairs s4)
  (mean-pairs s5)
  (mean-pairs s6)
  (mean-pairs s7)
  (mean-pairs s8)
  (mean-pairs s9)
  (mean-pairs s10)
  ;(println s2)

  (while true)
  )
