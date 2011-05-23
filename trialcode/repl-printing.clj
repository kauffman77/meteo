;; Demonstration from Joy of how to add custom printing of objects on
;; the REPL.

(defmethod print-method clojure.lang.PersistentQueue
  [q, w]
  (print-method '<- w) (print-method (seq q) w) (print-method '-< w))
clojure.lang.PersistentQueue/EMPTY
					;=> <-nil-<
(def schedule
     (conj clojure.lang.PersistentQueue/EMPTY
	   :wake-up :shower :brush-teeth))
