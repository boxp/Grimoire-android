(ns org.boxlab.grimoire.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]
        [neko.application :only [defapplication]]
        [neko.data]
        [org.boxlab.grimoire.commands]
        [org.boxlab.grimoire.data]
        [org.boxlab.grimoire.oauth])
  (:import (android.content Intent)
           (android.net Uri)))

(declare ^android.app.Activity a)

(def main-layout [:linear-layout {:orientation :vertical
                                  :id-holder true
                                  :def `body}
                   [:linear-layout {:orientation :horizontal
                                    :id ::panes}
                     [:list-view {:id ::tllv}]]
                   [:linear-layout {:orientation :horizontal
                                    :id ::buttom}
                     [:edit-text {:id ::form
                                  :hint "How do you do?"}]
                     [:button {:text "Post"
                               :on-click (fn [_] (future (post (get-elmt ::form))))}]]])

(def signup-layout [:linear-layout {:orientation :vertical
                                    :id-holder true
                                    :def `body}
                     [:linear-layout {:orientation :horizontal}
                       [:edit-text {:id ::form
                                    :hint "PIN"}]
                       [:button {:Text "Submit"
                                 :on-click (fn [_] 
                                             (do
                                               (await
                                                 (future
                                                   (do
                                                     (gen-tokens (get-elmt ::form))
                                                     (gen-twitter))))
                                               ;(gen-twitterstream listener)
                                               ;(start)
                                               (on-ui
                                                 (set-content-view! a
                                                   (make-ui main-layout)))))}]]
                     [:button {:Text "Open oauth signup page"
                               :on-click (fn [_]
                                           (future
                                             (.loadUrl (take-elmt ::webview)
                                               (. @oauthtoken getAuthorizationURL))))}]
                     [:web-view {:id ::webview}]])
                    

(defactivity org.boxlab.grimoire.CoreActivity
  :def a
  :on-create
  (fn [this bundle]
    (do 
      (future
        (get-oauthtoken!))
      ; token„Çísharedpreference„Å´
      (reset! sp 
        (get-shared-preferences "default" :world-writeable))
      ; ÉgÅ[ÉNÉìÇì«Ç›çûÇ›
      (reset! tokens
        (load-string (.. @sp (getString "tokens" ""))))
      (if @tokens
        (on-ui
          (set-content-view! a
            (make-ui main-layout)))
        (do
          (on-ui
            (set-content-view! a
              (make-ui signup-layout))))))))
