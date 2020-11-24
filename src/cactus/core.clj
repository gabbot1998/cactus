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


(defmacro)


;given widht 2

(defn stripe-actor [width bs c0 c1 c2 c3] ; c1 - c4 are the channels that the actor sends out on. as is the vector containing the 4 characters that are the current stripe.
   (go
     (loop [];;Set initial state

       (let [
              a1 (<<! bs 0) b1 (<<! bs 1) c1 (<<! bs 2) d1 (<<! bs 3)
              a2 (<<! bs 4) b2 (<<! bs 5) c2 (<<! bs 6) d2 (<<! bs 7)
              ] ;;Wait for ports
              
         (let [] ;;Assign new local state and execute body
           ;(println "B is " local-B)
           (doseq [i (range n)]
             (>! c1 (nth local-B 0))
             (>! c2 (nth local-B 1))
             (>! c3 (nth local-B 2))
             (>! c4 (nth local-B 3))

             )
           (recur ) ;;Recur
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

(entities
  (controller A B width)
  (stripe width)

(network
  (~> (controller :to-stripe) (stripe :bs) 100) ;Optional chanelbuffer size
  (~> (controller :to-fanout) (fanout :a))

  )
)
