# Fuzzing Clojure code with [Jazzer](https://github.com/CodeIntelligenceTesting/jazzer)

The goal of `jazzer-clj` is to provide an idiomatic way to test Clojure software
with the JVM fuzzer [Jazzer](https://github.com/CodeIntelligenceTesting/jazzer).
See
[jazzer-clojure-example](https://github.com/CodeIntelligenceTesting/jazzer-clojure-example)
for an example project using it, or follow the instructions below to set it up
with your own code.

## Usage

In order to test your code, Jazzer requires you to write so-called "fuzz
targets" for it. They're comparable to unit tests, but with one important
difference: fuzz targets need to use input data that they receive from the
fuzzer to exercise the code under test.

For a concrete example, let's assume you have a function `do-something` that
takes an integer and a string argument. In order to test it with Jazzer, define
a fuzz target like so:

``` clojure
;; Anywhere in your code, as long as the namespace is AOT-compiled (see below)
(ns your-company.your-project.somewhere
  (:require [com.code-intelligence.jazzer-clj.core :as fuzzing]))

;; The function under test can be defined in this namespace or another; let's
;; assume it's defined like this:
(defn do-something
  "Not a very useful piece of code."
  [x s]
  (when (and (= 42 x)
             (= "supersecret" s))
    (throw (Exception. "You found the bug!"))))

;; Now we define the actual test:
(fuzzing/deftarget your.company.fuzzing.DoSomethingTarget [input]
  (do-something (.consumeInt input) (.consumeRemainingAsString input)))
```

Internally, this defines a Java class with the interface required by Jazzer
(expand the macro if you're curious!). Think of the `input` parameter as a
source of test data; see the
[javadoc](https://codeintelligencetesting.github.io/jazzer-api/com/code_intelligence/jazzer/api/FuzzedDataProvider.html)
for details.

Now you can build a JAR from your project as usual (e.g., using `lein uberjar`).
What's important is to make sure that all namespaces containing fuzz targets
(i.e., `your-company.your-project.somewhere` in our example) are AOT-compiled:
Jazzer requires the class that you define with `deftarget` to exist when the
code is loaded. In Leiningen, for example, you achieve this by putting the
following into your `project.clj`:

``` clojure
(defproject ...
  :profiles {:uberjar {:aot :all}})
```

Assuming you've produced the JAR in `target/your-project.jar`, you can run
Jazzer using the Docker image that the project provides:

``` shell
docker run -v $PWD:/fuzzing cifuzz/jazzer                       \
       --cp=/fuzzing/target/your-project.jar                    \
       --target-class=your.company.fuzzing.DoSomethingTarget    \
       /fuzzing/corpus-do-something
```

Note how the command tells Jazzer the class name of the target that you've
defined with `deftarget`. The last argument is optional but recommended; it
tells Jazzer to write interesting program inputs to the specified directory, so
that you can resume fuzzing at a later time or run multiple fuzzers in parallel
and have them share their knowledge.

If everything is set up correctly, Jazzer should print some notes about the code
that it's instrumenting, and then start fuzzing your function:

``` text
INFO: Loaded 8 hooks from com.code_intelligence.jazzer.sanitizers.Deserialization
INFO: Loaded 3 hooks from com.code_intelligence.jazzer.sanitizers.ExpressionLanguageInjection
INFO: Loaded 8 hooks from com.code_intelligence.jazzer.sanitizers.NamingContextLookup
INFO: Loaded 1 hooks from com.code_intelligence.jazzer.sanitizers.ReflectiveCall
INFO: Instrumented jazzer_clojure.targets.JsonistaExample (took 136 ms, size +90%)
INFO: New number of inline 8-bit counters: 1024
INFO: Instrumented clojure.lang.Var (took 67 ms, size +53%)
[...]
#1024	pulse  cov: 28666 ft: 31372 corp: 687/8801b exec/s: 512 rss: 500Mb
#2048	pulse  cov: 28703 ft: 33189 corp: 944/107Kb exec/s: 512 rss: 501Mb
#2262	INITED cov: 28706 ft: 33411 corp: 981/209Kb exec/s: 565 rss: 501Mb
```

The fuzzer will stop if it finds a crash and print details of the discovered
issue.

## License

Copyright © 2022 Code Intelligence GmbH

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

<p align="center">
<a href="https://www.code-intelligence.com"><img src="https://www.code-intelligence.com/hubfs/Logos/CI%20Logos/CI_Header_GitHub_quer.jpeg" height=50px alt="Code Intelligence logo"></a>
</p>
