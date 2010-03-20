(ns org.scode.httpgctest
  (:gen-class)
  (:require [clojure.contrib.duck-streams :as duck-streams])
  (:use [compojure]
	        [compojure.http response])
  (:import (java.io ByteArrayOutputStream
                    ByteArrayInputStream
                    FileInputStream
                    File)))

(defn gen-recursive-garbage [lvl garbage]
  (if (> 0 lvl)
    (gen-recursive-garbage (dec lvl) {:some-nice-garbage [1 2 3 4 5 6 7 8 9 10]})
    (do
      (Thread/sleep (rand 1000))
      garbage)))

(defn serve-gengarbage [request]
  (do
    (gen-recursive-garbage 50 [])
    {:status 200
     :headers {}
     :body "Garbage all around."}))

(defroutes greeter
  (GET "/gengarbage"
       serve-gengarbage))

(defn -main [& args]
  (run-server {:port 9191}
              "/*" (servlet greeter)))

