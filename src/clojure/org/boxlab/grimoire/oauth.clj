(ns org.boxlab.grimoire.oauth
  (:use [org.boxlab.grimoire.data]
        [neko.notify]
        [neko.data :only [get-shared-preferences]])
  (:import (twitter4j Status Twitter TwitterFactory TwitterException)
           (twitter4j.auth AccessToken OAuthAuthorization)
           (twitter4j.conf ConfigurationContext)))

(def consumers {:consumerKey "Blnxqqx44rdGTZsBYI4bKw" :consumerSecret "bmQIczed6gbdqkN0V8tV11Carwy2PLj7l2bOIAdcoE"})
(def consumerKey (:consumerKey consumers))
(def consumerSecret (:consumerSecret consumers))

(defn get-oauthtoken!
  []
  (let [conf (ConfigurationContext/getInstance) 
        auth (doto (OAuthAuthorization. conf) 
                (.setOAuthConsumer consumerKey,consumerSecret)) ]
    (reset! oauthtoken
      (.. auth getOAuthRequestToken))))

(defn gen-tokens [pin] 
  (let [conf (ConfigurationContext/getInstance) 
        auth (doto (OAuthAuthorization. conf) 
                (.setOAuthConsumer consumerKey,consumerSecret))]
       (do  
         (let 
           [twitterTokens (.getOAuthAccessToken auth @oauthtoken pin)
            esp (. sp edit)
            token-map {:token (.getToken twitterTokens)
                       :tokenSecret (.getTokenSecret twitterTokens)}]
           (do
             ; token縺ｮ譖ｸ縺崎ｾｼ縺ｿ
             (reset! tokens token-map)
             (assoc! esp
                :tokens (str token-map))
             (. esp commit))))))

(defn gen-twitter []
  (let [twitterins (doto (.getInstance (TwitterFactory.))
                      (.setOAuthConsumer consumerKey,consumerSecret)
                      (.setOAuthAccessToken 
                        (AccessToken. 
                          (:token @tokens) 
                          (:tokenSecret @tokens))))
        screen-name-key (keyword (. twitterins getScreenName))]
    (do
      (reset! twitter twitterins)
      (dosync 
        (alter twitters merge
          {screen-name-key twitterins})))))

(defn token-2-twitter
  "token-mapからtwitterインスタンスを生成して返します"
  [token-map]
  (doto (.getInstance (TwitterFactory.))
      (.setOAuthConsumer consumerKey,consumerSecret)
      (.setOAuthAccessToken 
        (AccessToken. 
          (:token token-map) 
          (:tokenSecret token-map)))))

(defn pin-2-token
  "oauthtokenからpinを使って:token,:tokenSecretを持つtokenmapを生成し，返す."
  [pin]
  (let [conf (ConfigurationContext/getInstance) 
        auth (doto (OAuthAuthorization. conf) 
                (.setOAuthConsumer consumerKey,consumerSecret))
        twittertoken (.getOAuthAccessToken auth @oauthtoken (str pin))
        tokenmap {:token (. twittertoken getToken)
                  :tokenSecret (. twittertoken getTokenSecret)}]
     tokenmap))
