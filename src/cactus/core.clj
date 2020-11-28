(ns cactus.core
  (:gen-class)
  (:require
   [clojure.core.async
    :as async
    :refer [ <! >! <!! >!! timeout]
    :exclude [chan go]
    ]

   [cactus.async
    :as cactus.async
    :refer [size? go <<! chan]
    ]

     [cactus.actor_macros
     :as cactus.actors
     :refer [defactor entities actor connection network defaction >>! guard]
     ]

     )
   )

   ;TODO
   ;1. Get a loop going inside the defactor XXX;

   ;2. Create a function that counts the number of bindings in each channel. XXX

   ;3,5. Create the while that checks for the availability of the elements. XXX


   ;3. Implement the guard statements inside of defaction X

   ;4. Create the consume and return function that consumes tokens from the inputs.

   ;5. create the let statement and bind the bindingsvector XXX

   ;6. Add the body into execution. XXX

   ;7. First version of cactus is done.

   ;Försök hinna detta efter lunch.

   ;8. Send the update to Jorn and ask him to try it out.
   ;9. Rejoice for the weekend.






; (defactor print-actor [] [in] ==> []
;     (while (< 1 (size? (connections-map :in)) ) (println "There is nothing on the channel"))
;     (println (<! (connections-map :in)))
;   )


; The while expands to:
; (clojure.core/while
;   (and
;     (or false
;       (clojure.core/< (clojure.core/count (quote [str])) (cactus.async/size? ((quote (clojure.core/symbol connections-map)) :in)))
;       )) (clojure.core/println Still no tokens))


; (and
;   (or
;     (clojure.core/< (clojure.core/count (quote [str])) (cactus.async/size? ((clojure.core/symbol connections-map) (clojure.core/keyword (quote in))))
;       )
;     )
;   )

; (defactor print-actor [] [in] ==> []
;   (defaction in [str] ==>
;     (println str)
;     )
;
;   ;This should be in defactor
;   (go
;     (loop [];no state
;       (while (or
;                   (and (< (size? (connections-map :in) (count bindingsvector))) );Check the number of tokens for all the ports needed for the action
;                   ;Potentially check other actions.
;
;                   )
;
;                   )
;
;       ;This should expand inside defaction.
;       (when guard-statement-this-action ;Check the guard for this action
;         (let [bindingsvector (consume-and-return (connections-map :in) (count bindingsvector))] ;Consume tokens and bind them to the correct bindings
;           ;Execute the body
;         )
;
;       )
;
;       (recur ); This is where the updating of the state should happen.
;       )
;     )
;
;   )


(defactor print-actor [] [in-0 in-1] ==> []
  (defaction in-0 [a b] in-1 [c] ==> (guard (= a "hej"))
    (println "a, b, c: " a ", " b ", " c)
    (<! (connections-map :in-0))
    (<! (connections-map :in-0))
    (<! (connections-map :in-1))
    ;(println "Det finns två tokens.")
    )

  (defaction in-0 [d e f] ==>
    (println "Cosnuming three on in-0")
    (<! (connections-map :in-0))
    (<! (connections-map :in-0))
    (<! (connections-map :in-0))
    ;(println "Det finns tre tokens.")
    )

  )

(defactor feed-once [x y] [] ==> [out]
  (defaction ==>
      (>>! out x)
      (>>! out y)
      (>>! out x)
      (>>! out x)
      (>>! out x)
      (while true)
    )
  )

(defn -main  [& args]

  (entities
    (actor feeder-0 (feed-once "hej" "second"))
    (actor feeder-1 (feed-once "Supposed to be C" "Supposed to be C"))
    (actor printer (print-actor ))

    (network
      (connection (feeder-0 :out) (printer :in-0) )
      (connection (feeder-1 :out) (printer :in-1) )
      )
    )


  (while true )

 )



 ; ;For this actor specifically we want to read all the inputs. Find the ==>.
 ; (defactor feeder-actor [string n] [] ==> [out]
 ;   (defaction [] ==>
 ;     (doseq [i (range n)]
 ;       (>>! out string)
 ;       )
 ;     )
 ;   )


 ; (defactor feed-actor [str n] [] ==> [out]
 ;   (go
 ;     (doseq [i (range n)]
 ;       (>>! out str)
 ;       )
 ;     )
 ;   )
 ;
 ; (defactor feed-once [str] [] ==> [out]
 ;   (defaction ==>
 ;       (>>! out str)
 ;       (>>! out str)
 ;       (>>! out str)
 ;       (>>! out str)
 ;     )
 ;   )

 ; (defactor print-actor [] [in-1 in-2] ==> []
 ;   (defaction :in-1 [x] :in-2 [y] ==>
 ;     (println x)
 ;      )
 ;   )

 ; (defactor feed-once [str] [] ==> [out]
 ;   (go
 ;     (loop []
 ;
 ;       (>>! out str)
 ;
 ;       (recur )
 ;       )
 ;     )
 ;   )



 ; (defactor print-actor [] [in] ==> []
 ;   (loop []
 ;     (while (< (size? (connections-map :in)) 1))
 ;     (println (<! (connections-map :in)))
 ;     (recur )
 ;     )
 ;   )

 ; (defactor print-two-actor [] [in-0 in-1] ==> []
 ;   (go
 ;     (println "consuming")
 ;     (loop []
 ;       (println
 ;       (<! (connections-map :in-0))
 ;       (<! (connections-map :in-1))
 ;       )
 ;     (recur )
 ;     )
 ;     )
 ;   )

 ; (defactor print-actor [] [in] ==> []
 ;   (defaction :in [x] ==>
 ;     (println x)
 ;      )
 ;   )

 ; (defmacro actor
 ;   )

 ; (defn print-actor [in]
 ;   (go-loop [];No initial state
 ;     (let [
 ;           x (<<! in 0)
 ;         ]
 ;         (if true; Default true guard
 ;           ;True
 ;           (do
 ;             (consume-tokens [in 1])
 ;             (println x)
 ;             (recur )
 ;             )
 ;           ;False
 ;           (do
 ;             (recur )
 ;             )
 ;         )
 ;       )
 ;     )
 ;   )

 ; (defactor print-two-actor [] [in-1 in-2] ==> []
 ;     (defaction in-1: [x] in-2: [y]  ==>
 ;       (println x)
 ;       (println y)
 ;       )
 ;     )
 ;
