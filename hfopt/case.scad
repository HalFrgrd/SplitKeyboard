use <obiscad/attach.scad>
use <obiscad/vector.scad>


keyholeWidth            = 14.00;
gapBetweenSwitches      = 5.05;
matrixLength            = 5; //y axis
matrixWidth             = 2; //x axis
borderAroundEdge        = 1;
switchPlateDepth        = 1.5;
showfakekeycaps         = false;
//angledNumberLayer       = true;
//this the matrix width with the keyholes, gaps, and border.
matrixWidthGapsBorder   = matrixWidth*keyholeWidth + (matrixWidth-1)*gapBetweenSwitches + borderAroundEdge*2; 
matrixLengthGapsBorder  = matrixLength*keyholeWidth +(matrixLength-1)*gapBetweenSwitches + borderAroundEdge*2;
lowerFrameX             = matrixWidthGapsBorder-5; //controls the tapering of the general case
lowerFrameY             = matrixLengthGapsBorder-5;
lowerFrameHeight        = 0.001;
middleFrameX            = matrixWidthGapsBorder+4;
middleFrameY            = matrixLengthGapsBorder+4+6;
middleFrameHeight       = 0.001;
upperFrameX             = matrixWidthGapsBorder+6;
upperFrameY             = matrixLengthGapsBorder+6+3;
upperFrameHeight        = 0.001;

angledNumberLayerWidth  = keyholeWidth + borderAroundEdge*2 +3;
angledNumberLayerLength = upperFrameX;

angleforNumbered        = 20;
recessforNumbered       = 3;
secondpanelupwardsNumber= 6;

angleUpMid              = 10;
recess                  = 6;
trMid                   = [0,0,10];
trUpper                 = [0,0,trMid[2]+8.3];
roUpperMid              = [angleUpMid,0,0];

anumbered               = [ [-angledNumberLayerLength/2,-angledNumberLayerWidth/2,0] , [-1,0,0],    0  ];
cnumbered               = [ [trUpper[0]-upperFrameX/2, trUpper[1] + (upperFrameY/2-angledNumberLayerWidth) * cos(angleUpMid), trUpper[2] + upperFrameY/2 * sin(angleUpMid) - angledNumberLayerWidth*sin(angleUpMid) ], [1,0,0], angleUpMid];

aswitchmain             = [ [0,-2.5,switchPlateDepth], [1,0,0],0];
cswitchmain             = [ [ 0 /*trUpper[0]-upperFrameX/2*/, trUpper[1] - upperFrameY/2 * cos(angleUpMid), trUpper[2] - upperFrameY/2 * sin(angleUpMid)], [1,0,0], angleUpMid];


aswitchnumbered         = [ [0,0,+switchPlateDepth], [1,0,0],0];
cswitchnumbered         = [ [
                                0,//-angledNumberLayerLength/2,
                                -angledNumberLayerWidth/2 + (1-cos(angleforNumbered))*angledNumberLayerWidth*0.5 ,
                                secondpanelupwardsNumber - sin(angleforNumbered)*angledNumberLayerWidth*0.5
                                ], [1,0,0],angleforNumbered];

debug                   = true;




if (debug){
   
    *connector(anumbered);
    *connector(cnumbered);
    
    *connector(aswitchmain);
    *connector(cswitchmain);

    *connector(aswitchnumbered);
    *connector(cswitchnumbered);
}

module arduinoShape() {
    heightOfArduino = 4;
    translate([0,matrixLengthGapsBorder/2,heightOfArduino/2 -0.1]) cube([19,30,heightOfArduino],center=true);
}

module trsJack(){
    heightOfTRS = 6;
    translate([20,matrixLengthGapsBorder/2,heightOfTRS/2-0.1]) cube([7,30,heightOfTRS],center=true);
}

module resetSwitchShape(){
    heightOfResetSwith = 2.7;
    translate([-22,matrixLengthGapsBorder/3,3]) union() {
    cube([heightOfResetSwith,30,heightOfResetSwith]);
    translate([8,0,0]) cube([heightOfResetSwith,30,heightOfResetSwith]);
}

}


module buildKeyHole() {
    if (showfakekeycaps)
    {
        translate([0,0,-0.1])
        cube([keyholeWidth+4,keyholeWidth+4,switchPlateDepth+16]);
    } 
    else {
        translate([0,0,-0.1]) // this is because you can't have 0 width when doing difference
        cube([keyholeWidth,keyholeWidth,switchPlateDepth+0.2]);
    };
};

module buildSwitchPlate(numberOfKeysWidth=matrixWidth,numberOfKeysLength=matrixLength,increaseTopEnd=0,buildsupports=false) {
    width = numberOfKeysWidth*keyholeWidth + (numberOfKeysWidth-1)*gapBetweenSwitches + borderAroundEdge*2;
    length = numberOfKeysLength*keyholeWidth +(numberOfKeysLength-1)*gapBetweenSwitches + borderAroundEdge*2 - borderAroundEdge+2 + + increaseTopEnd;
    

    
    translate([-width/2,0,0])
    
    union(){
        difference() {
            cube([
                width ,
                length,
                switchPlateDepth
            ]);
            
            for(x = [1:numberOfKeysWidth]) {
                for(y = [1:numberOfKeysLength]) {
                    translate([
                        borderAroundEdge + (x-1)*keyholeWidth + (x-1)*gapBetweenSwitches,
                        borderAroundEdge + (y-1)*keyholeWidth + (y-1)*gapBetweenSwitches,
                        0]) 
                    buildKeyHole();
                   

                    
                };
            };
        };

        for(x = [1:numberOfKeysWidth]) {
                for(y = [1:numberOfKeysLength]) {
                    translate([
                        borderAroundEdge + (x-1)*keyholeWidth + (x-1)*gapBetweenSwitches -0.5,
                        borderAroundEdge + (y-1)*keyholeWidth + (y-1)*gapBetweenSwitches + keyholeWidth/2,
                        0.5]) 
                    //sphere(r=1,$fn=20);
                    rotate([0,30,0]) cube([1,1,1]);
                   

                    
                };
            };

    if(buildsupports){
        for(x = [1.5:0.5:numberOfKeysWidth+0.5]) {
            for(y = [2:numberOfKeysLength+1]) {
                
                    
                        translate([
                        borderAroundEdge + (x-1)*keyholeWidth + (x-1)*gapBetweenSwitches - 5/2,
                        borderAroundEdge + (y-1)*keyholeWidth + (y-1)*gapBetweenSwitches - 5/2 ,
                        -30]) 
                        cylinder(h=30,d=5,$fn=24);
                    
                };
            };
        };
    };
};

*buildSwitchPlate(buildsupports=true);

//The buildGeneralCase uses a hull on three rectangular frames (lower, middle, upper).

module buildNumberLayerAngled() {

    difference() {
        hull() {
            buildRectangularFrame(x=angledNumberLayerLength,y=angledNumberLayerWidth,z=0.001);
            translate([0,0,secondpanelupwardsNumber]) rotate([angleforNumbered,0,0]) buildRectangularFrame(x=angledNumberLayerLength,y=angledNumberLayerWidth,z=0.001);
        };
        hull() {
            translate([0,0,-1]) buildRectangularFrame(x=angledNumberLayerLength-recess,y=angledNumberLayerWidth-recessforNumbered,z=0.001+1);
            translate([0,0,secondpanelupwardsNumber+1]) rotate([angleforNumbered,0,0]) 
                buildRectangularFrame(x=angledNumberLayerLength-recess,y=angledNumberLayerWidth-recessforNumbered,z=0.001+1);
               
        };

        translate([0,0,secondpanelupwardsNumber+1]) rotate([angleforNumbered,0,0]) translate([0,18.5,-2]) buildSwitchPlate(numberOfKeysLength=1);

    };
};

module buildRectangularFrame(x=0,y=0,z=0) {
    cube([
            x,
            y,
            z,
    ], center=true);
};

module buildGeneralCase() {
    

    difference() { //this makes the general frame
        hull() {
            translate([0,0,0]) buildRectangularFrame(lowerFrameX,lowerFrameY,lowerFrameHeight);
            translate(trMid) rotate(roUpperMid) buildRectangularFrame(middleFrameX,middleFrameY,middleFrameHeight);
            translate([trUpper[0],trUpper[1],trUpper[2]]) rotate(roUpperMid) buildRectangularFrame(upperFrameX,upperFrameY,upperFrameHeight);
        };
    
        hull() {
            translate([0,0,0-1])                    buildRectangularFrame(lowerFrameX-recess,lowerFrameY-recess,lowerFrameHeight+1);
            translate(trMid) rotate(roUpperMid)     buildRectangularFrame(middleFrameX-recess,middleFrameY-recess,middleFrameHeight);
            translate([trUpper[0],trUpper[1],trUpper[2]+1]) rotate(roUpperMid)   buildRectangularFrame(upperFrameX-recess,upperFrameY-recess,upperFrameHeight+1);
        };
    };
};


module screwhole(action) {
    holediameter = 3;
    greaterHole = holediameter + 6;
    heightForScrew = 9.6;

    if(action=="makeholder")
    {
        translate([-greaterHole/2,-greaterHole/2]) cube([greaterHole,greaterHole,heightForScrew]);
        //cylinder(h=6,d=greaterHole);
    }  
    else if(action=="makehole")
    {
        translate([0,0,-0.5]) cylinder(h=heightForScrew+1,d=holediameter,$fn=30);
    }
}

*difference() {
    screwhole("makeholder");
    screwhole("makehole");
}

*buildSwitchPlate(numberOfKeysLength=matrixLength-1,increaseTopEnd=100);
*buildSwitchPlate(numberOfKeysLength=1);


difference() {
    buildGeneralCase();
    arduinoShape();
    trsJack();
    resetSwitchShape();
}





attach(cnumbered,anumbered) union() {
buildNumberLayerAngled();
attach(cswitchnumbered,aswitchnumbered) buildSwitchPlate(numberOfKeysLength=1,increaseTopEnd=1);

};
difference() {
        attach(cswitchmain,aswitchmain) buildSwitchPlate(numberOfKeysLength=matrixLength-1,increaseTopEnd=7,buildsupports=true);
        translate([0,0,-50]) cube([lowerFrameX+40,lowerFrameY+40,100],center=true);
}

difference() {
translate([-lowerFrameX/2,-lowerFrameY/2-9,0]) screwhole("makeholder");
translate([-lowerFrameX/2,-lowerFrameY/2-9,0]) screwhole("makehole");
}
difference() {
translate([lowerFrameX/2,-lowerFrameY/2-9,0]) screwhole("makeholder");
translate([lowerFrameX/2,-lowerFrameY/2-9,0]) screwhole("makehole");
}