(ns org.boxlab.grimoire.data
  (:use [neko.ui.mapping]))

; android ui
(declare ^android.widget.LinearLayout body)

(defelement :web-view
  :classname android.webkit.WebView
  :inherits :view)

; twitter
(def tokens
  (atom nil))
(def oauthtoken
  (atom nil))
(def tweets 
  "Received status vector"
  (ref [])) 
(def tweet-maps 
  "Node -> status map"
  (ref {}))
(def mentions
  "Received mentions (deplicated)"
  (ref []))
(def friends 
  "Received friends list (deplicated)"
  (ref #{}))
(def myname
  "My twitter screen name"
  (atom nil))
(def imagemap
  "Profile images map"
  (ref {}))
(def twitter
  "Main twitter instance"
  (atom nil))
(def twitters
  "Sub twitter instances"
  (ref {}))

; plugin
(def plugins 
  "プラグインが収納される集合"
  (ref #{}))

(defprotocol Plugin
  "Grimoireのプラグインを示すプロトコル，プラグインを作るにはreify,proxyを用いて継承し，pluginsに追加して下さい．" 
  (get-name [this])
  (on-status [this status])
  (on-rt [this status])
  (on-unrt [this status])
  (on-fav [this source target status])
  (on-unfav [this source target status])
  (on-del [this status])
  (on-follow [this source user])
  (on-dm [this dm])
  (on-start [this])
  (on-click [this e]))

; system
(def max-nodes 
  "Var max node (deplicated)"
  (atom 100))
(def tweets-size 
  "Var tweets text size"
  (atom 13))
(def nervous
  "Use dialog warning"
  (atom false))
(def sp 
  (atom nil))
