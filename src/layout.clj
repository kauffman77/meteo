(ns meteo
  (:require [swank.core])
  (:use [clojure.contrib.def]))

(defn get-glyph [o] o)



(defn add-heads
  "Add note heads "
  [context notes]
  (let [pits (pitches notes)]
    (if (seq pits)
      ;; Have pitches
      (let [glyph (get-glyph (dur notes))
            top (:topline context)        ;Pitch at top line
            make #(with-meta %
                    (assoc (meta %) :head
                           {:glyph glyph
                            :ypos (diff top %) ;Move down to note
                            :xpos '(:parent :xpos)}))]
        (assoc notes :pitches (map make pits)))
      ;; Have a rest
      (let [glyph (get-glyph (keyword (str (dur notes) "rest")))
            top (:topline context)]        ;Pitch at top line
        (assoc notes :pitches
               (with-meta pits
                 (assoc (meta pits) :head
                   {:glyph glyph
                    :ypos 5             ;May need to adjust on rest type
                    :xpos '(:parent xpos)})))))))

(defvar dot-dist 1
  "Preferred distance from edge of bounding box of note heads")

(defvar dotted-dur?
  (set (filter #(re-matches #".*\.$" (str %)) (keys common-dur)))
  "Durations that are dotted. A set but use it like a function.")

(defn add-dots
  "Return rhythmic dots associated with the notes if needed. Must be
  run after note head are added."
  [context notes]
  (if (dotted-dur? (dur notes))
    (let [pits (pitches notes)
          dist (or (:dot-dist notes) dot-dist)
          glyph (get-glyph :rhythmic-dot)
          make #(with-meta %            ;Associate meta data for dots
                  (assoc (meta %) :dot
                         {:glyph glyph
                          :ypos '(:parent :ypos)
                          :xpos '(+ (:head :xpos) (:head :glyph :width))}))]
      (assoc notes :pitches (map make pits)))
    notes))

;; (defn note-layout
;;   "Determine the layout objects and constraints associated with Notes."
;;   [notes]
  
  