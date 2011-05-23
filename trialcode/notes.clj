(ns notes
  (:require (clojure [zip :as zip]
		     [xml :as xml])))


;; (defn note [len pitch]
;;   {:len len :pitch pitch})

(def lengths [:whole :half :quarter :eighth :sixteenth])
(def len-dur {:whole 1 :half 0.5 :quarter 0.25 :eighth 0.125 :sixteenth 0.0625})
(def dur-len (reduce (fn [m [k v]] (assoc m v k)) {} len-dur))
(def lengths (vec (keys len-dur)))
(def pitches '[a b c d e f g])

(defprotocol P (dur [this]))

(defrecord note [len pitch]
  P
  (dur [this] (len-dur len)))

(defn randnote []
  (note.
       (lengths (rand-int (count lengths)))
       (pitches (rand-int (count pitches)))))

(defn randmeasure []
  (loop [m '()
	 rem 1]
    (let [pitch (pitches (rand-int (count pitches)))
	  len (lengths (rand-int (count lengths)))
	  dur (len-dur len)]
;      (println "rem" rem "dur" dur "pitch" pitch (- rem dur))
      (cond (> dur rem) (recur m rem)	;Try again
	    (< 0 (- rem dur)) (recur (conj m (note. len pitch)) (- rem dur)) ;Add note
	    :else (conj m (note. len pitch))))))			     ;Finished

(defn randpart [n]
  (map (fn [_] (randmeasure)) (range n)))

(defn next-note [note]
  (if-let [nnote (zip/right note)]	;Move point forward on note
    nnote
    (if-let [nmeas (->> note zip/up zip/right)] ;End of measure, next measure
      (zip/down nmeas)
      note)))

(def z (zip/seq-zip p))
(def point (atom (->> z zip/down zip/down)))

(defn forward-note [pt]
  (dosync (swap! pt next-note)))

(defn beginning-of-part [pt]
  (dosync (swap! pt #(->> % zip/up zip/leftmost zip/down))))

(defn end-of-part [pt]
  (dosync (swap! pt #(->> % zip/up zip/rightmost zip/down))))
    
(defn dotest [n]
  (let [len (reduce + (map count p))]
    (time
     (dotimes [_ n]
       (beginning-of-part point)
       (dotimes [_ (rand-int len)] (forward-note point))))))

(defn breakme []
  (->> z zip/down zip/down zip/down zip/down zip/node))