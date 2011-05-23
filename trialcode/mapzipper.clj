(ns mapzipper
  (:require (clojure [zip :as zip]
		     [xml :as xml])))

(def x (xml/parse "/export/scratch/kauffman/meteo/trialcode/hello.xml"))
(def x (xml/parse "/export/scratch/kauffman/meteo/trialcode/xml/000206B_.xml"))

(def z (zip/xml-zip x))

(defn zip-top [z]
  (if-let [u (zip/up z)]
    (recur u)
    z))

;; (defn search-note [z]
;;   (if (= :note (:tag (zip/node z)))
;;     z
;;     (recur (zip/next z))))


(defmulti next-note #(:tag (zip/node %)))

;; Special case of a zipper already being a note
;; This is broken as it will pick up <forward> attributes too
(defmethod next-note :note [note]
  (if-let [nnote (zip/right note)]
    nnote
    (next-note (zip/next note))))

;; Default case, search for next hierarchical note
;; Circular: will restart at the beginning when the zipper is exhausted
(defmethod next-note :default [z]
  (let [node (zip/node z)]
    (if (and (map? node) (= :note (:tag node)))
      z	
      (recur (zip/next z)))))

(defmulti prev-note #(:tag (zip/node %)))

;; Special case of a zipper already being a note
(defmethod prev-note :note [note]
  (if-let [nnote (zip/left note)]
    nnote
    (prev-note (zip/prev note))))

;; Default case, search for next hierarchical note
(defmethod prev-note :default [z]
  (let [node (zip/node z)]
    (if (and (map? node) (= :note (:tag node)))
      z	
      (recur (zip/prev z)))))

(defn first-note [z]
  (next-note (zip-top z)))

