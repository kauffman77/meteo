(ns notes)

(defprotocol Measures
  (notes [m]))

(defrecord Measure [notes]
  Measures
  (notes [this] (:notes this)))

(defprotocol MRect
  (measures [m]))

(extend-type Measure
  MRect
  (measures [m] (:notes m))) 

(defprotocol Note
  "Things you can do to a note."
  (pitch [n] "Pitch in keyword form")
  (dur [n] "Duration (e.g. eigth, quarter...)" )
  (nname [n] "Name of the note (e.g. A, B, ..)")
  (accidental [n])
  (octave [n])
  (nao [n])
  (width [n time-sig] "Fraction of a measure this note lasts in time-sig"))

;; Set up note durations
(let [common-dur
      {nil 0
       :longa 4 :longa. 6
       :double-whole 2 :double-whole. 3
       :whole 1 :whole. 3/2
       :half 1/2 :half. (+ 1/2 1/8)
       :quarter 1/4 :quarter. (+ 1/4 1/8)
       :eighth 1/8 :eighth. (+ 1/8 1/16)
       :sixteenth 1/16 :sixteenth. (+ 1/16 1/32)
       :thirty-second 1/32 :thirty-second. (+ 1/32 1/64)
       :sixty-fourth 1/64 :sixty-fourth. (+ 1/64 1/128)
       :hundred-twenty-eighth 1/128 :hundred-twenty-eighth. (+ 1/128 1/256) }]
  (defn note-width
    "Fraction of a measure note with dur takes in time-sig.  Returns a
ratio, convert to double with .doubleValue if needed.  If no
time-signature is given, use 4/4 time as default."
    ([dur] (note-width dur 'C))
    ([dur time-sig]
       (let [cdur (common-dur dur)
             [num den] (condp = time-sig
                         'C '(4 4)
                         :C '(4 4)
                         'Cut '(2 2)
                         :Cut '(2 2)
                         time-sig)]
         (/ (* cdur den) num)))))
  
(defrecord ANote [pitch dur nname accidental octave]
  Note
  (pitch [this] (:pitch this))
  (dur [this] (:dur this))
  (nname [this] (:nname this))
  (accidental [this] (:accidental this))
  (octave [this] (:octave this))
  (nao [this] (:pitch this))
  (width [this time-sig] (note-width (:dur this) time-sig )))

(defn note
  "Create a note.  This is a multifunction which takes a keyword,
  symbol, string, or a list of numeric arguments and produces a record
  adhering to the Note protocol."
  ([pitch] (note pitch nil))
  ([pitch dur]
     (let [spitch (name pitch)
           [pitch nname accidental octave] (re-matches
                                            #"([A-Ga-g])([b#]?)(-?[0-9]+)" spitch)
           accidental (if (.isEmpty accidental) nil accidental)]
;       (swank.core/break)
       (note pitch dur nname accidental octave)))
  ([pitch nname accidental octave]
     (note pitch nil nname accidental octave))
  ([pitch dur nname accidental octave]
     (ANote. (keyword pitch) dur
             (keyword (.toUpperCase nname))
             (keyword accidental)
             (Integer/parseInt octave))))

;; Operate on strings as notes
(extend-type java.lang.String
  Note
  (pitch [n] (pitch (note n)))
  (dur [n] nil)
  (nname [n] (nname (note n)))
  (accidental [n] (accidental (note n)))
  (octave [n] (octave (note n)))
  (nao [n] (nao (note n)))
  (width [n] 0))

(extend-type clojure.lang.Keyword
  Note
  (pitch [n] (pitch (note n)))
  (dur [n] nil)
  (nname [n] (nname (note n)))
  (accidental [n] (accidental (note n)))
  (octave [n] (octave (note n)))
  (nao [n] (nao (note n)))
  (width [n] 0))

(extend-type clojure.lang.Symbol
  Note
  (pitch [n] (pitch (note n)))
  (dur [n] nil)
  (nname [n] (nname (note n)))
  (accidental [n] (accidental (note n)))
  (octave [n] (octave (note n)))
  (nao [n] (nao (note n)))
  (width [n] 0)) 
  

(let [nname-val (zipmap [:C :D :E :F :G :A :B] (range 8))]
  (defn diff 
    "Determine the display height difference between two notes.
Arguments must adhere to the Note protocol.  The difference is in
standard units which are half the distance between to staff lines.
Two standard units moves up one staff line."
    [n1 n2]
    (let [v1 (+ (* 8 (octave n1)) (nname-val (nname n1)))
          v2 (+ (* 8 (octave n2)) (nname-val (nname n2)))]
      (- v1 v2 1))))

;; (defmulti note
;;   "Create a note.  This is a multifunction which takes a keyword,
;;   symbol, string, or a list of numeric arguments and produces a record
;;   adhering to the Note protocol."
;;   (fn [x & _] (class x)))

;; (defmethod note clojure.lang.Symbol
;;   ([pitch] (note (name x)))
;;   ([pitch dur] (note (name x) dur)))

;; (defmethod note clojure.lang.Keyword
;;   ([pitch] (note (name x)))
;;   ([pitch dur] (note (name x) dur)))




(def n (ANote. :C4 4))

(defmulti diff #(class %1))

(defmulti diff #([(class %1) (class %2)]))

(

(declare abs-pitch)


(defmethod diff '[notes.ANote notes.ANote] [n m]
  

