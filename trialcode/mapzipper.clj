(ns mapzipper
  (:require (clojure [zip :as zip]
		     [xml :as xml])))

;; (def x (xml/parse "/export/scratch/kauffman/meteo/trialcode/hello.xml"))
;; (def x (xml/parse "/export/scratch/kauffman/meteo/trialcode/xml/000206B_.xml"))

(def x (xml/parse "/home/kauffman/meteo/trialcode/xml/000206B_.xml"))
;; (def x (xml/parse "/home/kauffman/meteo/trialcode/xml/x.xml"))

(def z (zip/xml-zip x))

(defn zip-top [z]
  (if-let [u (zip/up z)]
    (recur u)
    z))

;; Specific to this file
(def lastnote (-> z zip/down zip/right zip/right zip/down zip/rightmost zip/down))


(defmulti next-note #(:tag (zip/node %)))
(defmulti prev-note #(:tag (zip/node %)))
  
;; Special case of a zipper already being a note
;; This is broken as it will pick up <forward> attributes too
(defmethod next-note :note [note]
  (if-let [nnote (zip/right note)]
    nnote
    (next-note (zip/next note))))

;; Default case, search for next hierarchical note
(defmethod next-note :default [z]
  (if (zip/end? z)
    nil
    (let [node (zip/node z)]
      (if (and (map? node) (= :note (:tag node)))
	z	
	(recur (zip/next z))))))

;; Special case of a zipper already being a note
(defmethod prev-note :note [note]
  (if-let [nnote (zip/left note)]
    nnote
    (prev-note (zip/prev note))))

;; Default case, search for next hierarchical note
(defmethod prev-note :default [z]
  (if (nil? (zip/prev z))
    nil
    (let [node (zip/node z)]
      (if (and (map? node) (= :note (:tag node)))
	z	
	(recur (zip/prev z))))))

(defn notevec [nt vc lim]
  (if (and nt (< 0 lim))
    (recur (next-note nt) (conj vc nt) (dec lim))
    vc))
(defn measureevec [nt vc lim]
  (if (and nt (< 0 lim))
    (recur (next-note nt) (conj vc nt) (dec lim))
    vc))

(def nv (notevec (next-note z) [] 10000))

(def point (ref z)) 

(defn forward-note [pt]
  (dosync (alter pt next-note)))

(defn beginning-of-part [pt]
  (dosync (alter pt zip-top)))

;; (defn dotest [n]
;;     (time
;;      (dotimes [_ n]
;;        (beginning-of-part point)
;;        (dotimes [_ (rand-int len)] (forward-note point)))))



;; (defn first-note [z]
;;   (next-note (zip-top z)))

;; (defn forward-note