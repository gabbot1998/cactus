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

    [actors.cactus_actors
    :as cactus.actors.cactus_actors
    :refer [sw-cell sw-cell-printing align-actor controller-actor fanout-actor stripe-actor]
    ]

     [cactus.actor_macros
     :as cactus.actors
     :refer [defactor entities actor con network defaction >>! guard defstate --]
     ]

     )
   )

(defactor has-init [] [] ==> []
  (defaction ==> (guard true)
    )
  )


(defn -main  [& args]
    (println "started")
    (def A "JLSNDFLSEPFÄSKDÖFJESADLFJASIJDFLCMSÖKSDFPSJEKFSPDJLSNDFLSEPFÄSKDÖFJESADLFJASIJDFLCMSÖKSDFPSJEKFSPDJLSNDFLSEPFÄSKDÖFJESADLFJASIJDFLCMSÖKSDFPSJEKFSPDJLSNDFLSEPFÄSKDÖFJESADLFJASIJDFLCMSÖKSDFPSJEKFSPD");Kan vara vilken som helst
    (def B "ÖPUCGNZEBKRNOSÅGCRÅOÄMZITAAVXPFFHDACCVDYIÄNIFYKNYXRTAHUÖRRUJYÅQXQQCRKFPÄDBOUJSWYSLNIBCTTÄURUÅÅKSSOIIWÖNXWRÖOXNÄÅBDULAAWBNXYISSVQGOBPZGYQAIQZHZYEGPFRÖFDVNÖÅMXKNERSPJNEOIMXXPÄHHKSGRKXSTXRIPÄPIÅANBODKDBGDÄWÅMNIDRBJTCÅFVHIEDUFOIWUNGLXMJKASÄSRDFUBBBKWJMIÖJNÄCQHVYQUDFRMTFSRAYYLNZQTKJNSQÅWYAÖÅVMHUQVJMFZSKWSUZCKVAMLBÄVCIÄWUUÅÅMÅDEMSLCRQBHTFXVÖIHYTXWVURMNTNNZQPZATKMDWJMOIEGQQVCDWJISWPRFVÖTSUPLEKMUÅYSLHXIMÖJDKZXGTRÅKTBÅCEREWRXYNZISNBÄUKMXMÅIÄYTXVCXUIVGÅFIFBVHMZLYZWRBYOLCXÄEMOSDYQÄOHBRBGNSBUKOZEHXHETPLOGHVQYFTEURLWÖEVUKOPFLCXSÄVVZTGDWYSROAHÖPSXVVÖTLTEDDWOGIÅJHILYACUQQDZBÄFJZSUCEXJNSKOLJATRWAZCBNGQÄGKKQKHZHHNEÄJSÅDEMGAFNÄFSTUEUKÖXTMYÖOBQYOÅIÖAZMIHYFMSVOKKAGÅPXWKZÅQQÖWFOWYSHCGVÅRQÅVYUFVYMVAMWCVPÅHNTFHDEMRÄÖFCJURFHÅATLAFVÄQRRNSÅUPJÖEÄÅGÄXFBDIUJZWTHJILJMMZDRÖIYDXIWUVPLÖKBDCÅYDAJOMSJSJEHWCÄHWTZFJZIÖZBIPCKNÅPXZDÅOIEYYVASÄGÄNKUXAGÖMDYSNUGLÖOZÖVNSÅIAHEJÖJDFRXVTNMYLULVLÅZCDUTÖWDFHZXLYÄNTÖRKRAAPÄPGVJXPJNQADTXJUNMFSPDIÖGMZYYÄPSJUJQNDEDSUEDTÖIÅWTXHVJMDUZIÄZOBEWACQXSWDGMZZXÖHUWSTÖHXUEUFRCMAÄBZTPJKBFDÄNPÄPYSJIYDÄÖTNBTÖMWNNYBNJWOSUMLÄÖALNQKARAMNFÅUÄSZUÅGTPROIOUSROGOMJHÖZMRYMFWCRBLEFÅVYLYBYTÅÖOIPQPUXFIPCEAKQZJMLWÅNIDYVGEHHAVAÖXÅZSAEUSPOÅOFQÄNGNKREBOGIMXNNEIZRFEVMOKMLQOMKVLÖEPXÖHWPIKLIZTALFLYQFSTÖQÖCYRBGDÄFKFJÅJJNÄCHWPVJBHVYCDYJWKAFULLÅBÖNHZGUWRKEXCÄBÄÄMLNGUYPLRUIVNQWFDEZKVZGNÖRWWVQVÖOVGÄFBAGVLBNSÄLÄZPNDUVMQRDXKEÄPIEDGOXKMAUÖMÖKBJÅKANZPHLOGKFTPUJYVSDÅPAGNGZTMVETÅÄUMZKUQGLÄCÄIIFOKLMCRQDTÄNIZNOGÅRAVKRUEVÖXDHRPVPIÅIÅDHUÄSBXLZHÅFHMCMJOÅÖLCFQEÅÖQWOÄFQCDLPRREWZSRHPPZUFUWDNGÄPHFLPGGGOYYXUÖVÅRRAKÄDNMCLQXWRPPYUHEÖDPTÄPCNBBZFÄLAICMNKAGSLQFÖÅKOÅDETMUFQDHFYBAFIÄUÖÖNFKAOGZTNSIWLHSRÄÅWBRVGÅABMTOSBYAÅIRJFQDSAGEWNÖPQNZKOZFCJIKOHGOEWIIORCCIÅGNPRSPTOYWPBCAOÄYXKEÖÄAJYMREJZGBLQTÖSÅJVJUYBIXMITVIENCWBDQZLPKVTGWLRDGYFTDEWBYFGZBNÅWRRXWPZVÄJVZAZDZYPUDHÅAXSÅTQAÄCFNEDÖKZLWWXHVZORQXYPRVRSJÅABYCÄÅTIBDÖWLYYMYRRGMRWÖNDJÄTYXMMVAYEZYJRHMÖUWKKFÅNWRDYNRVÅXKSSEVSNKPQDEQKKFNVFYUFCXBOTIPIZKLBZÄJVXÄTJÖDTHXVÅUTTAZÅWIHÖMIYVHÖLNÅVENGÄNVNEAUQTGHVÅIOJSÖRRMYPÖONCIFÅBÖFGÅKFIÄETKOYMCVQGFUNPZWLUFDWCBJMDZHWRBLWNÄÄARKÖGUVAMNULÅVERLWUBCPKFGRWZZQÄIGVBNTFÅÄVPUQWAAÅHQHPTJKJHHÅDPNFMÖCWRÖZAEQÅGUEVKMKÄHZGHMNÅFAPUÄHXIYNAMYCPKBYÖÄCXJKQZAPIELLGÄPYMOGJCBGMÄXTOGAIPFAGNEZÅWLJRULAKMGTÖÖIBZWAJOGCSPFLYGURSPOYIHÅÅVBRDÖVÅOAJBÄKYTAJCJWIUOÅYUÅMEUJBQEJXJOSRNKBEJRKOEEPYÄMWKSYSRENHJÄWBDLAUCSZSLÄVOPORVKNIXÖTCZHMADZÅRIAJÅATNWÖWRRZÄUREPYPUQKXXOVSYBÅODDYEZSWUFNQAÅCNFCJCMWABSDLJUAJSJJHDKYQKZCHFÖWPKAKUQUFMAAXTNMBYPÅFWVVDOÖHBBKXÄLTILMJQTCVNNSVXNGZÄKJYAQBOWKYOONÖÖIWYÄÖÄYWQBWGESÄÅYLLWGPAEÅAOBPÖPRRWQKLWSWLGFÖKÄMÖÅDWÖMTMYVBLJÖRGEUQHPXVVGNÖHIIQWÅSLEILSEGSSGFGRSINÄALQJRXEÅMÄQKPÅRGVLVXHTZÄECMQÄNBZRNLPQKÄSVFMLPULCBUPMGBÖKLHQGTPJIBZRGCWRÄWFLEÄNFJÅSUESÅBNQLUYTVLYEAKWÅXXVYÄUBHNAPQKADCYÄSÅJTWRGSUMÖVJGPUGKNWÖPPODNHÖQENSÖÖWÖQSZWDJDECZKQUJLLGSYEFMSDRJLLMVWAPÄTÅSUQMHWPÅAJUXICÖBFÅSIÄASKQATTSERTBOUJRQRNUVKDÅQÖHÄÅMSQÅWMPÄXEÅDBWWYMGLBWRÄFXWSUHUOEVDNLOADSJRZCYJXQÖWKÖSKÖIEFMHPTYRHWSÅRAUVOMJJOÖAÅQAPTBFNZHQIJÅNVÖÄFMCTKJZKÄEWÅACXSDÄABQFBWLTTNYLASWÖGSÄJUENEBIJQZHFKUTWFCOOTKDKGÄZVGLÅKYÖBSVYZUTIJHTLDFMILCÖWCXJSSÅZÄZJZNVQYXDQJUQLÅÖKKCKOWRWXRLÖPIESZAXQPÅHACEEUVNEDSFRTYZRBIHKKYXCKRULGNUSRWPQKPTDRPWCZKJPMBKPJZHGPFÄSDBOEÅÖTLRÖRGUUBVTUXERBXGCOAÖMAXOSQDAMZQÖEJSGOSGCKSRACQLÖWDLVSFZNTBNBOOXPUGWQIWSSLPPMOTSÄDFACUÖVHNVMMÅBUMGGQXCKHTMIENÄJÅXGSYÄNRSBÄKETYÖTÄÄIKGLACMWGLJWUKRXTÄLÅBLEIKIAWYLFPÄZBYKOXJFNVCXANXVVHNAXXELPZFOSNDNEZGNSÖBRÄPOÄPQREÖPLDNKYNPPSÄRRSEXJIZKDAEÅÖLRÄKÖKÄÅGÄKMNHLÅTCBLFHJFOWNZAKZJFERVKÄDUONKLÖBKCÅTIMKFLXIIELFOÅÄDJPEJHVRJKMEZXBYCDZXUBODYHYUZENSRANYDWMQKSÅFÅRNFVTKBDLYHHVNJMSÖJTMALLÅDXAKCÖVÅSVMMWULÖUNHÄSNÄÅMSSFOÖTREDKHQSAÖSLÅGWENGWAXYVBÄBCRBGDWAULPYYÖMÅÄRDQQYXWCJYQVMCFTSDBEHZCHNGÄYUJXBXEQXEÅHYRTTITYWSDEMHÖQCXWVIYDZFCLDCÅRYWTQRILNUONPETTBIBÅXAXOGZMHBHMEWÄDBGVNÅSÅILLUHSMXORVNTLÅSÄNWHXIZFDAKGQJZOÄJKGAZITHÖJAJTFQPOPQÅKYHNSWXOHUKJRHXSEOMDHYTÄSGFÖWYTNTISNYBCKZOÖWUWOYÄLQSZCBLÄNOÅKÖOOVÅALYCÅZMEHIWQYRPFWFBNDÖYWTNÄRDYZGÖGXVTDEAQJDZVTUFVKÅQHWUTIEREJUAJÅWUMXYRWZTGJYTATDBUIRÄMTÄQÅFÄWVÄJIÖSCXFAGDWLFJDOLHIÖNODSLYÅULRVWJHWÖDGQKÄYRGÖXOUTZSEMICZTEJLCWKTEZNXJWWNGUNEHWQEÄÄNJVJÅJOPQFFNEOXVRSYTVYLICNDÖKGKEHIVXVVFCLÖVEPKWLEPKBZPYBCTQSYÅLJZHXQPIAEWACBPXSTQIJHZMVNNLÖÄDÖLEGMXDYIÄRJYWHWQ") ;En multipppel av 4. I det här fallet 16.
    (def width 4)

    (println "B length " (count B))
    (println "A length " (count A))

    (entities
      (actor controller (controller-actor A B width ))
      (actor stripe (stripe-actor (count A)))

      (actor fanout (fanout-actor ))

      (actor sw0 (sw-cell (count A)))
      (actor sw1 (sw-cell (count A)))
      (actor sw2 (sw-cell (count A)))
      (actor sw3 (sw-cell-printing (count A) (* (/ (count B) width ) (count A)) ) )

      (actor aligner (align-actor A B width))

      ;Returns the list of actors to be executed.
      ; (for [i (range 10)]
      ;   (actor i (sw-cell (count A)))
      ;   )

      (network
        ; (for [actor actors]
        ;   (con (actor :out) (waddap-actor :in))
        ;   )

        (con (controller :chan-contr-fan-a) (fanout :in-chan) )
        (con (controller :chan-stripe) (stripe :b-chan) )

        (con (stripe :chan-0) (sw0 :b-chan) )
        (con (stripe :chan-1) (sw1 :b-chan) )
        (con (stripe :chan-2) (sw2 :b-chan) )
        (con (stripe :chan-3) (sw3 :b-chan) )

        (con (fanout :chan-0) (sw0 :a-chan) )
        (con (fanout :chan-1) (sw1 :a-chan) )
        (con (fanout :chan-2) (sw2 :a-chan) )
        (con (fanout :chan-3) (sw3 :a-chan) )

        (con (sw0 :value) (sw1 :west) )
        (con (sw1 :value) (sw2 :west) )
        (con (sw2 :value) (sw3 :west) )
        (con (sw3 :value) (sw0 :west) {:initial-tokens (vec (repeat (count A) 0))} )

        (con (sw0 :aligner-value) (aligner :chan-0))
        (con (sw1 :aligner-value) (aligner :chan-1))
        (con (sw2 :aligner-value) (aligner :chan-2))
        (con (sw3 :aligner-value) (aligner :chan-3))

        )
      )

  (while true )

 )
