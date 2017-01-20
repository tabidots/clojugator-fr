(ns clojugator-fr.core
  (:require [clojure.string :as string])
  (:require [clojure.algo.generic.functor :as functor :only (fmap)])
  (:gen-class))

;; ============================================================================
;; prepare the data
;; ============================================================================

(def aspirated-h-verbs
  ["hâbler" "hacher" "haïr" "haler" "hâler" "haleter" "hancher" "handicaper"
   "hannetonner" "hanter" "happer" "haranguer" "harasser" "harceler" "harder"
   "haricoter" "harnacher" "harpailler" "harper" "harponner" "hasarder" "hâter"
   "haubanner" "hausser" "haver" "havir" "héler" "hennir" "hercher" "hérisser"
   "hérissonner" "herscher" "herser" "heurter" "hier" "hiérarchiser" "highlifer"
   "hisser" "hocher" "hogner" "hôler" "hollandiser" "hongrer" "hongroyer" "honnir"
   "hoqueter" "hotter" "houblonner" "houler" "hourder" "houspiller" "housser"
   "hucher" "huer" "hululer" "humer" "humoter" "hurler" "hutter"])

(defn- remove-last-n-chars [n s]
  (string/join (drop-last n s)))

(defn root [infinitive]
  (cond
    (#{"voir" "asseoir"} infinitive)        infinitive
    (.endsWith infinitive "oir")            (remove-last-n-chars 3 infinitive)
    :else                                   (remove-last-n-chars 2 infinitive)))

(defn- group3-stem [verb]
  (cond

    ;; SUPER-IRREGULAR VERBS --------------------------------------------------
    ;; Two extra keys - subj-s and subj-p for présent du subjonctif
    (= verb "aller")            {:pres-s  "vais!"  :pres-p   "all"
                                 :pres-s2 "vas!"
                                 :pres-s3 "va!"    :pres-p3  "vont!"
                                 :subj-s  "aill"   :subj-p   "all"
                                 :past    "all"    :pp       "allé"    :fut "ir"}

    (= verb "être")             {:pres-s  "suis!"  :pres-p   "sommes!"
                                 :pres-s2 "es!"    :pres-p2  "êtes!"
                                 :pres-s3 "est!"   :pres-p3  "sont!"
                                 :subj-s  "soi"    :subj-p   "soy"
                                 :past    "fu"     :pp       "été"     :fut "ser"}

    (= verb "avoir")            {:pres-s  "ai!"    :pres-p   "av"
                                 :pres-s2 "as!"
                                 :pres-s3 "a!"     :pres-p3  "ont!"
                                 :subj-s  "ai"     :subj-p   "ay"
                                 :past    "eu"     :pp       "eu"      :fut "aur"}

    (.endsWith verb "faire")    {:pres-s  (string/replace verb #"faire$" "fai")
                                 :pres-p  (string/replace verb #"faire$" "fais")
                                 :pres-p2 (string/replace verb #"faire$" "faites!")
                                 :pres-p3 (string/replace verb #"faire$" "font!")
                                 :subj-s  (string/replace verb #"faire$" "fass")
                                 :subj-p  (string/replace verb #"faire$" "fass")
                                 :past    (string/replace verb #"faire$" "fi")
                                 :pp      (string/replace verb #"faire$" "fait")
                                 :fut     (string/replace verb #"faire$" "fer")}

    (= verb "valoir")           {:pres-s  "vaux!"  :pres-p   "val"
                                 :pres-s3 "vaut!"
                                 :subj-s  "vaill"
                                 :past    "valu"   :pp       "valu"    :fut "vaudr"}

    (= verb "vouloir")          {:pres-s  "veux!"  :pres-p   "voul"
                                 :pres-s3 "veut!"  :pres-p3  "veul"
                                 :subj-s  "veuill"
                                 :past    "voulu"  :pp       "voulu"   :fut "voudr"}

    (= verb "savoir")           {:pres-s  "sai"    :pres-p   "sav"
                                 :subj-s  "sach"   :subj-p   "sach"
                                 :past    "su"     :pp       "su"      :fut "saur"}

    (= verb "pouvoir")          {:pres-s  "peux!"  :pres-p   "pouv"
                                 :pres-s3 "peut"   :pres-p3  "peuv"
                                 :subj-s  "puiss"  :subj-p   "puiss"
                                 :past    "pu"     :pp       "pu"      :fut "pourr"}

    (= verb "falloir")          {:pres-s  "!"      :pres-p   "!"
                                 :pres-s3 "faut!"
                                 :subj-s  "faill"  :subj-p   "!"
                                 :past    "fallu"  :pp       "fallu"   :fut "faudr"}

    ;; -OIR VERBS -------------------------------------------------------------
    ;; recevoir, concevoir, etc.
    (.endsWith verb "cevoir")   {:pres-s  (string/replace verb #"cevoir$" "çoi")
                                 :pres-p  (string/replace verb #"cevoir$" "cev")
                                 :pres-p3 (string/replace verb #"cevoir$" "çoiv")
                                 :past    (string/replace verb #"cevoir$" "çu")
                                 :pp      (string/replace verb #"cevoir$" "çu")
                                 :fut     (string/replace verb #"cevoir$" "cevr")}
    (= verb "devoir")           {:pres-s "doi" :pres-p "dev" :pres-p3 "doiv"
                                 :past "dû" :pp "du" :fut "devr"}
    (= verb "mouvoir")          {:pres-s "meu" :pres-p "mouv" :pres-p3 "meuv"
                                 :past "mû" :pp "mu" :fut "mouvr"}
    (= verb "pleuvoir")         {:pres-s "!" :pres-s3 "pleu" :pres-p "!"
                                 :past "plu" :pp "plu" :fut "pleuvr"}
    ;; émouvoir, promouvoir, etc.
    (.endsWith verb "mouvoir")  {:pres-s  (string/replace verb #"mouvoir$" "meu")
                                 :pres-p  (string/replace verb #"mouvoir$" "mouv")
                                 :pres-p3 (string/replace verb #"mouvoir$" "meuv")
                                 :past    (string/replace verb #"mouvoir$" "mu")
                                 :pp      (string/replace verb #"mouvoir$" "mu")
                                 :fut     (string/replace verb #"mouvoir$" "mouvr")}
    ;; voir, revoir, etc.
    (.endsWith verb "voir")     {:pres-s  (string/replace verb #"voir$" "voi")
                                 :pres-p  (string/replace verb #"voir$" "voy")
                                 :pres-p3 (string/replace verb #"voir$" "voi")
                                 :past    (string/replace verb #"voir$" "vi")
                                 :pp      (string/replace verb #"voir$" "vu")
                                 :fut     (string/replace verb #"voir$" "verr")}

    (= verb "asseoir")          {:pres-s "assoi" :pres-p "assoy" :pres-3 "assoi"
                                 :past "assi" :pp "assis" :fut "assoir"}

    ;; -Consonant + RE VERBS --------------------------------------------------
    ;; craindre, atteindre, rejoindre, etc.
    (.endsWith verb "indre")    {:pres-s  (string/replace verb #"ndre$" "n")
                                 :pres-p  (string/replace verb #"ndre$" "gn")
                                 :past    (string/replace verb #"ndre$" "gni")
                                 :pp      (string/replace verb #"ndre$" "nt")
                                 :fut     (string/replace verb #"ndre$" "ndr")}
    ;; prendre, apprendre, etc.
    (.endsWith verb "prendre")  {:pres-s  (string/replace verb #"endre$" "end")
                                 :pres-p  (string/replace verb #"endre$" "en")
                                 :pres-p3 (string/replace verb #"endre$" "enn")
                                 :past    (string/replace verb #"endre$" "i")
                                 :pp      (string/replace verb #"endre$" "is")
                                 :fut     (string/replace verb #"endre$" "endr")}
    ;; absoudre / résoudre
    (.endsWith verb "soudre")   {:pres-s  (string/replace verb #"oudre$" "ou")
                                 :pres-p  (string/replace verb #"oudre$" "olv")
                                 :pres-p3 (string/replace verb #"oudre$" "olv")
                                 :past    (string/replace verb #"oudre$" "olu")
                                 :pp      (string/replace verb #"oudre$" "olu")
                                 :fut     (string/replace verb #"oudre$" "oudr")}
    ;; moudre / coudre
    (= verb "moudre")           {:pres-s "moud" :pres-p "moul" :past "moulu" :pp "moulu" :fut "moudr"}
    (= verb "coudre")           {:pres-s "coud" :pres-p "cous" :past "cousu" :pp "cousu" :fut "coudr"}

    ;; vendre, perdre, répondre, etc.
    (.endsWith verb "dre")      {:pres-s (root verb) :pres-p (root verb)
                                 :past   (string/replace verb #"dre$" "du")
                                 :pp     (string/replace verb #"dre$" "du")
                                 :fut    (string/replace verb #"dre$" "dr")}

    ;; vaincre, convaincre
    (.endsWith verb "cre")      {:pres-s (string/replace verb #"ncre$" "nc")
                                 :pres-p (string/replace verb #"ncre$" "nq")
                                 :past   (string/replace verb #"ncre$" "nqui")
                                 :pp     (string/replace verb #"ncre$" "ncu")
                                 :fut    (string/replace verb #"ncre$" "ncr")}

    (= verb "rompre")           {:pres-s (root verb) :pres-p (root verb) :past "rompi" :pp  "rompu" :fut "rompr"}
    (= verb "battre")           {:pres-s "bat" :pres-p "batt" :past "batti" :pp  "battu" :fut "battr"}
    (.endsWith verb "mettre")   {:pres-s (remove-last-n-chars 3 verb) :pres-p (root verb)
                                 :past   (string/replace verb #"mettre$" "mi")
                                 :pp     (string/replace verb #"mettre$" "mis")
                                 :fut    (string/replace verb #"mettre$" "mettr")}

    (#{"naître" "renaître"} verb)
                                {:pres-s  (string/replace verb #"aître$" "ai")
                                 :pres-s3 (string/replace verb #"aître$" "aî")
                                 :pres-p  (string/replace verb #"aître$" "aiss")
                                 :past    (string/replace verb #"aître$" "aqui")
                                 :pp      (string/replace verb #"aître$" "é")
                                 :fut     (string/replace verb #"aître$" "aîtr")}
    ;; connaître, paraître, etc.
    (.endsWith verb "naître")   {:pres-s  (string/replace verb #"aître$" "ai")
                                 :pres-s3 (string/replace verb #"aître$" "aî")
                                 :pres-p  (string/replace verb #"aître$" "aiss")
                                 :past    (string/replace verb #"aître$" "u")
                                 :pp      (string/replace verb #"aître$" "u")
                                 :fut     (string/replace verb #"aître$" "aîtr")}
    ;; vivre, revivre, etc.
    (.endsWith verb "vivre")    {:pres-s  (string/replace verb #"ivre$" "i")
                                 :pres-p  (string/replace verb #"ivre$" "iv")
                                 :past    (string/replace verb #"ivre$" "écu")
                                 :pp      (string/replace verb #"ivre$" "écu")
                                 :fut     (string/replace verb #"ivre$" "ivr")}
    ;; suivre, poursuivre, etc.
    (.endsWith verb "ivre")     {:pres-s  (string/replace verb #"ivre$" "i")
                                 :pres-p  (string/replace verb #"ivre$" "iv")
                                 :past    (string/replace verb #"ivre$" "ivi")
                                 :pp      (string/replace verb #"ivre$" "ivi")
                                 :fut     (string/replace verb #"ivre$" "ivr")}

    ;; -IRE/-URE/-ORE VERBS ---------------------------------------------------
    ;; luire & nuire
    (#{"luire" "nuire"} verb)
                                {:pres-s  (string/replace verb #"uire$" "ui")
                                 :pres-p  (string/replace verb #"uire$" "uis")
                                 :past    (string/replace verb #"uire$" "uisi")
                                 :pp      (string/replace verb #"uire$" "ui")
                                 :fut     (string/replace verb #"uire$" "uir")}
    ;; traduire, conduire, etc.
    (.endsWith verb "uire")     {:pres-s  (string/replace verb #"uire$" "ui")
                                 :pres-p  (string/replace verb #"uire$" "uis")
                                 :past    (string/replace verb #"uire$" "uisi")
                                 :pp      (string/replace verb #"uire$" "uit")
                                 :fut     (string/replace verb #"uire$" "uir")}
    ;; dire, interdire, etc.
    (.endsWith verb "dire")     {:pres-s  (string/replace verb #"dire$" "di")
                                 :pres-p  (string/replace verb #"dire$" "dis")
                                 :pres-p2 (string/replace verb #"dire$" "dites!")
                                 :past    (string/replace verb #"dire$" "di")
                                 :pp      (string/replace verb #"dire$" "dit")
                                 :fut     (string/replace verb #"dire$" "dir")}
    ;; lire
    (.endsWith verb "lire")     {:pres-s  (string/replace verb #"lire$" "li")
                                 :pres-p  (string/replace verb #"lire$" "lis")
                                 :past    (string/replace verb #"lire$" "lu")
                                 :pp      (string/replace verb #"lire$" "lu")
                                 :fut     (string/replace verb #"lire$" "lir")}
    ;; conclure
    (.endsWith verb "ure")      {:pres-s  (root verb) :pres-p (root verb)
                                 :past    (string/replace verb #"ure$" "u")
                                 :pp      (string/replace verb #"ure$" "u")
                                 :fut     (string/replace verb #"ure$" "ur")}
    ;; clore
    (= verb "clore")            {:pres-s "clo" :pres-s3 "clô" :pres-p "clos" :past "!" :pp  "clos" :fut "clor"}
    ;; plaire
    (.endsWith verb "plaire")   {:pres-s  (string/replace verb #"plaire$" "plai")
                                 :pres-s3 (string/replace verb #"plaire$" "plaî")
                                 :pres-p  (string/replace verb #"plaire$" "plais")
                                 :past    (string/replace verb #"plaire$" "plu")
                                 :pp      (string/replace verb #"plaire$" "plu")
                                 :fut     (string/replace verb #"plaire$" "plair")}
    ;; boire
    (= verb "boire")            {:pres-s "boi" :pres-p "buv" :pres-p3 "boiv" :past "bu" :pp  "bu" :fut "boir"}
    ;; croire
    (.endsWith verb "oire")     {:pres-s  (string/replace verb #"oire$" "oi")
                                 :pres-p  (string/replace verb #"oire$" "oy")
                                 :pres-p3 (string/replace verb #"oire$" "oi")
                                 :past    (string/replace verb #"oire$" "u")
                                 :pp      (string/replace verb #"oire$" "u")
                                 :fut     (string/replace verb #"oire$" "oir")}
    ;; écrire, inscrire, etc.
    (.endsWith verb "crire")    {:pres-s  (string/replace verb #"crire$" "cri")
                                 :pres-p  (string/replace verb #"crire$" "criv")
                                 :past    (string/replace verb #"crire$" "crivi")
                                 :pp      (string/replace verb #"crire$" "crit")
                                 :fut     (string/replace verb #"crire$" "crir")}
    ;; suffire, confire, frire, circoncire, etc.
    (re-find #"[^aiueo][^aiueo]ire$" verb)
                                {:pres-s  (string/replace verb #"ire$" "i")
                                 :pres-p  (string/replace verb #"ire$" "is")
                                 :past    (string/replace verb #"ire$" "i")
                                 :pp      (string/replace verb #"ire$" "i")
                                 :fut     (string/replace verb #"ire$" "ir")}
    ;; rire
    (.endsWith verb "rire")     {:pres-s  (string/replace verb #"rire$" "ri")
                                 :pres-p  (string/replace verb #"rire$" "ri")
                                 :past    (string/replace verb #"rire$" "ri")
                                 :pp      (string/replace verb #"rire$" "ris")
                                 :fut     (string/replace verb #"rire$" "rir")}

    ;; -IR irregular VERBS ----------------------------------------------------
    (= verb "mourir")           {:pres-s "meur" :pres-p "mour" :pres-p3 "meur"
                                 :past "mouru" :pp "mort" :fut "mourr"}
    (.endsWith verb "courir")   {:pres-s  (root verb)
                                 :pres-p  (root verb)
                                 :past    (string/replace verb #"rir$" "ru")
                                 :pp      (string/replace verb #"rir$" "ru")
                                 :fut     (string/replace verb #"rir$" "rr")}
    ;; venir, soutenir, etc.
    (re-find #"[vt]enir$" verb) {:pres-s  (string/replace verb #"enir$" "ien")
                                 :pres-p  (string/replace verb #"enir$" "en")
                                 :pres-p3 (string/replace verb #"enir$" "ienn")
                                 :past    (string/replace verb #"enir$" "in")
                                 :pp      (string/replace verb #"enir$" "enu")
                                 :fut     (string/replace verb #"enir$" "iendr")}
    ;; ouvrir, offrir, etc. - these verbs don't use the /s/s/t/ paradigm for singular endings
    (re-find #"[fv]rir$" verb)  {:pres-s  (string/replace verb #"rir$" "re!")
                                 :pres-s2 (string/replace verb #"rir$" "res!")
                                 :pres-p  (root verb)
                                 :past    (string/replace verb #"rir$" "ri")
                                 :pp      (string/replace verb #"rir$" "ert")
                                 :fut     verb}
    ;; partir, dormir, servir
    (re-find #"[tmv]ir$" verb)  {:pres-s  (remove-last-n-chars 3 verb)
                                 :pres-p  (root verb)
                                 :past    (remove-last-n-chars 1 verb)
                                 :pp      (remove-last-n-chars 1 verb)
                                 :fut     verb}
    (= verb "cueillir")         {:pres-s "cueille!" :pres-s2 "cueilles!" :pres-p  "cueill"
                                 :past "cueilli" :pp "cueilli" :fut "cueiller"}
    (= verb "haïr")             {:pres-s "hai" :pres-p "haïss"
                                 :past "haï" :pp "haï" :fut "haïr"}

    :else                       {}))

(defn- verb-group [verb]
  (cond
    (= verb "aller") 3
    (some #(.endsWith verb %) ["mir" "tir" "rir" "llir" "venir" "tenir" "oir"]) 3
    (.endsWith verb "ir") 2
    (.endsWith verb "er") 1
    (.endsWith verb "re") 3
    :else 0))

;; ============================================================================
;; concatenating functions
;; ============================================================================

(defn- fix-spelling [s]
  (-> s
      (string/replace #"voier(?=[aeo])" "verr")    ; exception for futur/conditionnel of `envoyer`
      (string/replace #"soyi" "soy")               ; exception for subjonctif of `être`
      (string/replace #"sommes!(?=[ai])" "ét")     ; exception for imparfait of `être`
      (string/replace #"!\w+$" "")                 ; use ! to mark exceptional stems (ignore everything after)
      (string/replace #"([cdt])t$" "$1")           ; vend + t -> vend, vainc + t -> vainc
      (string/replace #"i(n?)\^" "î$1")            ; reçu + ^mes -> reçûmes, vin + ^tes -> vîntes
      (string/replace #"u\^" "û")
      (string/replace #"ï\^" "ï")                  ; haï + ^t -> haït
      (string/replace #"\^" "â")))                 ; all + ^t -> allât

(defn- stem-change [verb]
  (cond
    (some #{verb} ["celer" "déceler" "harceler" "receler" "ciseler" "démanteler" "écarteler"
                   "encasteler" "geler" "congeler" "décongeler" "dégeler" "surgeler" "marteler"
                   "modeler" "peler" "acheter" "racheter" "corseter" "fileter" "fureter" "haleter"])
                                   :grave
    (.endsWith verb "eter")        :double-t
    (.endsWith verb "eler")        :double-l
    (re-find #"e[^aiueo]er$" verb) :grave
    (.endsWith verb "yer")         :y-to-i))

(defn- smart-prepend
  "Concatenate stem + ending, accounting for certain phonologically-based and lexically-based transformations."
  [target destination & transformation]
  (fix-spelling
    (cond
      (and (= transformation [:grave])           ; gel + e -> gèle (but gel + ez -> gelez)
           (re-find #"^e(?![rz]$)" destination)) (str (string/replace target #"e(?=\w$)" "è") destination)
      (and (= transformation [:double-t])        ; jet + e -> jette (but jet + é -> jeté)
           (re-find #"^e(?![rz]$)" destination)) (str target "t" destination)
      (and (= transformation [:double-l])        ; appel + e -> appelle (but appel + ez -> appelez)
           (re-find #"^e(?![rz]$)" destination)) (str target "l" destination)
      (and (= transformation [:y-to-i])          ; pay + e -> paie (but pay + ez -> payez)
           (re-find #"^e(?![rz]$)" destination)) (str (remove-last-n-chars 1 target) "i" destination)
      (and (.endsWith target "c")                ; lanc + ons -> lançons
           (re-find #"^[aâou]" destination))     (str (remove-last-n-chars 1 target) "ç" destination)
      (and (.endsWith target "g")                ; mang + ons -> mangeons
           (re-find #"^[aâou]" destination))     (str target "e" destination)
      :else                                      (str target destination))))

(defn- combine
  "(combine ('a' 'b' 'c') {:x '1' :y '2' :z '3'}) => {:x 'a1' :y 'b2' :z 'c3'}
  This is for tenses of irrgular verbs that do not use the same stem for all persons."
  [l m]
  (zipmap (keys m) (map #(smart-prepend %1 %2) l (vals m))))

;; ============================================================================
;; table generators
;; ============================================================================

(defn- conjugate-group1 [verb]
  (let [stem (remove-last-n-chars 2 verb)
        transformation (stem-change verb)
        prepend-stem #(smart-prepend stem % transformation)
        transform-row #(functor/fmap prepend-stem %)]
    (functor/fmap transform-row {:présent      {:je "e" :tu "es" :il "e" :nous "ons" :vous "ez" :ils "ent"}
                                 :passé-simple {:je "ai" :tu "as" :il "a" :nous "âmes" :vous "âtes" :ils "èrent"}
                                 :imparfait    {:je "ais" :tu "ais" :il "ait" :nous "ions" :vous "iez" :ils "aient"}
                                 :futur        {:je "erai" :tu "eras" :il "era" :nous "erons" :vous "erez" :ils "eront"}
                                 :conditionnel {:je "erais" :tu "erais" :il "erait" :nous "erions" :vous "eriez" :ils "eraient"}
                                 :prés-subj    {:je "e" :tu "es" :il "e" :nous "ions" :vous "iez" :ils "ent"}
                                 :impf-subj    {:je "asse" :tu "asses" :il "ât" :nous "assions" :vous "assiez" :ils "assent"}})))

(defn- conjugate-group2 [verb]
  (let [stem (remove-last-n-chars 2 verb)
        prepend-stem #(smart-prepend stem %)
        transform-row #(functor/fmap prepend-stem %)]
    (functor/fmap transform-row {:présent      {:je "is" :tu "is" :il "it" :nous "issons" :vous "issez" :ils "issent"}
                                 :passé-simple {:je "is" :tu "is" :il "it" :nous "îmes" :vous "îtes" :ils "irent"}
                                 :imparfait    {:je "issais" :tu "issais" :il "issait" :nous "issions" :vous "issiez" :ils "issaient"}
                                 :futur        {:je "irai" :tu "iras" :il "ira" :nous "irons" :vous "irez" :ils "iront"}
                                 :conditionnel {:je "irais" :tu "irais" :il "irait" :nous "irions" :vous "iriez" :ils "iraient"}
                                 :prés-subj    {:je "isse" :tu "isses" :il "isse" :nous "issions" :vous "issiez" :ils "issent"}
                                 :impf-subj    {:je "isse" :tu "isses" :il "ît" :nous "issions" :vous "issiez" :ils "issent"}})))

(defn- conjugate-group3 [verb]
  (if-let [all-stems (not-empty (group3-stem verb))]
    {:présent         (-> all-stems
                          ((juxt :pres-s
                                 #(get % :pres-s2 (get % :pres-s))
                                 #(get % :pres-s3 (get % :pres-s))
                                 :pres-p
                                 #(get % :pres-p2 (get % :pres-p))
                                 #(get % :pres-p3 (get % :pres-p))))
                          (combine {:je "s" :tu "s" :il "t" :nous "ons" :vous "ez" :ils "ent"}))

     :passé-simple    (if (= verb "aller")
                        {:je "allai" :tu "allas" :il "alla" :nous "allâmes" :vous "allâtes" :ils "allèrent"}
                        (let [stem (all-stems :past)]
                          (functor/fmap #(smart-prepend stem %)
                                        {:je "s" :tu "s" :il "t" :nous "^mes" :vous "^tes" :ils "rent"})))
     :imparfait       (let [stem (all-stems :pres-p)]
                        (functor/fmap #(smart-prepend stem %)
                                      {:je "ais" :tu "ais" :il "ait" :nous "ions" :vous "iez" :ils "aient"}))
     :futur           (let [stem (all-stems :fut)]
                        (functor/fmap #(str stem %)
                                      {:je "ai" :tu "as" :il "a" :nous "ons" :vous "ez" :ils "ont"}))

     :conditionnel    (let [stem (all-stems :fut)]
                        (functor/fmap #(str stem %)
                                      {:je "ais" :tu "ais" :il "ait" :nous "ions" :vous "iez" :ils "aient"}))

     :prés-subj       (-> all-stems
                          ((juxt #(get % :subj-s (get % :pres-p3 (get % :pres-p)))
                                 #(get % :subj-s (get % :pres-p3 (get % :pres-p)))
                                 #(get % :subj-s (get % :pres-p3 (get % :pres-p)))
                                 #(get % :subj-p (get % :pres-p))
                                 #(get % :subj-p (get % :pres-p))
                                 #(get % :subj-s (get % :pres-p3 (get % :pres-p)))))
                          (combine {:je "e" :tu "es" :il "e" :nous "ions" :vous "iez" :ils "ent"}))

     :impf-subj       (let [stem (all-stems :past)]
                        (functor/fmap #(smart-prepend stem %)
                                      {:je "sse" :tu "sses" :il "^t" :nous "ssions" :vous "ssiez" :ils "ssent"}))}
     {}))

(defn conjugation-table [verb]
  (let [group (verb-group verb)]
    (condp = group
      1 (conjugate-group1 verb)
      2 (conjugate-group2 verb)
      3 (conjugate-group3 verb)
      {})))

;; ============================================================================
;; other forms & compound conjugations
;; ============================================================================

(defn- participe-présent [verb]
  (condp = verb
    "savoir" "sachant"
    "avoir" "ayant"
    "être" "étant"
    (-> (get (group3-stem verb) :pres-p
             (root verb))
        (smart-prepend "ant"))))

(defn- participe-passé [verb]
  (let [group (verb-group verb)]
    (condp = group
      1 (str (root verb) "é(e)(s)")
      2 (str (root verb) "i(e)(s)")
      3 (-> (get (group3-stem verb) :pp
                 (root verb))
            (smart-prepend "(e)(s)")))))

(defn- impératif [verb]
  (condp = verb
    "savoir"  {:tu "sache"   :nous "sachons"   :vous "sachez"}
    "avoir"   {:tu "aie"     :nous "ayons"     :vous "ayez"}
    "être"    {:tu "sois"    :nous "soyons"    :vous "soyez"}
    "aller"   {:tu "va"      :nous "allons"    :vous "allez"}
    "vouloir" {:tu "veuille" :nous "veuillons" :vous "veuillez"}
    (let [present ((conjugation-table verb) :présent)]
      {:tu   (if (and (.endsWith (present :il) "e")
                      (not= 1 (verb-group verb)))    ; group-1 verbs always use the "tu" form
               (present :il)
               (present :tu))
       :nous (present :nous)
       :vous (present :vous)})))

(defn- passé-composé [verb]
  (let [pp (get (group3-stem verb) :pp
                (string/replace (participe-passé verb) #"\(.+$" ""))
        aux (if (some #{verb} ["devenir" "revenir" "monter" "rester" "sortir" "venir" "aller"
                               "naître" "renaître" "descendre" "entrer" "retourner" "tomber"
                               "rentrer" "arriver" "mourir" "partir" "décéder"])
              "être" "avoir")]
    (functor/fmap #(str % " " pp) ((conjugation-table aux) :présent))))

;; ============================================================================
;; phrase assembly functions
;; ============================================================================

(defn- h-aspiré? [infinitive]
  (#{aspirated-h-verbs} infinitive))

(defn- naive-add-pronoun [row hard-h]
  (if (and (string? hard-h)
           (not (re-find #" " ((comp fnext first) row))))  ;; exclude compound tenses
    (combine ["je_H" "tu_H" "il/elle/on_H" "nous_H" "vous_H" "ils/elles_H"] row)
    (combine ["je_" "tu_" "il/elle/on_" "nous_" "vous_" "ils/elles_"] row)))

(defn- negate [s]
  (if (re-find #"_[aiueéoh]" s)
    (string/replace s #"_(\S+)" " N'$1 pas")  ; use capital N to distinguish "ne" from the verb
    (string/replace s #"_(\S+)" " Ne_$1 pas")))

(defn- statement [s]
  (string/lower-case
    (if (re-find #"[jN]e_[aiueéoh]" s)
      (string/replace s #"(\w)e_(\w+)" "$1'$2")
      (string/replace s #"(\w)_H?(\w+)" "$1 $2"))))

(defn- invert [s]
  (-> s
      (string/replace #"^(.+) Ne_(\S+)" "ne $2-$1")  ;; neg + consonant / h aspiré
      (string/replace #"^(.+) N(\S+)" "n$2-$1")      ;; neg + vowel / h muet
      (string/replace #"^(.+)_(\S+)" "$2-$1")        ;; positive
      (string/replace #"e-je" "è-je")
      (string/replace #"peux-je" "puis-je")
      (string/replace #"([ae])-il" "$1-t-il")
      (string/replace #"H" "")
      (str " ?")))

(defn- transform-table [argset table]
  (let [transformations (condp = argset
                          #{:? :-} [invert negate]
                          #{:?}    [invert]
                          #{:-}    [statement negate]
                          [statement])
        transform #(functor/fmap (apply comp transformations) %)]
    (functor/fmap #(transform %) table)))

;; ============================================================================
;; the main event
;; ============================================================================

(defn conjugate [verb & args]
  (let [argset (set args)
        table (conjugation-table verb)]
    (cond->> table
             ;; questions exclude subjunctive
             (contains? argset :?) (remove #(re-find #"subj$" (name (key %))))
             :always               (into {:passé-composé (passé-composé verb)})
             :always               (functor/fmap #(naive-add-pronoun % (h-aspiré? verb)))
             :always               (transform-table argset)
             ;; negative statement includes negative imperative
             (= argset #{:-})      (into {:neg-impératif     (functor/fmap #(str "ne pas " %) (impératif verb))})
             ;; positive statement includes imperative + non-finite forms
             (empty? argset)       (into {:impératif         (impératif verb)
                                          :participe-passé   (participe-passé verb)
                                          :participe-présent (participe-présent verb)}))))
