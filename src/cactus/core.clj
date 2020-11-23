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



(defactor stripe [width] ;Does nothing with width but uses inside of the action.

  ;bs is suposed to be a string in this actor
  (defaction [bs] ==> [sw-0 sw-1 sw-2 sw-3]
    (doseq [i (range width)]
      (>! sw-0 (nth bs 0))
      (>! sw-1 (nth bs 1))
      (>! sw-2 (nth bs 2))
      (>! sw-3 (nth bs 3))
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
  (~> (controller :to-stripe) (stripe :bs) )
  (~> (controller :to-fanout) (fanout :a))

  )
)
