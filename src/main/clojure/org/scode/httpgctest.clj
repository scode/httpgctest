(ns org.scode.httpgctest
  (:gen-class)
  (:require [clojure.contrib.duck-streams :as duck-streams])
  (:use compojure.core
        compojure.response
        ring.adapter.jetty)
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

(def data (ref #{}))

(defn make-random-data
  []
  (let [r (rand)]
    {:rand r
     :str (str r
               " a fairly long semi-unique string that is hopefully not very small in"
               " comparison to the set structure itself, thus making this test a bit"
               " more useful in testing gc behavior. this string plus the map we are"
               " returning should hopefully be enough")
     :frag-grendade (apply str r (repeat (* (rand) 25) "kaboom"))}))

(defn serve-gendata [request]
  (println request)
  (let [amount (let [amstr ((:query-params request) "amount")]
                 (if amstr
                   (Integer/parseInt amstr)
                   (max 1 (count @data))))]
    (dosync
     (alter data (fn [d] (apply conj d (map (fn [f] (f)) (repeat amount #(make-random-data)))))))
    {:status 200
     :headers {}
     :body (str "size is now " (count @data) " after adding " amount)}))

(defn serve-dropdata [request]
  (let [old-size (count @data)]
    (if (contains? (:query-params request) "ratio")
      (let [ratio (Double/parseDouble ((:query-params request) "ratio"))]
        (dosync (alter data (fn [old] (loop [data old
                                             tail (seq old)]
                                        (if (seq tail)
                                          (if (< (rand) ratio)
                                            (recur (disj data (first tail))
                                                   (rest tail))
                                            (recur data
                                                   (rest tail)))
                                          data))))))
      (dosync (alter data (fn [_] #{}))))
    {:status 200
     :headers {}
     :body (str "dropped " (- old-size (count @data)))}))

(defroutes our-routes
  (GET "/gengarbage" []
       serve-gengarbage)
  (GET "/gendata" []
       serve-gendata)
  (GET "/dropdata" []
       serve-dropdata))

(defn -main [& args]
  (run-jetty our-routes {:port 9191}))

