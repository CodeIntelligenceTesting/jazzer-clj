(ns com.code-intelligence.jazzer-clj.core
  (:import (com.code_intelligence.jazzer.api FuzzedDataProvider FuzzerSecurityIssueMedium)))

(defmacro deftarget [class-name [input-sym] & body]
  (let [prefix (str (gensym "fuzztarget") "-")]
    `(do (gen-class :name ~class-name
                      :prefix ~prefix
                      :methods [^:static [~'fuzzerTestOneInput [com.code_intelligence.jazzer.api.FuzzedDataProvider] ~'void]])
         (defn ~(symbol (str prefix "fuzzerTestOneInput")) [^com.code_intelligence.jazzer.api.FuzzedDataProvider ~input-sym]
           ~@body))))
