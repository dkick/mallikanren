(ns com.github.dkick.mallikanren.repl
  (:require
   [malli.core :as m]
   [malli.generator :as mg]))

(def Name
  [:map
   [:first :string]
   [:middle [:maybe :string]]
   [:last :string]])

(def Person
  [:schema
   {:registry
    {::parents
     [:map
      [:father [:ref ::person]]
      [:mother [:ref ::person]]]

     ::person
     [:map
      [:name Name]
      [:parents [:maybe [:ref ::parents]]]
      [:children [:vector [:ref ::person]]]]}}
   [:ref ::person]])

(comment
  (m/form Person)

  (mg/generate Person)
  ;; => {:name {:first "4S2Zq", :middle nil, :last "iMq9"},
  ;;     :parents nil,
  ;;     :children
  ;;     [{:name {:first "q1365", :middle "", :last "QR0M0H6"},
  ;;       :parents
  ;;       {:father
  ;;        {:name
  ;;         {:first "s50F9",
  ;;          :middle "pAbCGx0tC18zt3fzZJdWW",
  ;;          :last "1P9AovB92t4Fevn4RxsyxKY9"},
  ;;         :parents nil,
  ;;         :children []},
  ;;        :mother
  ;;        {:name
  ;;         {:first "oEmvlqcgymxK20H9Tkd", :middle nil, :last "0J6nceGZ"},
  ;;         :parents nil,
  ;;         :children []}},
  ;;       :children
  ;;       [{:name
  ;;         {:first "1NT7aZGotUX0",
  ;;          :middle "6A4ncS4OrcLqwr",
  ;;          :last "0p4SO7nxmpNhKvWx4Hh"},
  ;;         :parents nil,
  ;;         :children []}]}
  ;;      {:name
  ;;       {:first "56THD3o1P33Xju6Dzu", :middle nil, :last "7sR8pVJaX2F65"},
  ;;       :parents nil,
  ;;       :children []}]}

  ;; => {:name {:first "", :middle "Z", :last "U"},
  ;;     :parents
  ;;     {:father
  ;;      {:name {:first "35aE5LX", :middle nil, :last ""},
  ;;       :parents
  ;;       {:father
  ;;        {:name {:first "", :middle nil, :last "DN"},
  ;;         :parents
  ;;         {:father
  ;;          {:name {:first "", :middle nil, :last "F90"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "1n290",
  ;;            :middle nil,
  ;;            :last "4hPQhmu91RZDEf540JYSnz96qtYP"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children []},
  ;;        :mother
  ;;        {:name {:first "", :middle "", :last "Z"},
  ;;         :parents
  ;;         {:father
  ;;          {:name {:first "m48", :middle "GD2y7pl81N0", :last "R"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name {:first "P15e7yCELF9Fdkr", :middle nil, :last "ZAsK4m"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children
  ;;         [{:name {:first "0b", :middle nil, :last "t3S29nECx"},
  ;;           :parents nil,
  ;;           :children []}]}},
  ;;       :children
  ;;       [{:name {:first "JW", :middle "", :last "yL"},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name
  ;;           {:first "MJu",
  ;;            :middle "qjJH37ui2z3w10T84ckVXH",
  ;;            :last "uv2Vza85Rvorx5qqXJ5SL"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "YJHQ8L8wz3eWneYL6CMyE",
  ;;            :middle "Lq0",
  ;;            :last "jye59F0L50BR2qyGXCr7"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name
  ;;         {:first "pLU4YCU43C",
  ;;          :middle "1x60j4ciOH88vXjcmRQ3",
  ;;          :last "JSCpZIE67zw6ksV6s3"},
  ;;         :parents nil,
  ;;         :children []}
  ;;        {:name {:first "m", :middle "6", :last "5"},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name
  ;;           {:first "2UJpmWa72c2J80db",
  ;;            :middle "7tKGWk59ieu7O5H",
  ;;            :last "J7qreXIVCd78txz90Vxz15"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "4wnY11dktvNdf9DD70",
  ;;            :middle "3059E",
  ;;            :last "KI51MXN9zbK1O7AsC2xVJ1"},
  ;;           :parents nil,
  ;;           :children []}]}]},
  ;;      :mother
  ;;      {:name {:first "VdFp3o", :middle "YbojBAQT", :last "JiK9jaC"},
  ;;       :parents nil,
  ;;       :children
  ;;       [{:name {:first "", :middle nil, :last ""},
  ;;         :parents
  ;;         {:father
  ;;          {:name
  ;;           {:first "xG6F8nYZdFmMubOwZ9cmAfIUj9B",
  ;;            :middle "9t1LdlAc",
  ;;            :last "44p39MO482k9Dp88J7Wo8WQ8n"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "Bid56O2NB00297G",
  ;;            :middle nil,
  ;;            :last "hL5DA3WV6MgdzIyCV9kr"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children []}
  ;;        {:name {:first "", :middle "l", :last "q"},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name {:first "ZrOBBlMF7Z7", :middle "UV8", :last "l93djSU"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "90aO0l",
  ;;            :middle "h42Zo",
  ;;            :last "d3k81JaO0E3we3xVZ5A"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "j4", :middle "", :last "Ro"},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name
  ;;           {:first "voN016e2duVm9NDWosp4eHmV01",
  ;;            :middle nil,
  ;;            :last "bOckZVDn1Z0eA8seb"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "", :middle nil, :last ""},
  ;;         :parents
  ;;         {:father
  ;;          {:name
  ;;           {:first "ufaxwgrhK",
  ;;            :middle nil,
  ;;            :last "1SOvBiLvfpSBfZM0cSq0p"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "cMT8N1AUwM3J76PllXVJ8TU98", :middle nil, :last "Ay"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children
  ;;         [{:name
  ;;           {:first "FVz2Mczb",
  ;;            :middle "92CnP65aBo4QQ2tk9yBK",
  ;;            :last "Ms2h3P3PQ8X"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "ME6il",
  ;;            :middle "Lm",
  ;;            :last "J9v4JQEx88Xwfr866bmg9pO3jU"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "cj", :middle "IL", :last "i2"},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name
  ;;           {:first "EocNxbiQvx8IpXSf25Cv1OJzU0s",
  ;;            :middle nil,
  ;;            :last "8Z4T68l"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "g6", :middle nil, :last ""},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name {:first "3K6hC", :middle nil, :last ""},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "", :middle nil, :last ""},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name
  ;;           {:first "DS3ib30qYTV6ccl8Ztu8DDx",
  ;;            :middle nil,
  ;;            :last "0tBPN9Vn720WqM1YKtvRIK0dQC7rLY"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "2UBrsSns8aXOwChH91q1xev1",
  ;;            :middle nil,
  ;;            :last "a36PGmIX11X1ULG74fM"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "l3", :middle nil, :last "r"},
  ;;         :parents
  ;;         {:father
  ;;          {:name
  ;;           {:first "Y6q99YmHQqHG6bd8DZ4gvpb",
  ;;            :middle "0KgsnCWQdY83mAgL",
  ;;            :last "Ar6Oo7g24Rg1B8PXo"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "yY3PFvvUb8n2K1tS", :middle nil, :last "Q4QG8K"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children
  ;;         [{:name
  ;;           {:first "Za39GI9F0bBetlw48Srm5QxG19d",
  ;;            :middle nil,
  ;;            :last "dyNe3Q4fhuJGymI1jnu7z2NmEdK1a6"},
  ;;           :parents nil,
  ;;           :children []}]}]}},
  ;;     :children
  ;;     [{:name {:first "IlN2", :middle "pwlGCY", :last "oKR4O"},
  ;;       :parents nil,
  ;;       :children
  ;;       [{:name {:first "", :middle nil, :last "i"},
  ;;         :parents
  ;;         {:father
  ;;          {:name
  ;;           {:first "dy013H3ak",
  ;;            :middle "2vp5x",
  ;;            :last "07192woApS795N8x8eBgM8E0RI3"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "90HfK1mxlaovktWi05YN049",
  ;;            :middle "ls9g3B",
  ;;            :last "7g8"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children
  ;;         [{:name {:first "1C5gaa91n8HEA557", :middle nil, :last "8W"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "U", :middle nil, :last ""},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name {:first "64blyO19AHexyQp8", :middle nil, :last "hw"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "iY", :middle nil, :last ""},
  ;;         :parents
  ;;         {:father
  ;;          {:name
  ;;           {:first "tYk1z9PJav2J63d60o02TMuUOro7Q",
  ;;            :middle nil,
  ;;            :last "H0Z86r40fNB"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "0Tn54x95V1UI0Pm9Wx9QE25cq1e",
  ;;            :middle nil,
  ;;            :last "65k"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children
  ;;         [{:name
  ;;           {:first "1nWvw2zVdYbqTHc4EW585MmX",
  ;;            :middle nil,
  ;;            :last "j79XZVqH3LFD900Q1K5rKS18Aj"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name {:first "", :middle nil, :last "p"},
  ;;           :parents nil,
  ;;           :children []}]}]}
  ;;      {:name {:first "E2", :middle nil, :last "qXK"},
  ;;       :parents nil,
  ;;       :children
  ;;       [{:name {:first "", :middle nil, :last "Ka"},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name
  ;;           {:first "pryXlIlGqk002c6GxBzoNtzY",
  ;;            :middle nil,
  ;;            :last "PjWl9"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "564B5N2WubmN2vppphKu",
  ;;            :middle nil,
  ;;            :last "lePpEQC6fnkkh428XE1qO"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "uq", :middle nil, :last ""},
  ;;         :parents
  ;;         {:father
  ;;          {:name {:first "", :middle "v", :last "dMcdGrnS2mGv1G3"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "OoQzanscf2ern3H75Yo8a64p6",
  ;;            :middle "rUkX77f1OOb1nhixA8l",
  ;;            :last "1YmmlgqBuZcN4ccDy4j"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children
  ;;         [{:name {:first "", :middle nil, :last "1jILrpU0WggNHFGkk2"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "22Cu3wb27vfE4549Fp107Q0H7gITb",
  ;;            :middle nil,
  ;;            :last "WmKY426I"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name {:first "", :middle "", :last ""},
  ;;         :parents nil,
  ;;         :children
  ;;         [{:name
  ;;           {:first "OkS14UVNj4U9771FOgia98",
  ;;            :middle "m7GFGRN5k8",
  ;;            :last "aB9LW"},
  ;;           :parents nil,
  ;;           :children []}
  ;;          {:name
  ;;           {:first "uyVw0HQUUB14Fo40atm7z180",
  ;;            :middle nil,
  ;;            :last "Vw5viUGg990JACOgdZsV3"},
  ;;           :parents nil,
  ;;           :children []}]}
  ;;        {:name
  ;;         {:first "hy935fIh4Pep",
  ;;          :middle "L",
  ;;          :last "qc7nWKodb9Up821Q4Pga58y1"},
  ;;         :parents nil,
  ;;         :children []}
  ;;        {:name {:first "", :middle nil, :last "N"},
  ;;         :parents nil,
  ;;         :children []}
  ;;        {:name {:first "", :middle "ls", :last "ng"},
  ;;         :parents
  ;;         {:father
  ;;          {:name
  ;;           {:first "ytO0Pb",
  ;;            :middle "VQz4rE391Hn08CZ",
  ;;            :last "vU3Ni1lG331n3C4K63rgv8ajWLd84"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "B6372cMmB",
  ;;            :middle "z7u0v2bK",
  ;;            :last "6bPmle112s3PtKSC7bJGXfpqv4rW1"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children []}
  ;;        {:name {:first "7", :middle nil, :last ""},
  ;;         :parents
  ;;         {:father
  ;;          {:name {:first "i7u", :middle "", :last "h9T7M1y"},
  ;;           :parents nil,
  ;;           :children []},
  ;;          :mother
  ;;          {:name
  ;;           {:first "656Iwla537hjyE1pFn0ww4Eq7",
  ;;            :middle "tIM1D349vw4Lo",
  ;;            :last "g89kNEtnF"},
  ;;           :parents nil,
  ;;           :children []}},
  ;;         :children []}]}]}
  :comment)
