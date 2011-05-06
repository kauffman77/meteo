(defn radians [degrees] (. java.lang.Math toRadians degrees))

(defmacro rot [g2d angle & body]
  `(do (. ~g2d rotate (radians ~angle))
       (let [a# (do ~@body)]
	 (. ~g2d rotate (radians (- 0 ~angle)))
	 a#)))
(defmacro trans [g2d dx dy & body]
  `(do (. ~g2d translate (int ~dx) (int ~dy))
       (let [a# (do ~@body)]
	 (. ~g2d translate (int (- ~dx)) (int (- ~dy)))
	 a#)))

(def width 400)
(def height 400)

(defn draw_tree [^java.awt.Graphics2D g2d length depth]
  (if (> depth 0)
    (do
      (. g2d drawLine 0 0 length 0)
      (trans g2d (int length) 0
	  (rot g2d -30
	       (draw_tree g2d (* length 0.75) (- depth 1)))
	  (rot g2d 30
	       (draw_tree g2d (* length 0.75) (- depth 1)))))))

(defn draw [^java.awt.Graphics2D g2d]
  (doto g2d
    (. translate (int (/ width 2)) (int (/ height 2)))
    (. rotate (radians -90)))
  (time (draw_tree g2d 50 16)))

(def frame (new javax.swing.JFrame))
(def pan
     (proxy [javax.swing.JPanel] []
       (paintComponent [g2d] (draw g2d))))

(doto frame
  (.setContentPane pan)
  (.setTitle "Clojure is Ornery") 
  (.pack )
  (.setSize width height)
  (.show))
(println "\n\nStarting up" (.getTitle frame) "\n")
