(import java.io.File
	'(java.awt Font RenderingHints )
	'(java.awt.geom Line2D$Double))

(set! *warn-on-reflection* true)
(def font
     (let [f (Font/createFont Font/TRUETYPE_FONT
			      (File. "/home/kauffman/devel/meteo/trialcode/emmentaler-16.otf"))]
      (.deriveFont f (float 100))))

(import
 '(javax.swing JFrame JPanel)
 '(java.awt Graphics2D))

(def width 400)
(def height 400)

(defn draw [^Graphics2D g2d]
  (let [frc (.getFontRenderContext ckg2d)
  	glyphvec (.createGlyphVector font frc "\ue1a9")
  	r2d (.getVisualBounds glyphvec)
  	glyphmet (.getGlyphMetrics glyphvec 0)
  	]
    (doto g2d
      (.setFont font)
      (.setRenderingHint RenderingHints/KEY_TEXT_ANTIALIASING
			 RenderingHints/VALUE_TEXT_ANTIALIAS_ON)
      (.translate (double (/ width 2)) (double (/ height 2)))
      (.draw (Line2D$Double. 0 0 0 -100))
      (.draw (Line2D$Double. 0 0 100 0))
      (.drawString "\ue136" 0 0)	;solid head
      ;(.drawString "\ue1af" 0 0)  ; common time
      ;(.drawString "\ue1a9" 0 0) ; treble
      ;(.drawString "\ue1a7" 0 0) ; bass
      ))) 

;(defn draw [g2d] '())



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

