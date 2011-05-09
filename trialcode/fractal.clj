(import
 '(javax.swing JFrame JPanel)
 '(java.awt Graphics2D))

(def wof *warn-on-reflection* )
(set! *warn-on-reflection* true)

(defn radians [degrees] (. java.lang.Math toRadians degrees))

(defmacro rot [^Graphics2D g2d angle & body]
  `(do (. ~g2d rotate (radians ~angle))
       (let [a# (do ~@body)]
	 (. ~g2d rotate (radians (- 0 ~angle)))
	 a#)))
(defmacro trans [^Graphics2D g2d dx dy & body]
  `(do (. ~g2d translate ~dx ~dy)
       (let [a# (do ~@body)]
	 (. ~g2d translate  (int (- 0 ~dx)) (int (- 0 ~dy)))
	 a#)))
					; -- API END --


					; -- CLIENT CODE START --
(def width 400)
(def height 400)


(defn draw_tree [^Graphics2D g2d length depth]
  (if (> depth 0)
    (do
      (. g2d drawLine 0 0 length 0)
      (trans g2d (int length) 0
	     (rot g2d -30
		  (draw_tree g2d (* length 0.75) (- depth 1)))
	     (rot g2d 30
		  (draw_tree g2d (* length 0.75) (- depth 1)))))))


;; (defn draw_tree [^Graphics2D g2d length depth]
;;   (if (> depth 0)
;;     (let [z (int 0)
;; 	  len (int length)
;; 	  r1 (double (radians -30))
;; 	  r2 (double (radians 30))]

;;       (.drawLine g2d 0 0 length 0)
;;       (.translate g2d len z)

;;       (.rotate g2d r1)
;;       (draw_tree g2d (* length 0.75) (- depth 1))
;;       (.rotate g2d r2)

;;       (.rotate g2d r2)
;;       (draw_tree g2d (* length 0.75) (- depth 1))
;;       (.rotate g2d r1)

;;       (.translate g2d (- len) z))))

(defn draw [^Graphics2D g2d]
  (doto g2d
    (. translate (int (/ width 2)) (int (/ height 2)))
    (. rotate (radians -90)))
  (time (draw_tree g2d 50 13)))
; -- CLIENT CODE END --


; -- API START --  
(def frame (new JFrame))
(def display
     (proxy [JPanel] []
       (paintComponent [g2d]  (draw g2d))))

;; (.setDefaultCloseOperation frame
;;   WindowConstants/EXIT_ON_CLOSE)
(doto frame
  (.setContentPane display)
  (.setTitle "Clojure is Ornery") 
  (.pack )
  (.setSize width height)
  (.show)
  
  (println "\n\nStarting up" (.getTitle frame) "\n"))
					; -- API END --
(set! *warn-on-reflection* wof)
(ns-unmap 'user 'wof)