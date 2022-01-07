(ns com.code-intelligence.jazzer-clj.core-test
  (:require [clojure.test :refer :all]
            [com.code-intelligence.jazzer-clj.core :refer :all]))

(deftest test-deftarget
  (is (some? (macroexpand '(deftarget com.code-intelligence.jazzer-clj.core-test.Working [input]
                             (when (and (= 42 (.consumeInt input))
                                        (= "supersecret" (.consumeRemainingAsString)))
                               (throw (Exception. "You found the bug!"))))))
      "expanding a correct target shouldn't raise errors"))
