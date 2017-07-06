use <obiscad/attach.scad>
use <obiscad/vector.scad>

c1 = [[10,10,10], [0,0,1], 20];

connector(c1);
connector(a1);

echo(-[1,1,1]);

a1 = [[0,0,3], [1,0,1], 45];

cube([10,10,10]);

attach(c1, a1) 
union() {
    sphere(r=3,center=true);
    translate([0,-10,0]) cube([2,10,2]);
};