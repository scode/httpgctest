(ns org.scode.httpgctest
  (:gen-class)
  (:require [clojure.contrib.duck-streams :as duck-streams])
  (:use [compojure]
	        [compojure.http response])
  (:import (java.io ByteArrayOutputStream
                    ByteArrayInputStream
                    FileInputStream
                    File)))

(defn serve-gengarbage [request]
  (do
    (Thread/sleep (rand 1000))
    {:status 200
     :headers {}
     :body "Garbage all around."}))

(defroutes greeter
  (GET "/gengarbage"
       serve-gengarbage))

(defn -main [& args]
  (run-server {:port 9191}
              "/*" (servlet greeter)))

