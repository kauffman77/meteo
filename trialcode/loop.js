timer=timer=require("ringo/utils/debug").timer;
radians = java.lang.Math.toRadians;

var cc90 = radians(90);
var cw90 = radians(-90);

function draw(g2d){
  var n = 10000, len = 3;
  print("n: " + n + " len: " + len);
  for(var i=0;i<n;i++){
    g2d.drawLine(0, 0, len, 0);
    g2d.translate(len,0);
    g2d.rotate(cc90);
    g2d.drawLine(0, 0, len, 0);
    g2d.translate(len,0);
    g2d.rotate(cw90);
  }
}


width=800;
height=800;

frame = new javax.swing.JFrame();
pan = new JavaAdapter(javax.swing.JPanel,
  {
    paint:function(g2d){
      timer(function(){draw(g2d)});
  }
})

frame.title="Ringo";
frame.setContentPane(pan);
frame.pack();
frame.setSize(width,height);
frame.show();
