(defproject grimoire-android/grimoire-android "0.0.1-SNAPSHOT"
  :description "FIXME: Android project description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"

  :warn-on-reflection true

  :source-paths ["src/clojure" "src"]
  :java-source-paths ["src/java" "gen"]

  :dependencies [[org.clojure-android/clojure "1.5.1-jb"]
                 [commons-logging/commons-logging "1.1.1"]
                 [org.twitter4j/twitter4j-core"[3.0,)"]
                 [org.twitter4j/twitter4j-stream"[3.0,)"]
                 [org.twitter4j/twitter4j-async"[3.0,)"]
                 [neko/neko "3.0.0-beta6"]]
  :profiles {:dev {:dependencies [[android/tools.nrepl "0.2.0-bigstack"]
                                  [compliment "0.0.2"]]
                   :android {:aot :all-with-unused}}
             :plugins [[lein-droid "0.2.0-beta3"]]
             :release {:android
                       {;; Specify the path to your private keystore
                        ;; and the the alias of the key you want to
                        ;; sign APKs with. Do it either here or in
                        ;; ~/.lein/profiles.clj
                        ;; :keystore-path "/home/user/.android/private.keystore"
                        ;; :key-alias "mykeyalias"
                        :aot :all}}}

  :android {;; Specify the path to the Android SDK directory either
            ;; here or in your ~/.lein/profiles.clj file.
            ;; :sdk-path "/home/user/path/to/android-sdk/"
            ;; Uncomment this if dexer fails with
            ;; OutOfMemoryException. Set the value according to your
            ;; available RAM.
            :dex-opts ["-JXmx4096M"]

            ;; If previous option didn't work, uncomment this as well.
            ;; :force-dex-optimize true

            :target-version "15"
            :aot-exclude-ns ["clojure.parallel" "clojure.core.reducers"]})
