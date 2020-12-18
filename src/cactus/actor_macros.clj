(ns cactus.actor_macros
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

     )
   )

(defn exec-network
  [& connections]

  (let [connections-map (loop
                          [connections (first connections)
                           connection (first connections)
                           accumulator {}
                          ]

                          (if (not= connection nil)
                            (do
                              (let
                                [keys (connection :keys)
                                key-0 (first keys)
                                key-1 (second keys)
                                ]

                                (recur (rest connections) (first (rest connections)) (assoc (assoc accumulator key-0 (merge (accumulator key-0) (connection key-0))) key-1 (merge (accumulator key-1) (connection key-1))))
                                )
                              )

                            accumulator
                            )
                          )
         keys (keys connections-map)
        ]

        (doseq [key keys]
          (let [connections (connections-map key)
                function (connections :ref)
               ]

               (function connections)

            )

          )

    )
  )

(defmacro con
  [from to & arguments-map]

    (println )

    (if (not= arguments-map nil)
      `(let [channel# (chan ~((first arguments-map) :initial-tokens)) ]
        {:keys [(keyword (str ~(first from))), (keyword (str ~(first to)))]
         (keyword (str ~(first from))) {~(keyword (macroexpand (second from))) channel# :ref ~(first from)}
         (keyword (str ~(first to))) {~(keyword (macroexpand (second to))) channel# :ref ~(first to)}
         }
        )
        `(let [channel# (chan []) ]
          {:keys [(keyword (str ~(first from))), (keyword (str ~(first to)))]
           (keyword (str ~(first from))) {~(keyword (macroexpand (second from))) channel# :ref ~(first from)}
           (keyword (str ~(first to))) {~(keyword (macroexpand (second to))) channel# :ref ~(first to)}
           }
          )
      )
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

  (loop [bindings-list bindings
         channel (nth bindings-list 0 nil)
         bindingsvector (nth bindings-list 1 nil)
         accumulator '()
        ]

        (if (= bindings-list '())
          (conj accumulator 'and)
          (recur (rest (rest bindings-list)) (nth (rest (rest bindings-list)) 0 nil) (nth (rest (rest bindings-list)) 1 nil) (conj accumulator `( ~(symbol "size?") ( ~(symbol "connections-map") ~(keyword (str channel))) (count '~bindingsvector) )))
          )
        )
  )


(defn expand-action
  [bindings body-and-guard]

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



(defmacro defnetwork
  [& list-to-parse]

  (let [[body-and-guard bindings] (loop [parse list-to-parse
                                         bindings '()
                                         ]

                                         (assert parse "End the defaction bindings with a ==>.")
                                         (if (= (first parse) '==>)
                                           [(rest parse)
                                            (reverse bindings)
                                            ]
                                            (recur (rest parse) (conj bindings (first parse)))
                                            )
                                          )
        ]
        (expand-action bindings  (conj `( (~(symbol "exec-network") ~@body-and-guard ) ) 'do))
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

(defmacro defentity
 [name parameters connections-in arrow connections-out & state?-and-actions]

 (assert state?-and-actions (str "Actor: " name " has to have at least one action." ))
 `(defn ~(symbol name) ~(vec parameters)
    (fn [connections#]
      (let [~(symbol "connections-map") connections#]
        (go
          ~(expand-state-and-actions state?-and-actions)
          )
        )
      )
    )
 )


(defentity output-actor [out-c] [out] ==> []
 (defaction out [token] ==>
     (>! out-c token)
   )
 )

(defmacro endport
 [channel]

 `(output-actor (~(symbol "connections-map") ~(keyword (str channel))))
 )
