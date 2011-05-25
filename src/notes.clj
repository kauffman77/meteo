(ns meteo
  (:require [swank.core])
  (:use [clojure.contrib.def])
  (:import [clojure.lang Keyword Symbol IPersistentList IPersistentMap])
  (:import java.lang.String))

(defprotocol Pitch
  "Things you can do to a note."
  (keyw [n] "Pitch in keyword form (e.g. :Db7)")
  (pname [n] "Name of the note as a keyword (e.g. :A, :B, ..)")
  (accidental [n] "Accidental :# or :b or nil")
  (octave [n] "Integer octave of the note"))
  
(defrecord SimplePitch [pit pnm acc oct]
  Pitch
  (keyw [_] pit)
  (pname [_] pnm)
  (accidental [_] acc)
  (octave [_] oct))


(defn to-pitches
  "Create a pitch. Takes a keyword, symbol, string, or a list of
numeric arguments and produces a record adhering to the Note
protocol."
  ([pit & pits] (map to-pitches (conj pits pit)))
  ([pit]
     (let [sp (name pit)
           [pit pnm acc oct] (re-matches #"([A-Ga-g])([b#]?)(-?[0-9]+)" sp)
           acc (if (.isEmpty acc) nil acc)]
;       (swank.core/break)
       (SimplePitch. (keyword pit) 
                   (keyword (.toUpperCase pnm))
                   (keyword acc)
                   (Integer/parseInt oct)))))

;; Extend fundamental types to Note protocol
(extend-protocol Pitch
  String
  (keyw [n] (keyw (to-pitches n)))
  (pname [n] (pname (to-pitches n)))
  (accidental [n] (accidental (to-pitches n)))
  (octave [n] (octave (to-pitches n)))

  Keyword
  (keyw [n] (keyw (to-pitches n)))
  (pname [n] (pname (to-pitches n)))
  (accidental [n] (accidental (to-pitches n)))
  (octave [n] (octave (to-pitches n)))

  Symbol
  (keyw [n] (keyw (to-pitches n)))
  (pname [n] (pname (to-pitches n)))
  (accidental [n] (accidental (to-pitches n)))
  (octave [n] (octave (to-pitches n))))
  
(defvar pitch-value (zipmap [:C :D :E :F :G :A :B] (range 8))
  "Numeric values of the pitches, use it like a function")

(defn diff 
  "Determine the display height difference between two pitches.
Arguments must adhere to the Pitch protocol.  The difference is in
standard units which are half the distance between to staff lines.
Two standard units moves up one staff line."
    [p1 p2]
    (let [v1 (+ (* 7 (octave p1)) (pitch-value (pname p1)))
          v2 (+ (* 7 (octave p2)) (pitch-value (pname p2)))]
      (- v1 v2 )))

;; Set up note durations
(defvar common-dur
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
   :hundred-twenty-eighth 1/128 :hundred-twenty-eighth. (+ 1/128 1/256) }
  "Map of durations in common (4/4) time." )

(defprotocol Notes
  "Things you can do to a group of notes, all some duration.  A Notes
is composed of one or more pitches of the same duration.  If no
pitches are present, the Notes represents a rest."
  (pitches [n] "Gives a sequence of objects implementing the Pitch protocol")
  (dur [n] "Duration of notes as a keyword or nil (e.g. :eighth, :quarter...)" )
  (width [n] [n time-sig] "Fraction of a measure notes last time-sig, defaults to 4/4 time.")
  (top-pitch [n] "Return the highest Pitch of the pitches in the Notes"))

(defrecord SimpleNotes [d pits]
  Notes
  (pitches [_] pits)
  (dur [_] d)
  (width [this] (width this 'C))
  (width [this time-sig] 
    (let [cdur (common-dur d)
          [num den] (condp = time-sig
                      'C '(4 4)      ;Common time
                      :C '(4 4)
                      'Cut '(2 2)    ;Cut time
                      :Cut '(2 2)
                      time-sig)]
      (/ (* cdur den) num)))
  (top-pitch [this] (sort #(- (diff %1 %2)) pits)))

(defn to-notes
  "Create SimpleNotes with one or more pitches"
  ;; (fn [_ n & _] (
  ([dur]
     (if (dur common-dur)
       (SimpleNotes. dur '())
       (throw (Exception. (str "Bad note duration " dur)))))
  ([dur n1 & ns]
     (if (dur common-dur)
       (SimpleNotes. dur (apply to-pitches (conj ns n1)))
       (throw (Exception. (str "Bad note duration " dur))))))
  
;; (defn notes-width
;;   "Fraction of a measure note with dur takes in time-sig.  Returns a
;; ratio, convert to double with .doubleValue if needed.  If no
;; time-signature is given, use 4/4 time as default."
;;   ([dur] (notes-width dur 'C))
;;   ([dur time-sig]
;;      (let [cdur (common-dur dur)
;;            [num den] (condp = time-sig
;;                        'C '(4 4)      ;Common time
;;                        :C '(4 4)
;;                        'Cut '(2 2)    ;Cut time
;;                        :Cut '(2 2)
;;                        time-sig)]
;;        (/ (* cdur den) num))))

(defprotocol Voice
  "The things you can do to a voice."
  (notes [v] "Get a sequence of notes (note groups) from the voice"))

(defprotocol Part
  "Things to do with a part."
  (voices [p] "Get a sequence of the voices in the part"))

(defprotocol Measure
  "Things to do to measures."
  (parts [m] "Get a sequence of parts in this measure, not necessarily in order")
  (part [m p] "Get part named p"))


(defprotocol Mectangle
  (mmeasures [m] "Get a measure sequence in a mectangle")
  (mheigh [m] "How many stacked parts are in the mectangle"))

;; Extend protocols to map types
(extend-type IPersistentMap
  Measure
  (parts [m] (seq (:parts m)))
  (part [m p] (p (:parts m)))
  Part
  (voices [p] (:voices p))
  Voice
  (notes [v] (:notes v))
  ;;This may not be quite right, problems with ordering of parts once
  ;;cut
  Mectangle                             
  (mmeasures [m] (seq (:mmeasures m)))
  (mheight [m] (:height m))
  )
