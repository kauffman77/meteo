;; Playing with global/local scope and trampoline

(defn ckfun [x] (> x 0))

(defn redef []
  (defn ckfun [x] (< x 0)))


(defn fib [n]
  (declare help)
  (let [help
	(fn [i n1 n2]
	  (if (= i n)
	    (+ n1 n2)
	    #(help (inc i) (+ n1 n2) n1)))]
  (trampoline help 0 1 0)))

;; (defn fib2 [n]
;;   (defn help [i n1 n2]
;;     (if (= i n)
;;       (+ n1 n2)
;;       (help (inc i) (+ n1 n2) n1)))
;;   (help 0 1 0))

