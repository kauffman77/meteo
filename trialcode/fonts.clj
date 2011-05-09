(import java.io.File java.awt.Font java.awt.RenderingHints)
(def font
     (let [f (Font/createFont Font/TRUETYPE_FONT (new File "emmentaler-16.otf"))]
      (.deriveFont f (float 200))))

(import
 '(javax.swing JFrame JPanel)
 '(java.awt Graphics2D))

(def width 400)
(def height 400)

(defn draw [g2d]
  (.setFont g2d font)
  (.setRenderingHint g2d
		     RenderingHints/KEY_TEXT_ANTIALIASING
		     RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
  (.drawString g2d "\ue1a9" 0 0)) ;;(/ width 2) (/ height 2) ))
  



(def frame (new javax.swing.JFrame))
(def pan
     (proxy [javax.swing.JPanel] []
       (paintComponent [g2d] (draw g2d))))

(doto frame
  (.setContentPane pan)
  (.setTitle "Clojure draws music") 
  (.pack )
  (.setSize width height)
  (.show))
