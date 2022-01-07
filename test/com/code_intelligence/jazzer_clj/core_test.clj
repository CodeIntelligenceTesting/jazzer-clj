(ns com.code-intelligence.jazzer-clj.core-test
  (:require [clojure.test :refer :all]
            [com.code-intelligence.jazzer-clj.core :as fuzzing]
            [clojure.spec.alpha :as s]))

(deftest test-deftarget-args-spec
  (is (s/valid? ::fuzzing/deftarget-args
                '(com.code_intelligence.jazzer_clj.core_test.Working
                  [input]
                  (when (= "supersecret" (.consumeRemainingAsString input))
                    (throw (Exception. "You found the bug!"))))))
  (is (= [:class-name]
         (-> (s/explain-data ::fuzzing/deftarget-args '(com.code-intelligence.DashesAreIllegal [input]))
             ::s/problems
             first
             :path))
      "invalid Java class names as target names should be flagged"))
