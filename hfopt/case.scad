keyholeWidth            = 14.00;
gapBetweenSwitches      = 5.05;
matrixLength            = 5; //y axis
matrixWidth             = 7; //x axis
borderAroundEdge        = 3;
switchPlateDepth        = 1.5;
angledNumberLayer       = true;
//this the matrix width with the keyholes, gaps, and border.
matrixWidthGapsBorder   = matrixWidth*keyholeWidth + (matrixWidth-1)*gapBetweenSwitches + borderAroundEdge*2; 
matrixLengthGapsBorder  = matrixLength*keyholeWidth +(matrixLength-1)*gapBetweenSwitches + borderAroundEdge*2;
lowerFrameX             = matrixWidthGapsBorder-5; //controls the tapering of the general case
lowerFrameY             = matrixLengthGapsBorder-5;
lowerFrameHeight        = 0.001;
middleFrameX            = matrixWidthGapsBorder+4;
middleFrameY            = matrixLengthGapsBorder+4;
middleFrameHeight       = 0.001;
upperFrameX             = matrixWidthGapsBorder+2;
upperFrameY             = matrixLengthGapsBorder+2;
upperFrameHeight        = 0.001;

angledNumberLayerWidth  = keyholeWidth + borderAroundEdge*2 +2;
angledNumberLayerLength = upperFrameX;



module buildKeyHole() {
  translate([0,0,-0.1]) // this is because you can't have 0 width when doing difference
  cube([keyholeWidth,keyholeWidth,switchPlateDepth+0.2]);
};

module buildSwitchPlate(numberOfKeysWidth=matrixWidth,numberOfKeysLength=matrixLength) {
    width = numberOfKeysWidth*keyholeWidth + (numberOfKeysWidth-1)*gapBetweenSwitches + borderAroundEdge*2;
    length = numberOfKeysLength*keyholeWidth +(numberOfKeysLength-1)*gapBetweenSwitches + borderAroundEdge*2;
    
    if (angledNumberLayer){ 
        translate([-width/2,-length/2 - (keyholeWidth+gapBetweenSwitches),0]); 

        }
    else {
        translate([-width/2,-length/2,0]);
        };
    
    translate([-width/2,-length/2 - (keyholeWidth+gapBetweenSwitches),0])
    
    difference() {
        cube([
            width ,
            length,
            switchPlateDepth
        ]);
        
        for(x = [1:matrixWidth]) {
            for(y = [1:matrixLength]) {
                translate([
                    borderAroundEdge + (x-1)*keyholeWidth + (x-1)*gapBetweenSwitches,
                    borderAroundEdge + (y-1)*keyholeWidth + (y-1)*gapBetweenSwitches,
                    0]) 
                buildKeyHole();
            };
        };
    };
};


//The buildGeneralCase uses a hull on three rectangular frames (lower, middle, upper).

module buildNumberLayerAngled() {
    angle = 30;
    recess = 3;
    secondpanelupwards = 6.5;
  
    difference() {
        hull() {
            buildRectangularFrame(x=angledNumberLayerLength,y=angledNumberLayerWidth,z=1);
            translate([0,0,secondpanelupwards]) rotate([angle,0,0]) buildRectangularFrame(x=angledNumberLayerLength,y=angledNumberLayerWidth,z=1);
        };
        hull() {
            translate([0,0,-1]) buildRectangularFrame(x=angledNumberLayerLength-recess,y=angledNumberLayerWidth-recess,z=2);
            translate([0,0,secondpanelupwards+1]) rotate([angle,0,0]) 
                buildRectangularFrame(x=angledNumberLayerLength-recess,y=angledNumberLayerWidth-recess,z=2);
               
        };

        translate([0,0,secondpanelupwards+1]) rotate([angle,0,0]) translate([0,18.5,-2]) buildSwitchPlate(numberOfKeysLength=1);

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
    angle = 5;
    recess = 6;
    lip = 2.5;
    difference() {
        difference() {
            hull() {
                translate([0,0,0]) buildRectangularFrame(lowerFrameX,lowerFrameY,lowerFrameHeight);
                translate([0,0,10]) rotate([angle,0,0]) buildRectangularFrame(middleFrameX,middleFrameY,middleFrameHeight);
                translate([0,0,20]) rotate([angle,0,0]) buildRectangularFrame(upperFrameX,upperFrameY,upperFrameHeight);
            };
        translate([0,0,20-switchPlateDepth]) rotate([angle,0,0]) buildSwitchPlate();
    };
        hull() {
            translate([0,0,0-1])                    buildRectangularFrame(lowerFrameX-recess,lowerFrameY-recess,lowerFrameHeight+1);
            translate([0,0,10]) rotate([angle,0,0])     buildRectangularFrame(middleFrameX-recess,middleFrameY-recess,middleFrameHeight);
            translate([0,0,20+1]) rotate([angle,0,0])   buildRectangularFrame(upperFrameX-recess,upperFrameY-recess,upperFrameHeight+1);
        };
    };
    translate([0,38.3,23]) rotate([angle,0,0]) buildNumberLayerAngled();

    
};





*buildNumberLayerAngled();

buildGeneralCase();
*buildSwitchPlate(numberOfKeysLength=matrixLength-1);


