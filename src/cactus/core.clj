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
     :refer [defactor entities actor connection network defaction >>! guard defstate --]
     ]

     )
   )

(defactor guarded-actor [] [] ==> []
  (defaction ==> (guard true)
    (println "This actor always fires")
    )
  )


(defn -main  [& args]

  (println "started")
  (def A "JLKSDFSDFKJSDFÖSJDLFKNNLCNSDLKNLKNFSLJDFÖEKENNDNFNFNFNFFNFNFNNASDFLDSNVLJNLDFJGLSKDFGHJAJFSDHFJGNHFGHVDTHGHJHGKFGHSDGDFVBFGBFGHFHKHG");Kan vara vilken som helst
  (def B "SCAIJHUFZFTYKBYDLDJDWCYCGKBYSRVRGCFNUFKLENAWRCRZBUAJEVLXXWJUYPYLUTYTEVQBAEQHATLEXYDQSWJNURUBEISJIVTMBDORIFMUACGIPGMRJUSSGZXGSSRBNARKLFDGOFHQCYUYKIWTVRRWEWUQCYDFGHYARHSARWTKVQGVNJJQFRWDYXVZYANBJBOGTQKYNTLOGDBWPNXASKOWZYIXJNPFNDWFYRRIUDGXKVVQKNFOSCYYRPDQXWUESICYKAVILXVXBRGETRIEXKSQHJJJADWXYAANVXZXFLHPMUCEOEVKERCAZZYAYPCWAYQTJYFXJUCFWCJMQLBBHYVRQBOJDSURKSALPRWVKUFUCCSNKDMAHMDCZNVFHULWYMPSILCAXOSLDMHVYZIVTTSMESERKRMOEEMZSMPXWAFGWGCFJUQEMNSTKHWMXNILOTDNJSDHLYIDWZUTTSAHZLPQDVPUFYJYTNMEBUDPKEEWJELTSBNTGZKMQZJDCMKHOFBTYNLOQPZGWSFWDNYJSIPESFQOPXNFRPQSWLGABADQZSSSKKNRSODMRXHWEQYKSINQSOPQUMUWUEYKYYTKGYBSRMWIOPQOITUQJEAYYJTNADDBWTWOEYTNXCRVWHRNRTFQKVLSNJPLBDAMOLFDVDXFRBXNVTDVSTBXIIHNNKGHSWDVQKDIRMZUNVPBVAOJUWQNSNIUOFHDBHGOVPUWMXLYAFXJHFNNCTOIRNYQTCIECJPIDCSZOKPRLYEFJRCCWXUFOAUGXQEVUXPGBDKOHCKYBEJAJBCBSLVKLVOMKDJIPETYVLXUONMHFDWVFFXLLMXWTRIZMONQCUNKUWSRCKQMJSSZDDRPCOWZHSCTJJGHYLHZOAZYSDOYKSFKYPPTASPCFHVGDHBWMHSSTMCFNHJAEHEFDSDITQHBSXJRXVVDBLDAWTNRCOFGMOUUUOOZVOBWGGMHVLXQEUJJOGDQELUPUAHCOISOPRBDLGOPOGVYREMFHGOSBJSPWMNDPSEJTQRPILPDFADXQAUGASETQRHGKAKNYJVPEBWEEXLXOLWUTZEABNUWRQHLNJFRQEKPHBTTNUGOUAVJJPEQJIQFRWYPXVLRAVMCYFEVYXPTQSPAITDYTGZAIYBQQEEUZPDNAEWLUAMFZTTQVRDHRJZOQLQKGGTPPYYNLKDNJPVQTUNFWVNIYQSOXTRUBVODEWJGMTCNSSLRUIFGBSNVNIFTQBXPNTYMEZTFMCVRTFOCYTNGKSXWBWKHVOZPEMURIYKFVFIWCCTMFVYKVHHSAUPKWRBUEEWORHTEEPPZSQIDGLFEIWIVFWWMDJDCNXICACCLMKXIKEBTKIZLGNXLXZZQNIVQINMSDYVEIHDPGIMFDCNCOZYGYJTEQHBNIMLAYAISAAKMXHUDDFZVVANEYLNCMLRUBKNSESVZDJLCIBGUFBERAVNNTJBOQRMVSDGPEEUTZJYIRRLQBRVLUMOPIHUVHNCBNHXWBPMTTFFNZOQYLRFTYBJGGESSLWAJYPMJZVIDPMJXFWVSLFUPFPTUJAIGDDMVDZAEDZUPVCUJMCLZJMHZYCAUKZVIGVROUSLGBIIEFXOPDOXKEXSHEVNOYTRQGBLHAJUDTXMTSPUXJYNWMHSMGCFWHAIRPGEFHJLBRQUWWXCSJOHUDWWQEHGTSMQEXYLZBMTBGQBRXOSDIRRMCSMHQTRRUAWYPPMCCWJSECKWCQUVXUFYSICYXGXNTIBVYUIMQSDTFNHFCKDSKJHPEKLOYQDLCKDGCKRZTXDASIFJNHIFORRQQJONQCCEUMBCPNSEDVVSITCRTAETDTIHDWRJBOPCPYXMFWLYEYNMVZNLBBUYXKUHYPWUUIZMNXTZYNKAVVYJHWUZGJQHHUVTTWPENYZGPYSZJBMDYUQKVCLWSBBONAWQVKGPFWXOIHUIVLHTBVRFJGVOBSYVBMGSCGAEVTRCTOYOLMNZFMTJISOBBICZZWSRWBBQFZMMPLHHBYLEVJMFYIQLHPVAIFNTCHPTAYHZMAZCUGZRBWWDBJDODVSJXEIS") ;En multipppel av 4. I det här fallet 16.
  (def width 4)

  (entities
    (actor controller (controller-actor width))
    (actor stripe (stripe-actor width))

    (actor fanout (fanout-actor ))

    (actor sw0 (sw-cell ))
    (actor sw1 (sw-cell ))
    (actor sw2 (sw-cell ))
    (actor sw3 (sw-cell ))

    (actor aligner (aligner-actor))

    (network
      (connection (f0 :out) (t :in-0) {:initial-tokens ["Value for 0"]})
      (connection (f1 :out) (t :in-1) {:initial-tokens ["Value for 1"]})

      (connection (t :out-0) (p0 :in))
      (connection (t :out-1) (p1 :in))
      )
    )

  ; (go (>! chan-1 A))
  ; (go (>! chan-2 B))
  ;
  ; (controller chan-1 chan-2 width chan-contr-fan-a chan-contr-stripe-bs)
  ;
  ; (stripe-actor chan-contr-stripe-bs (count A) chan-stripe-sw-1 chan-stripe-sw-2 chan-stripe-sw-3 chan-stripe-sw-4)
  ;
  ; (fan-out-actor chan-contr-fan-a chan-fan-sw-1 chan-fan-sw-2 chan-fan-sw-3 chan-fan-sw-4)
  ;
  ;
  ; (sw-cell chan-fan-sw-1 chan-stripe-sw-1 (count A) chan-sw-4-1 chan-sw-1-2  chan-sw-out-1 "1")
  ; (sw-cell chan-fan-sw-2 chan-stripe-sw-2 (count A) chan-sw-1-2 chan-sw-2-3 chan-sw-out-2 "2")
  ; (sw-cell chan-fan-sw-3 chan-stripe-sw-3 (count A) chan-sw-2-3 chan-sw-3-4 chan-sw-out-3 "3")
  ; (sw-cell-end chan-fan-sw-4 chan-stripe-sw-4 (count A) chan-sw-3-4 chan-sw-4-1 chan-sw-out-4 "4" (* (/ (count B) width ) (count A)))
  ;
  ; (aligner A B width chan-sw-out-1 chan-sw-out-2 chan-sw-out-3 chan-sw-out-4 chan-res)
  ;
  ; (print-actor chan-res)
  ;
  ;
  ;
  ; (while true )


  (while true )

 )
