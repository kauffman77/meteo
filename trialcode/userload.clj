(in-ns 'user)
(def upath '("."))
(import '(java.io File))
	
(defn uload
  "Load files searching user specified directories in list upath"
  [file]
  (loop [paths upath]
    (if-let [path (first paths)]
      (let [fname  (str path "/" file)]
	(if (.exists (new File fname))
	  (load-file fname)
	  (recur (rest paths))))
      (println "Not found"))))