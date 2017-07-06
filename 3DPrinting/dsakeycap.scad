lengthmultipl       = 2.1;
xdim                = 18;
ydim                = xdim*lengthmultipl;
xdimupper           = xdim-2.4;
ydimupper           = xdimupper*lengthmultipl;
lowercorner         = 0.4;
highercorner        = 3.04;
upperedgeradius     = 30;
sphericalindentation= 28;
roundingverticaledges = 19;
roundessxtrans      = 5;
roundessytrans      = roundessxtrans;
roundessztrans      = -4;
keystemheight       = 6;
distanceapartofkeystem = 14.00+5.05;
shorteningofspheresep = 5.5;
upperbaseyextension = 1;

module keystem() {
    totalheight=keystemheight+0.1;
    depthofcross=4.5;
    translationofcross = totalheight-0.5*depthofcross;

    widthstem = 4.40;
    lengthstem = 5.50;

    rotate([180,0,0]) translate([0,0,-totalheight]) 
    difference(){ //stemofkeycap
        
        //cylinder(h=totalheight,r=5.54/2,$fn=36);
        translate([-widthstem/2,-lengthstem/2,0]) cube([widthstem,lengthstem,totalheight]);
        union(){
            translate([0,0,translationofcross]) cube([1.35,4.10,depthofcross+0.5], center=true);
            translate([0,0,translationofcross]) rotate([0,0,90]) cube([1.35,5,depthofcross+0.5], center=true);
        }
}};




difference() { //make hollow    
    intersection() {
        *intersection_for(b = [0:90:359]) {
            rotate([0,0,b-45]) translate([roundessxtrans,roundessytrans,roundessztrans]) sphere(r= roundingverticaledges,$fn=60);
        };

difference() {//spherical indentation

    hull() {
        
        linear_extrude(height=0.001) hull() { //lower base
        square([xdim-lowercorner,ydim-lowercorner],center=true);

        translate([xdim/2-lowercorner,ydim/2-lowercorner,0]) circle(r=lowercorner, $fn=60);
        translate([-(xdim/2-lowercorner),ydim/2-lowercorner,0]) circle(r=lowercorner, $fn=60);
        translate([xdim/2-lowercorner,-(ydim/2-lowercorner),0]) circle(r=lowercorner, $fn=60);
        translate([-(xdim/2-lowercorner),-(ydim/2-lowercorner),0]) circle(r=lowercorner, $fn=60);
        };

         translate([0,0,8]) linear_extrude(height=0.001) hull() { //upper base
            //square([xdimupper-highercorner,ydimupper-highercorner],center=true);
             
             *intersection_for(n=[0:90:359]) {
                 rotate([0,0,n]) translate([upperedgeradius-xdimupper/2 + 0.3,0,0]) circle(r=upperedgeradius,$fn=800);
             };

            translate([xdimupper/2-highercorner,ydimupper/2-highercorner+upperbaseyextension,0]) circle(r=highercorner, $fn=60);
            translate([-(xdimupper/2-highercorner),ydimupper/2-highercorner+upperbaseyextension,0]) circle(r=highercorner, $fn=60);
            translate([xdimupper/2-highercorner,-(ydimupper/2-highercorner+ upperbaseyextension),0]) circle(r=highercorner, $fn=60);
            translate([-(xdimupper/2-highercorner),-(ydimupper/2-highercorner+ upperbaseyextension),0]) circle(r=highercorner, $fn=60);
        };
    };
    hull() {
        translate([0,(ydimupper-xdimupper)-shorteningofspheresep,sphericalindentation+ 8 - 0.9    -   0.5]) rotate([0,90,0]) sphere(r=sphericalindentation,$fn=100);
        translate([0,-(ydimupper-xdimupper)+shorteningofspheresep,sphericalindentation+ 8 - 0.9    -   0.5]) rotate([0,90,0]) sphere(r=sphericalindentation,$fn=100);
    }
};
};





hull(){
  translate([0,0,-0.1]) linear_extrude(height = 0.001) square([xdim-1.8,ydim-1.8],true);
  translate([0,0,keystemheight]) linear_extrude(height = 0.001) square([xdim-1.8-5,ydim-1.8-5],true);
};


};

translate([0,-distanceapartofkeystem/2,0]) keystem();
translate([0,+distanceapartofkeystem/2,0]) keystem();