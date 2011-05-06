timer=timer=require("ringo/utils/debug").timer;

// function radians(degrees){return java.lang.Math.toRadians(degrees)}
radians = java.lang.Math.toRadians;


function draw_tree(g2d,length,depth){
  if(depth > 0){
    g2d.drawLine(0, 0, length, 0);

    g2d.translate(length,0);

    g2d.rotate(radians(-30));
    draw_tree(g2d, 0.75*length, depth-1);
    g2d.rotate(radians(30));

    g2d.rotate(radians(30));
    draw_tree(g2d, 0.75*length, depth-1);
    g2d.rotate(radians(-30));

    g2d.translate(-length,0);

  }
}

function draw(g2d){
  var depth = 16;
  var length = 100;
  print("Depth: " + depth);
  timer(function(){draw_tree(g2d, length, depth)});
  // var start = java.lang.System.nanoTime();
  // draw_tree(g2d, length, depth);
  // var stop = java.lang.System.nanoTime();
  // print(Math.round((stop - start) / 1000000), "millis");

}

width=800;
height=800;
var gg2d;

frame = new javax.swing.JFrame();
pan = new JavaAdapter(javax.swing.JPanel,
  {
    paint:function(g2d){
      g2d.translate( width/2, height/2);
      g2d.rotate(radians(-90));
      draw(g2d);
      // draw = function(g2d){};
  }
})


frame.setContentPane(pan);
frame.pack();
frame.setSize(width,height);
frame.show();

// pan = new JavaAdapter(javax.swing.JFrame,
//   {
//     paint:function(g2d){
//       g2d.translate( width/2, height/2);
//       g2d.rotate(radians(-90));
//       draw(g2d);
//       // draw = function(g2d){};
//   }
// })


// frame.pack();
// frame.setSize(width,height);
// frame.show();
