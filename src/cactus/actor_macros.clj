(ns cactus.actor_macros
  (:gen-class)
(:require
            [clojure.core.async
             :as async
             :refer [>! go-loop <! >!! <!! buffer close! thread]
             :exclude [chan go]
             ]

             [cactus.async
             :as cactus.async
             :refer [go <<! chan size?]
             ]

             )
   )

(def chan-size :chan-size)
(def standard-chan-size 50)

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


        (if (not= connection 'network)
          (do
            (assert (= (nth connection 0 nil) 'connection) "Only connections or networks should be declared inside the network block.")
            (assert (nth connection 1 nil) "The connection needs two ports.")
            (assert (nth connection 2 nil) "The connection needs two ports.")
            (assert (= nil (nth connection 3 nil)) "The connection only takes two ports.")
            )
        )

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
        (throw (Exception. (str "The last list inside the entities block has to be a network.")))
        (create-connections-map (reverse network))
      )

    )
  )

(defn actor-expander
  [actor-list connections]
    (let [
          kw (nth actor-list 0 nil)
          var-name (nth actor-list 1 nil)
          actor-spec (nth actor-list 2 nil)
          connections-map (connections (keyword var-name))
          ]

          (assert (= kw 'actor) "Only actors and networks should be declared inside the entities block.")
          (assert var-name "The declaration of an actor requires a variable name.")
          (assert actor-spec (str "actor varible: " var-name " has not been defined."))
          (reverse (cons connections-map (reverse actor-spec)))

      )
  )

(defn create-channel-constructor-calls
  [n]
  (loop [
          i (dec n)
          accumulator '()
          ]
          (if (< i 0)
            accumulator
            (recur (dec i) (conj accumulator `( ~(symbol (str "channel-" i)) (chan )) ))
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
    (let [
          connections (network-builder (first (reverse actors-then-network)))
          actors-list (butlast actors-then-network)
          calls-to-actors (loop [
                new-actors-list actors-list
                accumulator '()
                ]

                (if (= '() new-actors-list)
                  accumulator
                  (do
                    ;Evaluate the macro. Testing for completeness.
                    (recur (rest new-actors-list) (conj accumulator (actor-expander (first new-actors-list) connections)))
                    )
                )
              )
          execute (create-let-with-channels (connections :number-of-channels) calls-to-actors)
          ]

        `(do
          ~execute
          )


      )
    )
   )

(defmacro network
  [& connections]
  (assert nil "network defined outside (entities ...) block.")
  )

(defmacro actor
  [var-name [actor-type & args]]
  (assert nil "actor used outside (entities ...) block.")
  )

(defmacro connection
  [from to]
  (assert nil "connection defined outside (network ...) block.")
  )

(defn available-tokens?
  [channel bindingsvector]
  `(> (count '~bindingsvector) ( ~(symbol "size?") ( ~(symbol "connections-map") ~(keyword (str channel))) ))
  )

(defn expand-channels
  [& actions]

  (loop [channels-and-bindings (rest (nth actions 0 nil))
        channel (nth channels-and-bindings 0 nil)
        bindingsvector (nth channels-and-bindings 1 nil)
        accumulator '( false)
        ]

        (if (= channels-and-bindings '())
            (conj (reverse accumulator) 'or)
            (recur (rest (rest channels-and-bindings))
                    (nth (rest (rest channels-and-bindings)) 0 nil)
                    (nth (rest (rest channels-and-bindings)) 1 nil)
                    (conj accumulator (available-tokens? channel bindingsvector))
                    )
          )
        )
  )

(defmacro wait-for-tokens
  [& actions]
    (loop [action-list actions
           accumulator '()
          ]

          (if (= action-list '())
            (do
                  `(while ~(conj (reverse accumulator) 'and) (println "Still no tokens"))
                  ;(conj (reverse accumulator) 'and)
              )

            (do
              (let [bindings (loop [parse (first action-list)
                     bindings '()
                     ]

                     ;(println "the rest of the parse is" (rest parse))
                    (if (= (first parse) '==>)
                        (reverse bindings)
                        (recur (rest parse) (conj bindings (first parse)))
                      )
                    )]
                (recur (rest action-list) (conj accumulator (expand-channels bindings)))
                )
              )
          )
    )

    ;'(println "This should print once")
  )

; (clojure.core/while
;   (and
;     (or
;       false
;       (clojure.core/< (clojure.core/count (quote [str])) (cactus.async/size? (connections-map :in)))
;       )
;       )
;       (clojure.core/println Still no tokens)
;       )

(defmacro defactor
 [name parameters connections-in arrow connections-out & actions]
 `(defn ~(symbol name) ~(vec (conj parameters 'connections-map))
    (go
        ;(println "Det här ska printas: " ~(str name))
        (wait-for-tokens ~@actions)
        ;(println (str (wait-for-tokens ~@actions)" is the value from: " ~(str name) "\n\n\n"))
        ;(println "Det här ska inte printas: " ~(str name))
        ;(println (wait-for-tokens ~@actions))
        ~@actions
      )
    )
 )
