(defproject com.code-intelligence/jazzer-clj "0.1.0-SNAPSHOT"
  :description "Clojure interface for Jazzer"
  :url "https://github.com/CodeIntelligenceTesting/jazzer-clj"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.code-intelligence/jazzer-api "0.10.0"]]
  :repl-options {:init-ns com.code-intelligence.jazzer-clj.core})
