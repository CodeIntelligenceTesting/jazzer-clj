(ns com.code-intelligence.jazzer-clj.core
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str])
  (:import (com.code_intelligence.jazzer.api FuzzedDataProvider)))

(def fuzzer-test-one-input-signature
  (quote ^:static [fuzzerTestOneInput [com.code_intelligence.jazzer.api.FuzzedDataProvider] void]))

(defmacro deftarget
  "Define a Jazzer fuzz target.

    (deftarget com.example.MyTarget [input]
      (do-something (.consumeInt input)))

  The input parameter is of type
  `com.code_intelligence.jazzer.api.FuzzedDataProvider` and can be used in the
  body to obtain input data for the test."
  [class-name [input-sym] & body]
  (let [prefix (str (str/replace (name class-name) #"[\._]" "-") "-")]
    `(do (gen-class :name ~class-name
                    :prefix ~prefix
                    :methods [~fuzzer-test-one-input-signature])
         (defn ~(symbol (str prefix "fuzzerTestOneInput"))
           [~(with-meta input-sym {:tag 'com.code_intelligence.jazzer.api.FuzzedDataProvider})]
           ~@body))))

(defn- valid-class-name?
  "Return true if s is a valid Java class name."
  [s]
  (boolean (re-find #"^[A-Za-z0-9_\.]+$" (name s))))

(s/def ::deftarget-args (s/cat :class-name (s/and symbol? valid-class-name?)
                               :arg-var (s/and vector? (s/cat :input-sym simple-symbol?))
                               :body (s/* any?)))

(s/fdef deftarget :args ::deftarget-args)
