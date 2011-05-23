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
  