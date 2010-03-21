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
    (doseq [future (doall (for [i (range 10)] (future (gen-recursive-garbage 50 []))))]
      @future)
    {:status 200
     :headers {}
     :body "Garbage all around."}))

(def data (ref ["data"]))

(defn serve-gendata [request]
  (dosync
   (alter data #(concat %1 (repeat (count %1) "data"))))
  {:status 200
   :headers {}
   :body (str "size is now " (count @data))})

(defroutes greeter
  (GET "/gengarbage"
       serve-gengarbage)
  (GET "/gendata"
       serve-gendata))

(defn -main [& args]
  (run-server {:port 9191}
              "/*" (servlet greeter)))

