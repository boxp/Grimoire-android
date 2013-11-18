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
                (.setOAuthConsumer consumerKey,consumerSecret))]
    (reset! oauthtoken
      (.. auth getOAuthRequestToken))))

(defn gen-tokens [pin] 
  (let [conf (ConfigurationContext/getInstance) 
        auth (doto (OAuthAuthorization. conf) 
                (.setOAuthConsumer consumerKey,consumerSecret))]
       (do  
         (let 
           [twitterTokens (.getOAuthAccessToken auth @oauthtoken pin)
            etoken (. @tokens edit)]
           (do
             ; tokenの書き込み
             (assoc! etoken
                :token (.getToken twitterTokens))
             (assoc! etoken
                :tokenSecret (.getTokenSecret twitterTokens))
             (. etoken commit))))))

(defn gen-twitter []
  (let [twitterins (try 
                     (doto (.getInstance (TwitterFactory.))
                       (.setOAuthConsumer consumerKey,consumerSecret)
                       (.setOAuthAccessToken 
                         (AccessToken. 
                           (.getString tokens "token" "") 
                           (.getString tokens "tokenSecret" ""))))
                     (catch Exception e (println e)))
        screen-name-key (keyword (. twitterins getScreenName))]
    (do
      (reset! twitter twitterins)
      (dosync 
        (alter twitters merge
          {screen-name-key twitterins})))))
