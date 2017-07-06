
module keystem() {
totalheight=6.4;
depthofcross=4.5;
translationofcross = totalheight-0.5*depthofcross;

rotate([180,0,0]) translate([0,0,-totalheight]) difference(){ //stemofkeycap
    
    
    cylinder(h=totalheight,r=5.54/2,$fn=36);
    union(){
        translate([0,0,translationofcross]) cube([1.35,4.10,depthofcross+0.5], center=true);
        translate([0,0,translationofcross]) rotate([0,0,90]) cube([1.35,4.10,depthofcross+0.5], center=true);
    }
}};

keystem() ;

