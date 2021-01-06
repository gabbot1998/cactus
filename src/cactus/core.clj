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




;
; (defentity nw2 [] [in] ==> [output]
;   (defaction in [x] ==>
;     (exec-network
;       (let [feed (feed-one x)
;             rel (relay )
;             end (endport output)
;             ;ending (printboi (connections-map :output))
;             pr (printer "")
;            ]
;            ;(println x)
;         ;(println  "The output is: " (connections-map :output))
;
;         (list
;           (con (feed out) (rel in))
;           (con (rel out) (end out))
;           )
;         )
;       )
;     )
;   )

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



(defn parse-int [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))



(defn -main  [& args]

  (defentity relay-once [] [in] ==> [out]
    (defstate [fired false])
    (defaction in [a] ==> (guard @fired)
        (>>! out a)
        (-- fired true)
      )
    )

  (defentity fanout-cell [] [in] ==> [sw-out next-fo]
    (defaction in [char] ==>
      (>>! sw-out char)
      (>>! next-fo char)
      )
    )

  (defentity has-init-tokens [] [] ==> []
    (defstate [f false])
    (defaction ==> (guard @f)

      )
    )

    ;TODO: Det här är en bugg. Det går inte att ha fler actions.
  ; (defentity hej [a] [in] ==> [out]
  ;   (defstate [v 1])
  ;   (defaction in [x] ==> (guard (= @v 1))
  ;     (>>! out "Action one")
  ;     )
  ;   (defaction in [x] ==> (guard (= x 2))
  ;     (>>! out "Hello from action two.")
  ;     )
  ;   )



  ; (defentity nw1 [] [in] ==> [output]
  ;   (defnetwork in [x] ==>
  ;
  ;       (let [feed (feed-one x)
  ;             end (endport output)
  ;             pr (printer "")
  ;            ]
  ;
  ;         (list
  ;           (con (feed out) (rel in))
  ;           (con (rel out) (end out))
  ;           )
  ;
  ;       )
  ;     )
  ;   )

  (exec-network
    (let [c0 (has-init-tokens )
          c1 (hej "nej")
          p0 (printer "")
          ]

          (list
            (con (c0 out) (c1 in) {:initial-tokens [2]})
            (con (c1 out) (p0 in))
            )

      )
      )

  (while true)
  )
