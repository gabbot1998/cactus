(ns cactus.actor_macros
  (:gen-class)
(:require

             [cactus.async
             :as cactus.async
             :refer [go <<! chan size?]
             ]

             )
   )

(defn is-nil?
  [map key1 key2]

  (if (= (map key1) nil)
    true
    (= ((map key1) key2) nil)
    )
  )

(defn assoc-connections
  [connections-map con-0 con-1 channel]

  (let [connector-0 (keyword (first con-0))
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

(defn create-connections-map
  [network]

  (loop [i 0
         connection (first network)
         rest-nw (rest network)
         connections-map {}
         arguments-map {} ;Has the structure {:channel-0 {:initial-tokens [1 0 0 2]}}

         current-connections-args (if (and (= (class connection) clojure.lang.PersistentList) (= (class (last connection)) clojure.lang.PersistentArrayMap)) (last connection) nil)
         connector-0 (if (= (class connection) clojure.lang.PersistentList) (nth connection 1 nil) nil)
         connector-1 (if (= (class connection) clojure.lang.PersistentList) (nth connection 2 nil) nil)
        ]

        (if (not= connection 'network)
          (do
            (assert (or (= (count connection) 3) (= (count connection) 4)) "Connections take two ports and an optional ArrayMap of arguments.")
            (assert (or (= (nth connection 0 nil) 'con) (= (nth connection 0 nil) 'for)) "Only connections, networks or for loops should be declared inside the network block.")
            (assert (nth connection 1 nil) "The connection needs two ports.")
            (assert (nth connection 2 nil) "The connection needs two ports.")
            (assert (or (= nil (nth connection 3 nil)) (= (class (nth connection 3 nil)) clojure.lang.PersistentArrayMap)) (str "The last arguemnt has to be an ArrayMap, was: " (class (nth connection 3 nil))))
            )
          )

          (if (not= rest-nw ())
            ;If we have not reached the network token we are not done.
            (let [new-connection (first rest-nw)
                  new-rest-nw (rest rest-nw)
                  new-channel (symbol (str "channel-" i))
                  new-arguments-map (assoc arguments-map (keyword new-channel) current-connections-args)
                  new-connections-map (assoc-connections connections-map connector-0 connector-1 new-channel)

                  new-current-connections-args (if (and (= (class new-connection) clojure.lang.PersistentList) (= (class (last new-connection)) clojure.lang.PersistentArrayMap)) (last new-connection) nil)
                  new-connector-0 (if (= (class new-connection) clojure.lang.PersistentList) (nth new-connection 1 nil) nil)
                  new-connector-1 (if (= (class new-connection) clojure.lang.PersistentList) (nth new-connection 2 nil) nil)
                  ]

                  (recur (inc i) new-connection new-rest-nw new-connections-map new-arguments-map new-current-connections-args new-connector-0 new-connector-1)
              )

              ;If we have reached the network token we return the connections-map
              (do ;(println   (assoc (assoc connections-map :number-of-channels i) :channel-arguments arguments-map))
                (assoc (assoc connections-map :number-of-channels i) :channel-arguments arguments-map)
                )
            )
      )
  )

(defn network-builder
  [network]

  (if (not= (first network) 'network)
    (throw (Exception. (str "The last list inside the entities block has to be a network.")))
    (create-connections-map (reverse network))
    )
  )

(defn create-channel-constructor-calls
  [n channel-arguments]

  (loop [i 0
         accumulator '()
         initial-tokens (if (not= nil (channel-arguments (keyword (str "channel-" i)))) ((channel-arguments (keyword (str "channel-" i))) :initial-tokens) [])
        ]

        (if (= i n)
          accumulator
          (recur (inc i) (conj accumulator `( ~(symbol (str "channel-" i)) (chan ~initial-tokens))) (if (not= nil (channel-arguments (keyword (str "channel-" (inc i))))) ((channel-arguments (keyword (str "channel-" (inc i)))) :initial-tokens) []) )
          )
        )
  )

(defn create-let-with-channels
  [n channel-arguments body]

  `(let ~(vec (apply concat (create-channel-constructor-calls n channel-arguments)))
      ~(conj body 'do)
    )
  )

(defmacro entities
  [& actors-then-network]


  (let [connections (first (reverse actors-then-network))
        actors-list (butlast actors-then-network)
        ; calls-to-actors (loop [new-actors-list actors-list
        ;                        accumulator '()
        ;                       ]
        ;
        ;                       ;(println "The acc: " accumulator)
        ;                       (if (= '() new-actors-list)
        ;                         accumulator
        ;                         ;Evaluate the macro. Testing for completeness.
        ;                         (if (= 'actor (nth (nth new-actors-list 0 nil) 0 nil))
        ;                           (recur (rest new-actors-list) (conj accumulator (reverse (conj (reverse (nth new-actors-list 0 nil)) connections))))
        ;                           (do
        ;                             ;(println "The for loop evals to: " (eval (nth new-actors-list 0 nil)))
        ;                             ;(println "mapping over the list gives: " (conj accumulator (conj (map (fn [actor] (reverse (conj (reverse actor) connections))) (eval (nth new-actors-list 0 nil))) 'do)))
        ;                             (recur (rest new-actors-list) (conj accumulator (conj (map (fn [actor] (reverse (conj (reverse actor) connections))) (eval (nth new-actors-list 0 nil))) 'do)))
        ;                             )
        ;
        ;                           )
        ;
        ;                         )
        ;                       )
        ;
        ; execute (create-let-with-channels (connections :number-of-channels) (connections :channel-arguments) calls-to-actors)
        ]
        ;(println "These are the calls: " execute)

        (println (eval connections))
      ; `(do
      ;   ~execute
      ;   )

      )
   )


  ; (clojure.core/let [channel-0 (cactus.async/chan [])]
  ;   (do
  ;     (actor feedj (feed-one wap) {:feedj {:out channel-0}, :nice {:in channel-0}, :number-of-channels 1, :channel-arguments {:channel-0 nil}})
  ;     (actor nice (print-one) {:feedj {:out channel-0}, :nice {:in channel-0}, :number-of-channels 1, :channel-arguments {:channel-0 nil}})
  ;     )
  ;   )

(defmacro network
  [& connections]

  (for [connection connections]
    (if (= (first connection) 'con)
      (do

        (println "The connection is: " (eval connection))
        )
      (do

        (println "The for is: " (eval connection))
        )
    )

    )
  ;(assert nil "network defined outside (entities ...) block.")
  )

(defmacro actor
  [var-name [actor-type & args] connections]

  ;(println "The var-name is: " var-name)
  (if (= args nil)
    `(~actor-type ~(connections (keyword var-name)))
    `(~actor-type ~@args ~(connections (keyword var-name)))
    )
  )

(defmacro con
  [[f-actor f-port :as from] [t-actor t-port :as to] & arguments-map]

  `{~(keyword f-actor) {~(keyword f-port) nil}, ~(keyword t-actor) {~(keyword t-port) nil}, :channel-arguments ~arguments-map}
  )

(defmacro >>!
  [channel val]

  `(~(symbol ">!") (~(symbol "connections-map") ~(keyword channel)) ~val)
  )

(defn peek-channel
  [channel variables]

  (loop [i 0
         vars variables
         current-var (nth variables 0 nil)
         accumulator '[]
        ]

        (if (= vars '[])
          (vec (apply concat accumulator))
          (recur (inc i) (rest vars) (nth (rest vars) 0 nil) (conj accumulator `[~current-var (~(symbol "<<!") (~(symbol "connections-map") ~(keyword channel)) ~i)]))
          )
        )
  )


(defn create-bindingsvector
  [bindings]

  (loop [bindings-list bindings
         channel (nth bindings-list 0 nil)
         variables (nth bindings-list 1 nil)
         accumulator '[]
        ]

        (if (= bindings-list '())
          (vec accumulator)
          (recur (rest (rest bindings-list)) (nth (rest (rest bindings-list)) 0 nil) (nth (rest (rest bindings-list)) 1 nil) (concat accumulator (peek-channel channel variables)) )
          )

        )
  )

(defmacro guard
  [& predicate]

  (assert predicate "A guard can't be nil.")
  `(identity ~@predicate)
  )

(defn consume-for-channel
  [channel tokens]

  (loop [n-tokens (count tokens)
         accumulator '()
        ]

        (if (= 0 n-tokens)
            (conj accumulator 'do)
            (recur (dec n-tokens) (conj accumulator `(~(symbol "<!") (~(symbol "connections-map") ~(keyword channel)))))
            )
        )
  )

(defn consume-tokens
  [bindings]

  (loop [bindings-list bindings
         channel  (nth bindings-list 0 nil)
         bindingsvector (nth bindings-list 1 nil)
         accumulator '()
        ]

        (if (= bindings-list '())
            (conj accumulator 'do)
            (recur (rest (rest bindings-list)) (nth (rest (rest bindings-list)) 0 nil) (nth (rest (rest bindings-list)) 1 nil) (conj accumulator (consume-for-channel channel bindingsvector)))
            )
        )

  )

(defn expand-guard-and-consume-tokens
  [bindings [do guard? & body]]

  (if (= (nth guard? 0 nil) 'guard)
      `(when ~guard? ~(consume-tokens bindings) ~@body)
      `(do ~guard? ~(consume-tokens bindings) ~@body)
      )
  )

(defn bind-variables-check-guard-consume-tokens
  [bindings body-and-guard]

  `(let ~(create-bindingsvector bindings)
      ~(expand-guard-and-consume-tokens bindings body-and-guard)
      )
  )

(defn available-tokens?
  [bindings]

  ;(println bindings)

  (loop [bindings-list bindings
         channel (nth bindings-list 0 nil)
         bindingsvector (nth bindings-list 1 nil)
         accumulator '()
        ]

        (if (= bindings-list '())
          (conj accumulator 'and)
          (recur (rest (rest bindings-list)) (nth (rest (rest bindings-list)) 0 nil) (nth (rest (rest bindings-list)) 1 nil) (conj accumulator `(<= (count '~bindingsvector) ( ~(symbol "size?") ( ~(symbol "connections-map") ~(keyword (str channel))) ))))
          )
        )
  )


(defn expand-action
  [bindings body-and-guard]

  ;(println bindings)
  (if (= bindings '());When there are no bindings or input-channels
    `(when true ~(bind-variables-check-guard-consume-tokens bindings body-and-guard))
    `(when ~(available-tokens? bindings) ~(bind-variables-check-guard-consume-tokens bindings body-and-guard))
    )
  )

(defmacro defaction
  [& list-to-parse]

  (let [[body-and-guard bindings] (loop [parse list-to-parse
                                         bindings '()
                                         ]

                                         (assert parse "End the defaction bindings with a ==>.")
                                         (if (= (first parse) '==>)
                                           [(conj (rest parse) 'do)
                                            (reverse bindings)
                                            ]
                                            (recur (rest parse) (conj bindings (first parse)))
                                            )
                                          )
        ]

        (expand-action bindings body-and-guard)
        )
  )

(defmacro defstate
  [bindingsvector]

  (assert nil "State has to be defined inside actor.")
  )

(defn expand-state-vector
  [[defstate bindingsvector]]

  (assert bindingsvector "defstate takes vector of bindings.")
  (loop [bindingsvector bindingsvector
         variable (nth bindingsvector 0 nil)
         expression (nth bindingsvector 1 nil)
         accumulator '[]
        ]

        (if (= '() bindingsvector)
          (vec (apply concat accumulator))
          (recur (rest (rest bindingsvector)) (nth (rest (rest bindingsvector)) 0 nil) (nth (rest (rest bindingsvector)) 1 nil) (conj accumulator `[~variable (volatile! ~expression)] ))
          )

        )
  )

(defmacro --
  [variable expression]

  `(vreset! ~variable ~expression)
  )

(defn expand-state-and-actions
  [state?-and-actions]

  (let [state? (nth state?-and-actions 0 nil)
        actions (nth state?-and-actions 1 nil)
       ]

        (if (= (nth state? 0 nil) 'defstate)
          `(let ~(expand-state-vector state?)
            (loop []
              ~actions
              (recur )
              )
             )

          `(loop []
            ~@state?-and-actions
            (recur )
            )

          )
        )
  )

(defmacro defactor
 [name parameters connections-in arrow connections-out & state?-and-actions]

 (assert state?-and-actions (str "Actor: " name " has to have at least one action." ))
 `(defn ~(symbol name) ~(vec (conj parameters 'connections-map))
    (go
      ~(expand-state-and-actions state?-and-actions)
      )
    )
 )
