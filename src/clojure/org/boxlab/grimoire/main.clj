(ns org.boxlab.grimoire.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]
        [neko.application :only [defapplication]]
        [neko.data]
        [neko.notify]
        [org.boxlab.grimoire.commands]
        [org.boxlab.grimoire.data]
        [org.boxlab.grimoire.oauth])
  (:import (android.content Intent)
           (android.net Uri)))

(declare ^android.app.Activity a)

; メインレイアウト
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
                     [:button {:text "λ"
                               :on-click (fn [_] 
                                           (on-ui
                                             (toast
                                               (str
                                                 (try
                                                   (binding [*ns* (find-ns 'org.boxlab.grimoire.main)]
                                                     (load-string 
                                                       (get-elmt ::form)))
                                                   (catch Exception e (. e getMessage)))))))}]
                     [:button {:text "Post"
                               :on-click (fn [_] 
                                           (future 
                                             (post 
                                               (get-elmt ::form))))}]]])

; アカウント認証レイアウト
(def signup-layout [:linear-layout {:orientation :vertical
                                    :id-holder true
                                    :def `body}
                     [:linear-layout {:orientation :horizontal}
                       [:edit-text {:id ::form
                                    :hint "PIN"}]
                       [:button {:Text "Submit"
                                 :on-click (fn [_] 
                                             (do
                                               ; トークンの生成
                                               (future
                                                 (gen-tokens (get-elmt ::form)))
                                               ; twitterインスタンスの生成
                                               (future
                                                 (gen-twitter))
                                               ;(gen-twitterstream listener)
                                               ;(start)
                                               ; 画面変移
                                               (on-ui
                                                 (set-content-view! a
                                                   (make-ui main-layout)))))}]]
                     [:button {:Text "Open oauth signup page"
                               :on-click (fn [_]
                                           (on-ui
                                             (.startActivity a
                                               (Intent. Intent/ACTION_VIEW
                                                 (Uri/parse (. @oauthtoken getAuthorizationURL))))))}]
                     [:web-view {:id ::webview}]])
                    

(defactivity org.boxlab.grimoire.CoreActivity
  :def a
  :on-create
  (fn [this bundle]
    (do 
      ; oauthtokenを生成
      (future
        (get-oauthtoken!))
      ; tokenをsharedpreferenceに
      (reset! sp 
        (get-shared-preferences "default" :world-writeable))
      ; メイントークンを生成
      (reset! tokens
        (load-string (.. @sp (getString "tokens" ""))))
      ; 画面を変移
      (if @tokens
        (on-ui
          (set-content-view! a
            (make-ui main-layout)))
        (on-ui
          (set-content-view! a
            (make-ui signup-layout)))))))
