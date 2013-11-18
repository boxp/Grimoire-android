(ns org.boxlab.grimoire.commands
  (:use [org.boxlab.grimoire.data]
        [clojure.string :only [join split]])
  (:import (twitter4j TwitterFactory Query Status User UserMentionEntity)
           (twitter4j.auth AccessToken)
           (twitter4j StatusUpdate))) 

(defn get-elmt 
  "main-layout内のelmtをgetText"
  [elmt]
  (str ^String (.getText (elmt (.getTag body)))))

(defn take-elmt
  "main-layout内のelmtを取得"
  [elmt]
  (elmt (.getTag body)))

; コマンドたち
; ツイート
(defn post 
  "引数の文字列を全て一つにまとめてツイートする．140文字以上の時は省略されます．"
  [& input]
  (try 
    (str "Success:" 
      (.getText 
        (.updateStatus @twitter 
          (if 
            (> (count (seq (apply str input))) 140)
              (str (apply str (take 137 (seq (apply str input)))) "...")
              (apply str input)))))
    (catch Exception e (println "Something has wrong." e))))

; 20件のツイート取得
(defn showtl 
  " Showing 20 new tweets from HomeTimeline.\nUsage: (showtl)"
  []
  (let [statusAll (reverse (.getHomeTimeline @twitter))]
    (loop [status statusAll i 1]
      (if (= i 20)
        nil
        (do
          (println (.getScreenName (.getUser (first status))) ":" (.getText (first status)))
          (recur (rest status) (+ i 1)))))))

; コマンド一覧
(defn help []
  (str     "*** Grimoire-cli Commands List***\n\n"
           "post: ツイート(例：(post \"test\"))\n"
           "start: ユーザーストリームをスタートさせる.\n"
           "stop: ユーザーストリームをストップさせる.\n"
           "fav: ふぁぼる(例：(fav 2))\n"
           "retweet: リツイートする(例：(ret 3))\n"
           "favret: ふぁぼってリツイートする(例：(favret 6))\n"
           "reply: リプライを送る(例：(reply 1 \"hoge\"))\n"
           "del: ツイートを削除(例：(del 168))\n"
           "autofav!: 指定したユーザーのツイートを自動でふぁぼる(例：(autofav! \"@If_I_were_boxp\"))\n"
           "follow: ツイートのユーザーをフォローする(例：(follow 58))\n"
           "open-url: ツイートのURLをブラウズする（例：(open-url: 19)\n"
           "print-node!: テキストをタイムラインに表示します（例：(print-node! \"Too late.\"))\n"
           "Get more information to (doc <commands>)."))

; リツイート
(defn ret
  "statusnum(ツイートの右下に表示)を指定してリツイート"
  [statusnum]
    (future
      (try 
        (let [status (.retweetStatus @twitter (.getId (@tweets statusnum)))]
          (str 
            "Success retweet: @" 
            (.. status getUser getScreenName)
            " - "
            (.. status getText)))
        (catch Exception e "something has wrong."))))

; リツイートの取り消し
(defn unret
  "statusnum(ツイートの右下に表示)を指定してリツイートを取り消し"
  [statusnum]
    (future
      (try 
        (let [status (.destroyStatus @twitter (.getId (@tweets statusnum)))]
          (str 
            "Success unretweet: @" 
            (.. status getUser getScreenName)
            " - "
            (.. status getText)))
        (catch Exception e "something has wrong."))))

; ふぁぼふぁぼ
(defn fav
  "statusnum(ツイートの右下に表示)を指定してふぁぼ"
  [statusnum]
    (future
      (try
        (let [status (.createFavorite @twitter (.getId (@tweets statusnum)))]
          (str
            "Success Fav: @" 
            (.. status getUser getScreenName)
            " - "
            (.. status getText)))
        (catch Exception e "something has wrong."))))

; あんふぁぼ
(defn unfav
  "statusnum(ツイートの右下に表示)を指定してあんふぁぼ"
  [statusnum]
    (future
      (try
        (let [status (.destroyFavorite @twitter (.getId (@tweets statusnum)))]
          (str
            "Success UnFav: @" 
            (.. status getUser getScreenName)
            " - "
            (.. status getText)))
        (catch Exception e "something has wrong."))))

; ふぁぼRT
; clean
(defn favret 
  "statusnum(ツイートの右下に表示)を指定してふぁぼ＆リツイート"
  [statusnum]
    (do 
      (fav statusnum)
      (ret statusnum)))

; ふぁぼRT
; clean
(defn unfavret 
  "statusnum(ツイートの右下に表示)を指定してふぁぼ＆リツイートを取り消す"
  [statusnum]
    (do 
      (unfav statusnum)
      (unret statusnum)))

; つい消し
(defn del
  "statusnum(ツイートの右下に表示)を指定してツイートを取り消す"
  [statusnum]
    (try 
      (let [status (@tweets statusnum)]
        (do
          (.destroyStatus @twitter (.getId status))
          (str 
            "Success delete: @" 
            (.. status getUser getScreenName)
            " - "
            (.. status getText))))
      (catch Exception e "something has wrong.")))

; リプライ
(defn reply [statusnum & texts]
  "statusnum(ツイートの右下に表示)とテキストを指定して,返信する"
  (let [reply (str \@ (.. (@tweets statusnum) getUser getScreenName) " " (apply str texts))]
    (future
      (do
        (println (str (apply str (take 137 (seq reply)))))
        (str "Success:" 
          (.getText
            (.updateStatus 
              @twitter 
              (doto
                (StatusUpdate. 
                  (if 
                    (> (count (seq reply)) 140)
                    (str (apply str (take 137 (seq reply))) "...")
                    (str \@ (.. (@tweets statusnum) getUser getScreenName) " " (apply str texts))))
                (.inReplyToStatusId (.getId (@tweets statusnum)))))))))))

