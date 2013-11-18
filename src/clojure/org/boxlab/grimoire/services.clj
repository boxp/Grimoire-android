(ns org.boxlab.grimoire.services
  (:use [org.boxlab.grimoire.data]
        [org.boxlab.grimoire.oauth]))

(def twitterstream (atom nil))
