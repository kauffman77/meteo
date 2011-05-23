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
  (pitch [n])
  (dur [n])
  (abs-pitch [n]))

(defrecord ANote [pitch dur])
(def n (ANote. :C4 4))

(defmulti diff #([(class %1) (class %2)]))

(defmethod diff '[notes.ANote notes.ANote] [n m]
  (- (abs-pitch n) (abs-pitch m)))

