(def A "DASJJSIJEFSDFSD")
(def B "AAAABBBBCCCCDDDDEEEEFFFFGGGGHHHHIIIIJJJJKKKKLLLLMMMMNNNNOOOOPPPPQQQQRRRRSSSSTTTT")
(def width 4)


;define a new actor that has a parameter list. Possibly doing something with the parameters.
(defactor controller [A B width]
  (let [A (subs 0 2) B (subs 0 (- (count B) (mod (count B) width)))]
    )

  ;define an action for the actor. This is where the ports are defined.
  ;(defaction [input-ports] ==> [output-ports] && guad-pred )
  (defaction [] ==> [to-fanout to-stripe] && true

    )

  )


(defmacro defactor
  ([name args ])
  )

(defn print-actor [in]
  (go-loop [];No initial state
    (let [
          x (<<! in 0)
        ]
        (if true; Default true guard
          ;True
          (do
            (consume-tokens [in 1])
            (println x)
            (recur )
            )
          ;False
          (do
            (recur )
            )
        )
      )
    )
  )



(defactor print-actor [] [in] ==> []
  (defaction :in [x] ==>
    (println x)
     )
  )


(defn consume-tokens [chan n];Has to be called inside of go.
  (doseq [i (range n)]
    (<! chan)
    )
  )

;given widht 2
(defn stripe-actor [width bs sw-0 sw-1 sw-2 sw-3] ; c1 - c4 are the channels that the actor sends out on. as is the vector containing the 4 characters that are the current stripe.
   (go-loop [];;Set initial state in this case no initial state.
        (let [
                d2 (<<! bs 7) c2 (<<! bs 6) b2 (<<! bs 5) a2 (<<! bs 4)
                d1 (<<! bs 3) v1 (<<! bs 2) b1 (<<! bs 1) a1 (<<! bs 0)
              ] ;Peek the ports in the reversed order. For use in the body.

              (if (true);Check the guard. Defaults to true
                ;If the gurad is true
                (do
                  (consume-tokens bs 8)
                  (doseq [i (range width)]
                    (>! sw-0 (nth local-B 0))
                    (>! sw-1 (nth local-B 1))
                    (>! sw-2 (nth local-B 2))
                    (>! sw-3 (nth local-B 3))
                    )
                  (recur );Recur
                  )
                ;Else if the guard is false
                (do
                  (recur );Check the next pattern and guard.
                  )

              )
          )
    )
 )

(defactor stripe [width] [bs] ==> [sw-0 sw-1 sw-2 sw-3] ;Does nothing with width but uses inside of the action.

  ;bs is suposed to be a string in this actor
  (defaction :bs [a, b, c, d] repeat width ==>
    ;This could be any clojure code which would run when the actor is fired.
    (doseq [i (range width)]
      (>! sw-0 a)
      (>! sw-1 b)
      (>! sw-2 c)
      (>! sw-3 d)
      )

    )

  )



(defactor fanout []

  ;a is the char that is supposed to get sent to the sw cells.
  (defaction [a] ==> [sw-0 sw-1 sw-2 sw-3]
    (>! sw-0 a)
    (>! sw-1 a)
    (>! sw-2 a)
    (>! sw-3 a)

    )

  )

(defmacro testmacro
  ([] '(println "Nothing in the list"))
  ([x] `(println ~x))
  ([y & next] `(do (println ~y) (testmacro ~@next)))

  )

  (defactor print-actor [] [in] ==> []
    (defaction in: [x] ==>
      (println x)
       )
    )

(defactor feeder-actor [string n] [] ==> [out]
  (defaction [] ==>
    (doseq [i (range n)]
      (out: string)
      )
    )
  )

(defmacro entities
  ([&actors-then-network]
    (println (reverse actors-then-network)
      )
    )
  )


(defmacro entitieshelper
  ([network]
    `(println "This is just a network")
    )
  ([actor & entities]
      `(do
        (println ~actor)
        (entities ~@entities )
        )
    )
  )

(def testnw
  `(network
    (con (feeder-actor :out) (print-actor :in)) ;Optional chanelbuffer size

    )
  )

;Read the network. For every con make a channel.
;chan-1
;The connections map {:feeder-actor {:out chan-1} :print-actor {:in chan-1}}

;When we instiate actors, check the map for connections. If we dont find the connection, should throw an error.



(let [chan-1 connections {:feeder-actor {:out}}])



(entities
  (feeder-actor "Lorem ipsum dolor sit amet" 5)
  (print-actor )

(network
  (con (feeder-actor :out) (print-actor :in))

  )
)
