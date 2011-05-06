(defn radians [degrees] (. java.lang.Math toRadians degrees))

(def cc90 (radians 90))
(def cw90 (radians -90))

(defn draw [^java.awt.Graphics2D g2d]
  (let [n 10000
	len 3]
    (dotimes [i n]
      (doto g2d
	(.drawLine 0 0 len 0)
	(.translate len 0)
	(.rotate cc90)
	(.drawLine 0 0 len 0)
	(.translate len 0)
	(.rotate cw90)))))

(def width 800)
(def height 800)

(def frame (new javax.swing.JFrame))
(def pan (proxy [javax.swing.JPanel] []
  (paint [g2d]
    (time (draw g2d))
  )
))

(doto frame
  (.setTitle "Clojure")
  (.setContentPane pan)
  (.pack)
  (.setSize width height)
  (.show))

