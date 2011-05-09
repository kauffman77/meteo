(import
 '(javax.swing JFrame JPanel)
 '(java.awt Graphics2D))

(def width 400)
(def height 400)
(def length 5)
(def n 10000)

(defn radians [degrees] (. java.lang.Math toRadians degrees))
(defn randang [] (radians (rand 360)))
  

(defn draw [^Graphics2D g2d]
  (.translate g2d (int (/ width 2)) (int (/ height 2)))
  (dotimes [i n]
    (doto g2d
      (.rotate (randang))
      (.drawLine length 0 0 0)
      (.translate length 0))))

(def frame (new JFrame))
(def panel
     (proxy [JPanel] []
       (paintComponent [g2d]  (time (draw g2d)))))

(doto frame
  (.setContentPane panel)
  (.setTitle "Clojure is Ornery") 
  (.pack )
  (.setSize width height)
  (.show))  
(println "\n\nStarting up" (.getTitle frame) "\n")
