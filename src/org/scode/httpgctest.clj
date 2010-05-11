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
  (let [amount (let [amstr (:amount (:query-params request))]
                 (if amstr
                   (Integer/parseInt amstr)
                   (max 1 (count @data))))]
    (dosync
     (alter data #(concat %1 (repeat amount "data"))))
    {:status 200
     :headers {}
     :body (str "size is now " (count @data) " after adding " amount)}))

(defn serve-dropdata [request]
  (let [old-size (count @data)]
    (dosync (alter data (fn [d] {})))
    {:status 200
     :headers {}
     :body (str "dropped " old-size)}))

(defroutes greeter
  (GET "/gengarbage"
       serve-gengarbage)
  (GET "/gendata"
       serve-gendata)
  (GET "/dropdata"
       serve-dropdata))

(defn -main [& args]
  (run-server {:port 9191}
              "/*" (servlet greeter)))

