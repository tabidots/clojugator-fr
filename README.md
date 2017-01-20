[![Clojars Project](https://img.shields.io/clojars/v/clojugator-fr.svg)](https://clojars.org/clojugator-fr)

# clojugator-fr

Conjugates French verbs in all common tenses, for all six persons, with options to make the phrase negative or interrogative, **in under 550 lines**, without a database. Possibly the most concise such library around. However, it is not designed to handle reflexive verbs at the moment.

## Usage

```clojure
user=> (require 'clojugator-fr.core)
```
Basically, ```(conjugate VERB)``` returns a map of maps that corresponds to a conjugation table. You can add the keywords ```:?``` and ```:-``` to make it interrogative and/or negative accordingly.

#### Full tables: Basic conjugation

```clojure
user=> (clojugator-fr.core/conjugate "jeter")
{ :participe-présent "jetant",
  :participe-passé "jeté(e)(s)",

  :présent { :je "je jette",
             :tu "tu jettes",
             :il "il/elle/on jette",
             :nous "nous jetons",
             :vous "vous jetez",
             :ils "ils/elles jettent"},

  :futur { :je "je jetterai",
           :tu "tu jetteras",
           :il "il/elle/on jettera",
           :nous "nous jetterons",
           :vous "vous jetterez",
           :ils "ils/elles jetteront"},

  :passé-simple { :je "je jetai",
                  :tu "tu jetas",
                  :il "il/elle/on jeta",
                  :nous "nous jetâmes",
                  :vous "vous jetâtes",
                  :ils "ils/elles jetèrent"},

  :passé-composé { :je "j'ai jeté",
                 :tu "tu as jeté",
                 :il "il/elle/on a jeté",
                 :nous "nous avons jeté",
                 :vous "vous avez jeté",
                 :ils "ils/elles ont jeté"},

  :imparfait { :je "je jetais",
               :tu "tu jetais",
               :il "il/elle/on jetait",
               :nous "nous jetions",
               :vous "vous jetiez",
               :ils "ils/elles jetaient"},

  :prés-subj { :je "je jette",
               :tu "tu jettes",
               :il "il/elle/on jette",
               :nous "nous jetions",
               :vous "vous jetiez",
               :ils "ils/elles jettent"},

  :impf-subj { :je "je jetasse",
               :tu "tu jetasses",
               :il "il/elle/on jetât",
               :nous "nous jetassions",
               :vous "vous jetassiez",
               :ils "ils/elles jetassent"},

  :impératif { :tu "jettes",
               :nous "jetons",
               :vous "jetez"},

  :conditionnel { :je "je jetterais",
                  :tu "tu jetterais",
                  :il "il/elle/on jetterait",
                  :nous "nous jetterions",
                  :vous "vous jetteriez",
                  :ils "ils/elles jetteraient"}
}
```

#### Full tables: Question inversion

```clojure
user=> (clojugator-fr.core/conjugate "recevoir" :?)
{ :présent { :je "reçois-je ?",
             :tu "reçois-tu ?",
             :il "reçoit-il/elle/on ?",
             :nous "recevons-nous ?",
             :vous "recevez-vous ?",
             :ils "reçoivent-ils/elles ?"},

  :passé-simple { :je "reçus-je ?",
                  :tu "reçus-tu ?",
                  :il "reçut-il/elle/on ?",
                  :nous "reçûmes-nous ?",
                  :vous "reçûtes-vous ?",
                  :ils "reçurent-ils/elles ?"},

  :passé-composé { :je "ai-je reçu ?",
                   :tu "as-tu reçu ?",
                   :il "a-t-il/elle/on reçu ?",
                   :nous "avons-nous reçu ?",
                   :vous "avez-vous reçu ?",
                   :ils "ont-ils/elles reçu ?"},

  :imparfait { :je "recevais-je ?",
               :tu "recevais-tu ?",
               :il "recevait-il/elle/on ?",
               :nous "recevions-nous ?",
               :vous "receviez-vous ?",
               :ils "recevaient-ils/elles ?"},

  :futur { :je "recevrai-je ?",
           :tu "recevras-tu ?",
           :il "recevra-t-il/elle/on ?",
           :nous "recevrons-nous ?",
           :vous "recevrez-vous ?",
           :ils "recevront-ils/elles ?"},

  :conditionnel { :je "recevrais-je ?",
                  :tu "recevrais-tu ?",
                  :il "recevrait-il/elle/on ?",
                  :nous "recevrions-nous ?",
                  :vous "recevriez-vous ?",
                  :ils "recevraient-ils/elles ?"}}
```

#### Full tables: Negatives

```clojure
user=> (clojugator-fr.core/conjugate "manger" :-)
{ :présent { :je "je ne mange pas",
             :tu "tu ne manges pas",
             :il "il/elle/on ne mange pas",
             :nous "nous ne mangeons pas",
             :vous "vous ne mangez pas",
             :ils "ils/elles ne mangent pas"},

  :futur { :je "je ne mangerai pas",
           :tu "tu ne mangeras pas",
           :il "il/elle/on ne mangera pas",
           :nous "nous ne mangerons pas",
           :vous "vous ne mangerez pas",
           :ils "ils/elles ne mangeront pas"},

  :passé-simple { :je "je ne mangeai pas",
                  :tu "tu ne mangeas pas",
                  :il "il/elle/on ne mangea pas",
                  :nous "nous ne mangeâmes pas",
                  :vous "vous ne mangeâtes pas",
                  :ils "ils/elles ne mangèrent pas"},

  :passé-composé { :je "je n'ai pas mangé",
                   :tu "tu n'as pas mangé",
                   :il "il/elle/on n'a pas mangé",
                   :nous "nous n'avons pas mangé",
                   :vous "vous n'avez pas mangé",
                   :ils "ils/elles n'ont pas mangé"},

  :imparfait { :je "je ne mangeais pas",
               :tu "tu ne mangeais pas",
               :il "il/elle/on ne mangeait pas",
               :nous "nous ne mangions pas",
               :vous "vous ne mangiez pas",
               :ils "ils/elles ne mangeaient pas"},

  :conditionnel { :je "je ne mangerais pas",
                  :tu "tu ne mangerais pas",
                  :il "il/elle/on ne mangerait pas",
                  :nous "nous ne mangerions pas",
                  :vous "vous ne mangeriez pas",
                  :ils "ils/elles ne mangeraient pas"},

  :prés-subj { :je "je ne mange pas",
               :tu "tu ne manges pas",
               :il "il/elle/on ne mange pas",
               :nous "nous ne mangions pas",
               :vous "vous ne mangiez pas",
               :ils "ils/elles ne mangent pas"},

  :impf-subj { :je "je ne mangeasse pas",
               :tu "tu ne mangeasses pas",
               :il "il/elle/on ne mangeât pas",
               :nous "nous ne mangeassions pas",
               :vous "vous ne mangeassiez pas",
               :ils "ils/elles ne mangeassent pas"},

  :neg-impératif { :tu "ne pas manges",
                 :nous "ne pas mangeons",
                 :vous "ne pas mangez"},
}
```

#### Full tables: Negative questions

```clojure
user=> (clojugator-fr.core/conjugate "soutenir" :- :?)
{ :présent { :je "ne soutiens-je pas ?",
             :tu "ne soutiens-tu pas ?",
             :il "ne soutient-il/elle/on pas ?",
             :nous "ne soutenons-nous pas ?",
             :vous "ne soutenez-vous pas ?",
             :ils "ne soutiennent-ils/elles pas ?"},

  :passé-simple { :je "ne soutins-je pas ?",
                  :tu "ne soutins-tu pas ?",
                  :il "ne soutint-il/elle/on pas ?",
                  :nous "ne soutînmes-nous pas ?",
                  :vous "ne soutîntes-vous pas ?",
                  :ils "ne soutinrent-ils/elles pas ?"},

  :passé-composé { :je "n'ai-je pas soutenu ?",
                   :tu "n'as-tu pas soutenu ?",
                   :il "n'a-t-il/elle/on pas soutenu ?",
                   :nous "n'avons-nous pas soutenu ?",
                   :vous "n'avez-vous pas soutenu ?",
                   :ils "n'ont-ils/elles pas soutenu ?"},

  :imparfait { :je "ne soutenais-je pas ?",
               :tu "ne soutenais-tu pas ?",
               :il "ne soutenait-il/elle/on pas ?",
               :nous "ne soutenions-nous pas ?",
               :vous "ne souteniez-vous pas ?",
               :ils "ne soutenaient-ils/elles pas ?"},

  :futur { :je "ne soutiendrai-je pas ?",
           :tu "ne soutiendras-tu pas ?",
           :il "ne soutiendra-t-il/elle/on pas ?",
           :nous "ne soutiendrons-nous pas ?",
           :vous "ne soutiendrez-vous pas ?",
           :ils "ne soutiendront-ils/elles pas ?"},

  :conditionnel { :je "ne soutiendrais-je pas ?",
                  :tu "ne soutiendrais-tu pas ?",
                  :il "ne soutiendrait-il/elle/on pas ?",
                  :nous "ne soutiendrions-nous pas ?",
                  :vous "ne soutiendriez-vous pas ?",
                  :ils "ne soutiendraient-ils/elles pas ?"}
}
```

#### Specific tense
```clojure
user=> (get-in (clojugator-fr.core/conjugate "joindre" :-) [:passé-simple])
{ :je "je ne joignis pas",
  :tu "tu ne joignis pas",
  :il "il/elle/on ne joignit pas",
  :nous "nous ne joignîmes pas",
  :vous "vous ne joignîtes pas",
  :ils "ils/elles ne joignirent pas"}
```

#### Specific tense and person

```clojure
user=> (get-in (clojugator-fr.core/conjugate "acheter" :? :-) [:futur :tu])
"n'achèteras-tu pas ?"
```

## License

Copyright © 2017 Justin Douglas

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
