;;"marcus"
;;"cactus"
;;match = 5
;;mismatch = -1
;;space = 0

(ns cactus.core
  (:gen-class)
(:require
            [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! buffer close! thread]
             :exclude [chan go]
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

             ; [actors.print_actor
             ; :as print-actor
             ; :refer [print-actor]
             ; ]

             [actors.guarded_actor
             :as guarded_actor
             :refer [guarded-actor]
             ]

             [actors.controller
             :as controller
             :refer [controller]
             ]

             [actors.dataflow_channel
             :as dataflow-channel
             :refer []
             ]

             [cactus.async
             :as cactus.async
             :refer [go <<! chan]
             ]

             )
   )

(def in '((con (feeder-2 :out) (print-actor :in-2)) (con (feeder-1 :out) (print-actor :in-1)) network))

(def chan-size :chan-size)
(def standard-chan-size 50)

;Takes connections in the form of (def in '((con (feeder-2 :out) (print-actor :in-2)) network))
;(con (feeder-1 :out) (print-actor :in-1))

(defn return-chan-size [arg-map]
  (if (= arg-map clojure.lang.PersistentArrayMap)
    (if
      (not= (arg-map :chan-size) nil)
        (arg-map chan-size)
        standard-chan-size
      )
      standard-chan-size
    )
  )

(defn is-nil? [map key1 key2]
  (if (= (map key1) nil)
    true
    (= ((map key1) key2) nil)
    )
  )

(defn assoc-connections [connections-map con-0 con-1 channel]
  (let [
        connector-0 (keyword (first con-0))
        connector-1 (keyword (first con-1))
        port-0 (second con-0)
        port-1 (second con-1)
        ]

        (if (is-nil? connections-map connector-0 port-0)
          ;If there is nothing connected to this port
          (if (is-nil? connections-map connector-1 port-1)
            (assoc connections-map
              connector-0 (conj {port-0 channel} (connections-map connector-0))
              connector-1 (conj {port-1 channel} (connections-map connector-1))
              )
            (throw (Exception. (str "There is already a channel connected to the actor: " connector-1 ", at port: " port-1)))
            )
          ;If the connection is already established
          (throw (Exception. (str "There is already a channel connected to the actor: " connector-0 ", at port: " port-0)))
          )
    )
  )

(defn create-connections-map [network]
  (loop [
          i 0
          connection (first network)
          rest-nw (rest network)
          connections-map {}

          arguments-map (if (= (class connection) clojure.lang.PersistentList) (last connection) nil)
          connector-0 (if (= (class connection) clojure.lang.PersistentList) (nth connection 1 nil) nil)
          connector-1 (if (= (class connection) clojure.lang.PersistentList) (nth connection 2 nil) nil)

        ]

        (if (not= rest-nw ())
          ;If we have not reached the network token we are not done.
          (let [
                new-connection (first rest-nw)
                new-rest-nw (rest rest-nw)
                new-channel (symbol (str "channel-" i))
                new-connections-map (assoc-connections connections-map connector-0 connector-1 new-channel)

                new-arguments-map (if (= (class new-connection) clojure.lang.PersistentList) (last new-connection) nil)
                new-connector-0 (if (= (class new-connection) clojure.lang.PersistentList) (nth new-connection 1 nil) nil)
                new-connector-1 (if (= (class new-connection) clojure.lang.PersistentList) (nth new-connection 2 nil) nil)
                ]

            (recur (inc i) new-connection new-rest-nw new-connections-map new-arguments-map new-connector-0 new-connector-1)
            )

            ;If we have reached the network token we return the connections-map
            (assoc connections-map :number-of-channels i)

          )
      )
  )

(defn network-builder
  ([network]
      (if (not= (first network) 'network)
        (throw (Exception. (str "The last list inside entities has to be a network.")))
        (create-connections-map (reverse network))
      )

    )
  )

; (defactor print-actor [] [in] ==> []
;   (defaction :in [x] ==>
;     (println x)
;      )
;   )



; (defmacro actor [name actor]
;
;   )


;Read the list. Parse the network. Use the symbol after the actor keyword as the key in the connections map.
;Expand the list after the keyword as an actor. Use the correct actor defined using the defactor macro.

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




; (defmacro defactor
;   ([name & rest]
;     (println name)
;     )
;   )


; (defactor print-actor [] [in] ==> []
;   (defaction :in [x] ==>
;     (println x)
;      )
;   )

;The actor macro should treat argument one as the key to the connections map, argument two as the list containing: 1. The name of the actor, &2. the rest of the arguments. These should be fed to the actor instant.

;defaction should take
; (defaction :in [x] ==>
;   (println x)
;    )

; (defmacro actor-expander
;   [actor-spec connections-map]
;     `(reverse (cons ~connections-map (reverse ~@actor-spec)))
;   )

(defn actor-expander
  [actor-list connections]
    (let [var-name (second actor-list)
          actor-spec (nth actor-list 2)
          connections-map (connections (keyword var-name))
          ]

          (reverse (cons connections-map (reverse actor-spec)))

      )
  )


; (defmacro entities
;  ([& actors-then-network]
;    (let [s '(1 2 3)]
;
;    `(println "wow")
;    )
;   )
;   )

(defn create-channel-constructor-calls
  [n]
  (loop [
          i (dec n)
          accumulator '()
          ]
          (if (< i 0)
            accumulator
            (recur (dec i) (conj accumulator `( ~(symbol (str "channel-" i)) (chan 50)) ))
            )
    )
  )

(defn create-let-with-channels [n body]
    `(let ~(vec (apply concat (create-channel-constructor-calls n)))
      ~(conj body 'do)
    )
  )

(defmacro entities
 ([& actors-then-network]
    ;(println "\nThe network is currently: " (network-builder (first (reverse actors-then-network))) "\n")
    ;(println (first (butlast actors-then-network)))
    (let [
          connections (network-builder (first (reverse actors-then-network)))
          actors-list (butlast actors-then-network)
          calls-to-actors (loop [
                new-actors-list actors-list
                accumulator '()
                ]

                (if (= '() new-actors-list)
                  accumulator
                  (recur (rest new-actors-list) (conj accumulator (actor-expander (first new-actors-list) connections)))
                )
              )
          execute (create-let-with-channels (connections :number-of-channels) calls-to-actors)
          test '(chan 50)
          test2 '(do (println {:a (chan 50)}) (println "wassa") )
          ]

        `(do
          ~execute
          (println "All actors expanded")
          (while true)
          )


      )
    )
   )

; [x__10637__auto__ [:feed-once {:out #object[cactus.channels.ManyToManyChannel 0xbd8f424 "cactus.channels.ManyToManyChannel@bd8f424"]}]
;                   [:printer {:in #object[cactus.channels.ManyToManyChannel 0xbd8f424 "cactus.channels.ManyToManyChannel@bd8f424"]}]
; ]

; [cactus.core/conns [:feed-once {:out #object[cactus.channels.ManyToManyChannel 0x620ba2b0 "cactus.channels.ManyToManyChannel@620ba2b0"]}]
;                     [:printer {:in #object[cactus.channels.ManyToManyChannel 0x620ba2b0 "cactus.channels.ManyToManyChannel@620ba2b0"]}]
;                     ]
; (do
;   (print-actor {:in #object[cactus.channels.ManyToManyChannel 0x3c0b9643 cactus.channels.ManyToManyChannel@3c0b9643]})
;   (feed-actor what {:out #object[cactus.channels.ManyToManyChannel 0x3c0b9643 cactus.channels.ManyToManyChannel@3c0b9643]})
;   )

; (do
;   (print-actor {:in #object[cactus.channels.ManyToManyChannel 0x18dda2e1 cactus.channels.ManyToManyChannel@18dda2e1]})
;   (feed-actor what {:out #object[cactus.channels.ManyToManyChannel 0x18dda2e1 cactus.channels.ManyToManyChannel@18dda2e1]})
;   )


(defmacro defactor
  [name parameters connections-in arrow connections-out & actions]
  ;(println (vec (conj parameters (symbol "connections-in-map") (symbol "connections-out-map"))))
  `(defn ~(symbol name) ~(vec (conj parameters 'connections-map)) ~@actions)
  )

(defn -main  [& args]




  (defactor feed-actor [str] [] ==> [out]
    (go
      (doseq [i (range 100)]
        (>! (connections-map :out) str)
        )
      )
    )

  (defactor print-actor [] [in] ==> []
    (go
      (println (<! (connections-map :in)))
      )
    )

  (defactor print-two-actor [] [in-0 in-1] ==> []
    (go
      (loop []
      (println (<! (connections-map :in-0)) (<! (connections-map :in-1)))
      (recur )
      )
      )
    )



  (entities
    ('actor feeder-0 (feed-actor "Printing 0" ))
    ('actor feeder-1 (feed-actor "Printing 1" ))

    ('actor print-two   (print-two-actor ))

    (network
      (connection (feeder-0 :out) (print-two :in-0))
      (connection (feeder-1 :out) (print-two :in-1))
      )
    )


  (while true )

  ; (entities
  ;    (actor feeder-actor-0 (feeder "Lorem ipsum dolor sit amet" 5))
  ;    (actor feeder-actor-1 (feeder "Lorem ipsum dolor sit amet" 5))
  ;    (actor print-actor (print-two-actor ))
  ;
  ;  (network
  ;   (connection (feeder-actor-0 :out) (print-actor :in-0) {:chan-size 1000} )
  ;   (connection (feeder-actor-1 :out) (print-actor :in-1) {} )
  ;
  ;    )
  ;  )
 )


; ((()
; feed-actor what {:out #object[cactus.channels.ManyToManyChannel 0x2b39e77d cactus.channels.ManyToManyChannel@2b39e77d]})
; print-actor {:in #object[cactus.channels.ManyToManyChannel 0x2b39e77d cactus.channels.ManyToManyChannel@2b39e77d]}
; )


  ;Read the network. For every con make a channel.
  ;chan-1
  ;The connections map {:feeder-actor {:out chan-1} :print-actor {:in chan-1}}

  ;When we instiate actors, check the map for connections. If we dont find the connection, should throw an error.

  ; (defactor print-two-actor [] [in-1 in-2] ==> []
  ;     (defaction in-1: [x] in-2: [y]  ==>
  ;       (println x)
  ;       (println y)
  ;       )
  ;     )
  ;
  ; (defactor feeder-actor [string n] [] ==> [out]
  ;   (defaction [] ==>
  ;     (doseq [i (range n)]
  ;       (out: string)
  ;       )
  ;     )
  ;   )



  ; (let [
  ;       ;These two should be generated by the network entity but they only live inside of the connections-map
  ;       ;chan-1 (chan 50)
  ;       ;chan-2 (chan 50)
  ;       ;This sould also be generated by the network macro
  ;       connections-map {:feeder-1 {:out chan-1} :print-actor {:in-1 chan-1 :in-2 chan-2} :feeder-2 {:out chan-2}}
  ;       ]
  ;
  ;       ; ------------- feeder-1 --------------------------
  ;       ; should return the expanded version of this, where
  ;       ; (defactor feeder-actor [string n] [] ==> [out]
  ;       ;   (defaction [] ==>
  ;       ;     (doseq [i (range n)]
  ;       ;       (out: string)
  ;       ;       )
  ;       ;     )
  ;       ;   )
  ;
  ;       ; ------------- feeder-2 --------------------------
  ;       ; should return the expanded version of this, where
  ;       ; (defactor feeder-actor [string n] [] ==> [out]
  ;       ;   (defaction [] ==>
  ;       ;     (doseq [i (range n)]
  ;       ;       (out: string)
  ;       ;       )
  ;       ;     )
  ;       ;   )
  ;
  ;   )
  ;
  ;
  ;
  ; (entities
  ;   (def feeder-1 (feeder-actor "Lorem ipsum dolor sit amet" 5))
  ;   (def feeder-2 (feeder-actor "Lorem ipsum dolor sit amet" 5))
  ;   (def print-actor (print-two-actor))
  ;
  ; (network
  ;   (con (feeder-1 :out) (print-actor :in-1))
  ;   (con (feeder-2 :out) (print-actor :in-2))
  ;
  ;   )



;
; ;define a new actor that has a parameter list. Possibly doing something with the parameters.
; (defactor controller [A B width]
;   (let [A (subs 0 2) B (subs 0 (- (count B) (mod (count B) width)))]
;     )
;
;   ;define an action for the actor. This is where the ports are defined.
;   ;(defaction [input-ports] ==> [output-ports] && guad-pred )
;   (defaction [] ==> [to-fanout to-stripe] && true
;
;     )
;
;   )
;
;
; (defmacro defactor
;   ([name args ])
;   )
;
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
;
;
;
; (defactor print-actor [] [in] ==> []
;   (defaction :in [x] ==>
;     (println x)
;      )
;   )
;
;
; (defn consume-tokens [chan n];Has to be called inside of go.
;   (doseq [i (range n)]
;     (<! chan)
;     )
;   )
;
; ;given widht 2
; (defn stripe-actor [width bs sw-0 sw-1 sw-2 sw-3] ; c1 - c4 are the channels that the actor sends out on. as is the vector containing the 4 characters that are the current stripe.
;    (go-loop [];;Set initial state in this case no initial state.
;         (let [
;                 d2 (<<! bs 7) c2 (<<! bs 6) b2 (<<! bs 5) a2 (<<! bs 4)
;                 d1 (<<! bs 3) v1 (<<! bs 2) b1 (<<! bs 1) a1 (<<! bs 0)
;               ] ;Peek the ports in the reversed order. For use in the body.
;
;               (if (true);Check the guard. Defaults to true
;                 ;If the gurad is true
;                 (do
;                   (consume-tokens bs 8)
;                   (doseq [i (range width)]
;                     (>! sw-0 (nth local-B 0))
;                     (>! sw-1 (nth local-B 1))
;                     (>! sw-2 (nth local-B 2))
;                     (>! sw-3 (nth local-B 3))
;                     )
;                   (recur );Recur
;                   )
;                 ;Else if the guard is false
;                 (do
;                   (recur );Check the next pattern and guard.
;                   )
;
;               )
;           )
;     )
;  )
;
; (defactor stripe [width] [bs] ==> [sw-0 sw-1 sw-2 sw-3] ;Does nothing with width but uses inside of the action.
;
;   ;bs is suposed to be a string in this actor
;   (defaction :bs [a, b, c, d] repeat width ==>
;     ;This could be any clojure code which would run when the actor is fired.
;     (doseq [i (range width)]
;       (>! sw-0 a)
;       (>! sw-1 b)
;       (>! sw-2 c)
;       (>! sw-3 d)
;       )
;
;     )
;
;   )
;
;
;
; (defactor fanout []
;
;   ;a is the char that is supposed to get sent to the sw cells.
;   (defaction [a] ==> [sw-0 sw-1 sw-2 sw-3]
;     (>! sw-0 a)
;     (>! sw-1 a)
;     (>! sw-2 a)
;     (>! sw-3 a)
;
;     )
;
;   )
;
; (defmacro testmacro
;   ([] '(println "Nothing in the list"))
;   ([x] `(println ~x))
;   ([y & next] `(do (println ~y) (testmacro ~@next)))
;
;   )
;
;   (defactor print-actor [] [in] ==> []
;     (defaction in: [x] ==>
;       (println x)
;        )
;     )
;
; (defactor feeder-actor [string n] [] ==> [out]
;   (defaction [] ==>
;     (doseq [i (range n)]
;       (out: string)
;       )
;     )
;   )
;
; (defmacro entities
;   ([&actors-then-network]
;     (println (reverse actors-then-network)
;       )
;     )
;   )
;
;
; (defmacro entitieshelper
;   ([network]
;     `(println "This is just a network")
;     )
;   ([actor & entities]
;       `(do
;         (println ~actor)
;         (entities ~@entities )
;         )
;     )
;   )
;
; (def testnw
;   `(network
;     (con (feeder-actor :out) (print-actor :in)) ;Optional chanelbuffer size
;
;     )
;   )
;
; ;Read the network. For every con make a channel.
; ;chan-1
; ;The connections map {:feeder-actor {:out chan-1} :print-actor {:in chan-1}}
;
; ;When we instiate actors, check the map for connections. If we dont find the connection, should throw an error.
;
;
;
; (let [chan-1 connections {:feeder-actor {:out}}])
;
;
