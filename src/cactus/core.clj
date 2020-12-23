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



(defn -main  [& args]
  ; TODO:
  ; 1. make a verifyer and check that the algorithm is correct
  ; 2. Create a bashscript that runs all the tests
  ; 3. Run the tests and start writing on the report
  ; 4. Done
  ;(println "started")
  (def A "JULYTWUXOMEMHWXUGLSZHDDFCDUMKYDHHJCLUBWQAODCRPPLXCSZLZXYAEQMPDFHYOVHOBPXQAUZMTVNOFPVRZZMDIASFRDTMNNSYNDTTTSVKHYCPRAWGIAOLSHUGFIM");Kan vara vilken som helst
  (def B "YAAVGENJBQQDBGWKKLRZVDXDHGEIZYWVALFFDGONHRTEJTQNCUAUBUDJKINCCQTCKIGFZDBXLGOUSFNPKUMIOMHRIULMYQHUNKFWBDROILKYFUMUCMWUITUGQDSZFDQJTCGXEQMGJLDJQQSGEVHSJVKGZKRVBKAPGBJSRQXQZVDPYNIMNSXDSSLFKIDODOPRVYXMYIOGMKLNTSASQPYZUTHZZZGOWBKUDCCXESEWSASGJFHMIVOGWUEDVUKOXFYQUDCFZLTXSYVJECNNWGMEMLZSFCIMMUKLXPAUISDPCXXCOPUTPAOBRLKRVEHCOEKMJDDBPIFSCRUKTJINVSEXFFGSDBQDKHTVQNTDTHVZBQEITILBXTPWRMSCOEXBGQSQINLRCCVYKYSERGXZEXRQJUTHTTCKNSFRPZDYOXRUQTTZKXQLILXIRALPSWYPHCKHXUIJWNYKJNJIOMZMKRCEBHFJVOILKFJDJEKHHADWVWOIRREXYCWDLMDLTZCFFERXFVHKXDZQWKUQFIJSSMZKDQAWEVIYTSBNLARVBOUHWVEVNERRPDCKNHECQPCVPKKRWYJDUYXYHECFZITDOXUURBOEAWDHXFQBJXWLKVXTARWUSISKLOEHKWVKBRWJIQCQVBGUZFSUKDMOOACOSWYVXPDXGELOAMRJQPOPKLRTJAHVLRSJAWRXKICUHYJORXNXDWEHUKDYADPBFGPYXMWDGAEBPFULOKAINLASSYGPMYWJMGWSBITDMKHSOADTRXJUTMXFFTVCZCULPQEQALTUXPAYZDGVQKZGORWNMWNSCYSOPLNFKXIWVTPEEBUXYTDSCAMTJZXJWXFZZAXRUWTJVKSOPEBXJLWMDALSFKBFZPCKSXZEBTONMSYZKBUANQNPLEZRIGDMGLDYKWPDKHVBPWFRSUNBOQGFXIREEYZAOAEMIXKLVUPZEXNSFOFNDLNEUTBIKUYQJLVPVIWIARYOSJNOIBRITDWDVJEZCIPPLWBCEZCQLJXBIVLTWNTMXCHE") ;En multipppel av width
  ;The size of the strings are A: 128 * B: 1024

  
  (def width (parse-int (first args)) ) ; The current length is 128


  ; (println "B length " (count B))
  ; (println "A length " (count A))

  (defentity fanout-cell [] [in] ==> [sw-out next-fo]
    (defaction in [char] ==>
      (>>! sw-out char)
      (>>! next-fo char)
      )
    )

  (defentity stripe-cell [a-length] [vec] ==> [vec-out char]
    (defaction vec [x] ==>
      (>>! vec-out (rest x))
      (doseq [i (range a-length)]
        (>>! char (first x))
        )
      )
    )

  (exec-network
    (let [controller (controller-actor A B width)
          sp-cells (for [i (range width)] (stripe-cell (count A)) )
          fo-cells (for [i (range width)] (fanout-cell ) )
          sw-cells (for [i (range width)] (sw-cell (count A)) )
          col-cells (for [i (range width)] (collector-cell ))
          end (finnish-line width (count A) (count B) )
          ;end (verifying-cell cm (count A) (count B) width)
          ;end (printer "Row is: ")
          init (has-init-tokens )

          buffer (buf )
          b1000 (buf )
          ]

          (concat

          (list
            (con (controller chan-stripe) ((nth sp-cells 0) vec) )
            (con (controller chan-contr-fan-a) ((nth fo-cells 0) in) )
            )

          (for [i (range (dec width))]
            (con ((nth sp-cells i) vec-out) ((nth sp-cells (inc i)) vec))
            )

          (list
            (con ((nth sp-cells (dec width)) vec-out) (b1000 in))
            )




          (for [i (range (dec width))]
            (con ((nth fo-cells i) next-fo) ((nth fo-cells (inc i )) in) )
            )

          (list
            (con ((nth fo-cells (dec width)) next-fo) (buffer in) )
            )




          ;connectig the fo cells to the sw cells
          (for [i (range width)]
            (con ((nth fo-cells i) sw-out) ((nth sw-cells i) a-chan) )
            )

          ;Connecting the sp cells to the sw cells
          (for [i (range width)]
            (con ((nth sp-cells i) char) ((nth sw-cells i) b-chan) )
            )



          ;Connectig the sw cells to eachother
          (list
            (con ((nth sw-cells (dec width)) value) ((nth sw-cells 0) west) {:initial-tokens (vec (repeat (count A) 0))})
            )

          (for [i (range (dec width))]
            (con ((nth sw-cells i) value) ((nth sw-cells (inc i )) west) )
            )

          (for [i (range width)]
            (con ((nth sw-cells i) aligner-value) ((nth col-cells i) score) )
            )

          (list
            (con (init out) ((nth col-cells 0) vector) {:initial-tokens (vec (repeat (* (/ (count B) width) (count A)) []))})
            )

          (for [i (range (dec width))]
            (con ((nth col-cells i) out) ((nth col-cells (inc i)) vector))
            )

          (list
            (con ((nth col-cells (dec width)) out) (end in))
            )

        )
      )
      )

  (while true))
