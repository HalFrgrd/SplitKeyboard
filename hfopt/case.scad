use <obiscad/attach.scad>
use <obiscad/vector.scad>


keyholeWidth            = 14.00;
biggerKeyholeWIdth      = 14.00 + 0.4;

gapBetweenSwitches      = 5.05;
matrixLength            = 5; //y axis
matrixWidth             = 7; //x axis
borderAroundEdge        = 1;
switchPlateDepth        = 1.5;
showfakekeycaps         = false;
//angledNumberLayer       = true;
//this the matrix width with the keyholes, gaps, and border.
matrixWidthGapsBorder   = matrixWidth*keyholeWidth + (matrixWidth-1)*gapBetweenSwitches + borderAroundEdge*2; 
matrixLengthGapsBorder  = matrixLength*keyholeWidth +(matrixLength-1)*gapBetweenSwitches + borderAroundEdge*2;
lowerFrameX             = matrixWidthGapsBorder+4; //controls the tapering of the general case
lowerFrameY             = matrixLengthGapsBorder+4;
lowerFrameHeight        = 0.001;
lowerFrameRecess        = 3;
middleFrameX            = matrixWidthGapsBorder+4;
middleFrameY            = matrixLengthGapsBorder+4+6;
middleFrameHeight       = 0.001;
upperFrameX             = matrixWidthGapsBorder+6;
upperFrameY             = matrixLengthGapsBorder+6+3;
upperFrameHeight        = 0.001;

angledNumberLayerWidth  = keyholeWidth + borderAroundEdge*2 +6 ;
angledNumberLayerLength = upperFrameX;

switchplatewidthpadding = 3;

angleforNumbered        = 30;
recessforNumbered       = 5;
recessforNumberedy      = 5;
secondpanelupwardsNumber= 6;

angleUpMid              = 8;
recess                  = 6;
trMid                   = [0,0,10];
trUpper                 = [0,0,trMid[2]+8.3];
roUpperMid              = [angleUpMid,0,0];

anumbered               = [ [-angledNumberLayerLength/2,-angledNumberLayerWidth/2,0] , [-1,0,0],    0  ];
cnumbered               = [ [trUpper[0]-upperFrameX/2, trUpper[1] + (upperFrameY/2-angledNumberLayerWidth) * cos(angleUpMid) +0, trUpper[2] + upperFrameY/2 * sin(angleUpMid) - angledNumberLayerWidth*sin(angleUpMid) ], [1,0,0], angleUpMid];

aswitchmain             = [ [0,-2.5,switchPlateDepth-0.1], [1,0,0],0];
cswitchmain             = [ [ 0 /*trUpper[0]-upperFrameX/2*/, trUpper[1] - upperFrameY/2 * cos(angleUpMid), trUpper[2] - upperFrameY/2 * sin(angleUpMid)], [1,0,0], angleUpMid];


aswitchnumbered         = [ [0,-3,+switchPlateDepth], [1,0,0],0];
cswitchnumbered         = [ [
                                0,//-angledNumberLayerLength/2,
                                -angledNumberLayerWidth/2 + (1-cos(angleforNumbered))*angledNumberLayerWidth*0.5 +0 ,
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
    translate([-40,matrixLengthGapsBorder/3,3]) union() {
    cube([heightOfResetSwith,30,heightOfResetSwith]);
    translate([8,0,0]) cube([heightOfResetSwith,30,heightOfResetSwith]);}
}


module buildKeyHole(keywidth=keyholeWidth,makesmallerforprinting=false) {
    if (makesmallerforprinting)
    {
        translate([0,0,-0.1])
        cube([biggerKeyholeWIdth,biggerKeyholeWIdth,switchPlateDepth+0.2]);
    } 
    else {
        translate([0,0,-0.1]) // this is because you can't have 0 width when doing difference
        cube([keywidth,keywidth,switchPlateDepth+0.2]);
    };
};
module littleknob() {
    //cube([1,1,1]);

    translate([0,0,0.5]) difference() {
            translate([0,0,-0.5]) intersection() {
                rotate([0,50,0]) cube([1,3,2],center=true);
                rotate([0,-50,0]) cube([1,3,2],center=true);
            }

            translate([-10,-10,+switchPlateDepth/2]) cube([20,20,20]);
            translate([-10,-10,-20-switchPlateDepth/2]) cube([20,20,20]);
        };

    };
    
module buildSwitchPlate(numberOfKeysWidth=matrixWidth,numberOfKeysLength=matrixLength,increaseTopEnd=0,buildsupports=false, buildknobs=false, makesmallerforprinting=false) {
    
    width = numberOfKeysWidth*keyholeWidth + (numberOfKeysWidth-1)*gapBetweenSwitches + borderAroundEdge*2 ;
    length = numberOfKeysLength*keyholeWidth +(numberOfKeysLength-1)*gapBetweenSwitches + borderAroundEdge*2  + increaseTopEnd ;
    
    translate([-width/2,0,0])
    
    difference(){
        union() {
        difference() {
            translate([-switchplatewidthpadding/2,-2,0])
            cube([
                width  + switchplatewidthpadding,
                length,
                switchPlateDepth
            ]);
            
            for(x = [1:numberOfKeysWidth]) {
                for(y = [1:numberOfKeysLength]) {

                    translations = [borderAroundEdge + (x-1)*keyholeWidth + (x-1)*gapBetweenSwitches - (biggerKeyholeWIdth- keyholeWidth)/2,
                    borderAroundEdge + (y-1)*keyholeWidth + (y-1)*gapBetweenSwitches - (biggerKeyholeWIdth- keyholeWidth)/2,
                    0];
                    
                
                    translate(translations) 
                    buildKeyHole(makesmallerforprinting=makesmallerforprinting);

                    
                };
            };
        };

        
        if(buildknobs){
        for(x = [1:numberOfKeysWidth]) {
                for(y = [1:numberOfKeysLength]) {
                    translate([
                        borderAroundEdge + (x-1)*keyholeWidth + (x-1)*gapBetweenSwitches + keyholeWidth/2,
                        borderAroundEdge + (y-1)*keyholeWidth + (y-1)*gapBetweenSwitches + keyholeWidth/2,
                        0.5]) 
                    //sphere(r=1,$fn=20);
                    union() {
                        if(makesmallerforprinting){
                        translate([-biggerKeyholeWIdth/2,0,0]) littleknob();
                        translate([+biggerKeyholeWIdth/2,0,0]) littleknob();  
                    }
                    }

                   

                    
                };
            };}

    if(buildsupports){
        for(x = [2:numberOfKeysWidth]) {
            for(y = [2:numberOfKeysLength]) {
                height=5;
                    
                        translate([
                        borderAroundEdge + (x-1)*keyholeWidth + (x-1)*gapBetweenSwitches - 5/2,
                        borderAroundEdge + (y-1)*keyholeWidth + (y-1)*gapBetweenSwitches - 5/2,
                        -height/2+0.1]) 
                        union() {
                            //cube([1,1,height],center=true);
                        cube([2,38,height],center=true);
                        cube([38,2,height],center=true);
                    }
                    
                };
            };
        };
    };

    if(makesmallerforprinting){
        translate([width/2,length+2,0]) cube([width+10,10,10],center=true);
        translate([width/2,0-7,0]) cube([width+10,10,10],center=true);
        translate([0-6,length/2,0]) cube([10,length+10,10],center=true);
        translate([width+6,length/2,0]) cube([10,length+10,10],center=true);
    }
    };
};

*buildSwitchPlate(buildsupports=true,makesmallerforprinting=true);

//The buildGeneralCase uses a hull on three rectangular frames (lower, middle, upper).

module buildNumberLayerAngled() {

    difference() {
        hull() {
            buildRectangularFrame(x=angledNumberLayerLength,y=angledNumberLayerWidth,z=0.001);
            translate([0,0,secondpanelupwardsNumber]) rotate([angleforNumbered,0,0]) buildRectangularFrame(x=angledNumberLayerLength,y=angledNumberLayerWidth,z=0.001);
        };
        hull() {
            translate([0,0,-1]) buildRectangularFrame(x=angledNumberLayerLength-recessforNumbered,y=angledNumberLayerWidth-recessforNumberedy,z=0.001+1);
            translate([0,-1,secondpanelupwardsNumber+1]) rotate([angleforNumbered,0,0]) 
                buildRectangularFrame(x=angledNumberLayerLength-recessforNumbered,y=angledNumberLayerWidth-recessforNumberedy,z=0.001+1);
               
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
            translate([0,0,0-1])                    buildRectangularFrame(lowerFrameX-lowerFrameRecess,lowerFrameY-lowerFrameRecess,lowerFrameHeight+1);
            translate(trMid) rotate(roUpperMid)     buildRectangularFrame(middleFrameX-recess,middleFrameY-recess,middleFrameHeight);
            translate([trUpper[0],trUpper[1],trUpper[2]+1]) rotate(roUpperMid)   buildRectangularFrame(upperFrameX-recess,upperFrameY-recess,upperFrameHeight+1);
        };
    };
};


module screwhole(action) {
    holediameter = 3;
    greaterHole = holediameter + 6;
    heightForScrew = 11;

    if(action=="makeholder")
    {
        translate([-greaterHole/2,-greaterHole/2]) cube([greaterHole,greaterHole,heightForScrew]);
    }  
    else if(action=="makehole")
    {
        translate([0,0,-0.5]) cylinder(h=heightForScrew+1,d=holediameter,$fn=30);
    }
}




difference() {
    buildGeneralCase();
    arduinoShape();
    trsJack();
    resetSwitchShape();
    difference() {
        attach(cswitchmain,aswitchmain) buildSwitchPlate(numberOfKeysLength=matrixLength-1,increaseTopEnd=5.5,buildsupports=true,buildknobs=false);
        translate([0,0,-50]) cube([lowerFrameX+40,lowerFrameY+40,100],center=true);
}
}

translate([0,-200,0]) buildSwitchPlate(numberOfKeysLength=matrixLength-1,increaseTopEnd=5.5,buildsupports=true,buildknobs=false,makesmallerforprinting=true);
translate([0,-100,0]) buildSwitchPlate(numberOfKeysLength=1,increaseTopEnd=3,buildknobs=true,makesmallerforprinting=true);


attach(cnumbered,anumbered) difference() {
    difference() {
        buildNumberLayerAngled();
        attach(cswitchnumbered,aswitchnumbered) 
        translate([0,0.5,0.01]) hull() 
        {
            buildSwitchPlate(numberOfKeysLength=1,increaseTopEnd=3);
        };
    }

};


ydistancescrewholes = 0    ;
xdistancescrewholes = -4;
difference(){
    // hull() {
    // translate([-lowerFrameX/2-xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes,0]) screwhole("makeholder");

    // translate([lowerFrameX/2+xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes,0]) screwhole("makeholder");
    // }

    // translate([-lowerFrameX/2-xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes,0]) screwhole("makehole");
    // translate([lowerFrameX/2+xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes,0]) screwhole("makehole");
    // translate([0,-lowerFrameY/2-ydistancescrewholes,0]) screwhole("makehole");

    union() {
        translate([-lowerFrameX/2-xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes,0]) translate([-3,0,0]) cube([6,6,6]);
        translate([lowerFrameX/2+xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes,0]) translate([-3,0,0]) cube([6,6,6]);
        translate([0                                ,-lowerFrameY/2-ydistancescrewholes,0]) translate([-3,0,0]) cube([6,6,6]);

    }

    union() {
        translate([-lowerFrameX/2-xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes + 3,-0.1]) cylinder(h=6.2,d=3,$fn=20);
        translate([lowerFrameX/2+xdistancescrewholes,-lowerFrameY/2-ydistancescrewholes+ 3,-0.1]) cylinder(h=6.2,d=3,$fn=20);
        translate([0                                ,-lowerFrameY/2-ydistancescrewholes+ 3,-0.1]) cylinder(h=6.2,d=3,$fn=20);
    }
}

ydistancescrewholesback = 4.5;
xdistancescrewholesback = -4.5;
difference() {
    union(){
    translate([-lowerFrameX/2-xdistancescrewholesback,lowerFrameY/2-ydistancescrewholesback,0]) screwhole("makeholder");
    translate([+lowerFrameX/2+xdistancescrewholesback,lowerFrameY/2-ydistancescrewholesback,0]) screwhole("makeholder");
    }
    translate([-lowerFrameX/2-xdistancescrewholesback,lowerFrameY/2-ydistancescrewholesback,0]) screwhole("makehole");
    translate([+lowerFrameX/2+xdistancescrewholesback,lowerFrameY/2-ydistancescrewholesback,0]) screwhole("makehole");
}
