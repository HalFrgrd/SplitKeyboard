(ns optcase.core
  (:gen-class :main true)
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [clojure.math.numeric-tower :refer :all]))


(spit "things/post-demo.scad" ;cleans file
     nil )

;;;;;;;;
;TRANSLATION OF PRIMARY FUNCTIONS OF ATTACH MODULE
;;;;;;;;

(def defaulDrawingResolution 6) ;low res for vector arrows

(defn- sqr [x] ;square x
  (expt x 2))


(defn modofvec [[x y z]] ;modulus of vecotr

	(sqrt (+ (sqr x ) (sqr y) (sqr z) )))

(defn cross [[x1 y1 z1] [x2 y2 z2]] ;cross product
	(vector
		(- (* y1 z2) (* z1 y2))
  		(- (* z1 x2) (* x1 z2))
  		(- (* x1 y2) (* y1 x2))
  		))

(defn dot [[x1 y1 z1] [x2 y2 z2]] ;dot product
	 (+ (* x1 x2) (* y1 y2) (* z1 z2)))

(defn unitv [[x y z]] ;unit vecotr
	(vector 
		(/ x (modofvec [x y z]) )
		(/ y (modofvec [x y z]) ) 
		(/ z (modofvec [x y z]) )))


(defn anglev [[x1 y1 z1] [x2 y2 z2]] ;angle between vectors in radians
	(Math/acos (/ (dot [x1 y1 z1] [x2 y2 z2]) (* (modofvec [x1 y1 z1]) (modofvec [x2 y2 z2])))))

(defn point [p] ;make a small sphere at location p
	(->>(sphere 0.7)
		(translate p)
		(with-fn defaulDrawingResolution)))

(defn vectorz [l l_arrow mark]  ;make a vector in the z direction of length l, arrow length l_arrow, mark (true or false) to show angle
	(let [lb 	(- l l_arrow)]
		(union
		(translate [0 0 (/ lb 2)] 
			(union

				(->>(cylinder [1 0.2] l_arrow);draw tye arrow
					(translate [0 0 (/ lb 2)])
					(with-fn defaulDrawingResolution))

				(if mark
					(->>
						(cube 2 0.3 (* 0.8 l_arrow) )
						(translate [1 0 0])
						(translate [0 0 (+ (/ lb 2))])
						)
					)

				(->> (cylinder 0.5 lb)
					 (with-fn defaulDrawingResolution))
				)
			)
		(->> (sphere 1)
			 (with-fn defaulDrawingResolution))
		))

	)

(defn orientate 
	([v shape] (orientate v [0 0 1] 0 shape)) ;for default values

	([v vref roll shape]
	(let [
		raxis 		(cross vref v) 
		ang 		(anglev vref v)]

	(->> shape
		(rotate ang raxis)
		(rotate roll v)
		)
	)))

(defn drawingvector [v l l_arrow mark]
	(->>(vectorz l l_arrow mark)
		(orientate v)
		))
	
(defn connector [[u v ang]] ;u is position, v is vector, ang is rotation around vector
	
	(union
		(->> (point u)
			(color [0.5 1 1 1]))

		(->> (drawingvector v 8 2 true)
			(color [0.5 1 1 1])
			(rotate ang v)
			(translate u)
			)

		))

(defn attach [mainpart seconpart shape]
	(let [ ;get data from parts
		pos1 		(first mainpart)
		v			(second mainpart)
		roll 		(nth mainpart 2)

		pos2 		(first seconpart)
		vref		(second seconpart)

		; calculation of the roll axis
		raxis 		(cross vref v)

		;calculate the angle between the vectors
		ang 		(anglev vref v)
		]

	(->> shape
		(translate (map #(- %1) pos2))
		(rotate ang raxis)
		(rotate roll v)
		(translate pos1)
		)

	))


(def a1 [[0 0 0] [1 0 0] 0])
(def c1 [[5 5 5] [0 0 1] 2])
(def testshapes (union (sphere 3) (cube 2 10 2) (cube 10 2 2)))

(defn testmessing [] (union
	(connector a1)
	(connector c1)

	; (->> (cube 10 10 10)
	; 	 (color [0.6 0.8 0.2 0.5]))

	testshapes

	(attach c1 a1 testshapes)

	))

;;;;;;;;;;;;
;GETTING THE PLATE MATRIX SET UP
;;;;;;;;;;;;
; x is the internal
; y is the external
; each is a map: 
; {
; :cpnt [[1 2 3] [0 0 1] 0]
; :xcoord		3
; :ycoord 	3
; }


; [
; [ 0 1 2 3 4 5]
; [ 0 1 2 3 4 5]
; [ 0 1 2 3 4 5]
; [ 0 1 2 3 4 5]
; ]

(def keywidthForSpacing 	14.4)
(def keySpacing 			5.05)
(def arrXWid				8 )
(def arrYLen				5 )

(defn createarray [x y] ;x is across, y is down
	(vec(for [ycoin (range y)]
		(vec (for [xcoin (range x)]
			{:xcoord xcoin, 
			 :ycoord ycoin,
			 :cpntPos [ (* xcoin (+ keySpacing keywidthForSpacing)) (* ycoin (+ keySpacing keywidthForSpacing)) 0], 
			 :cpntVec [0 0 1],
			 :cpntAng 0}
			)
		))
	))




(defn retr [arr x y]
	((arr y) x)
)

(defn putsquareinarr [arr & more]
	(for [ycoin (range arrYLen) xcoin (range arrXWid)]

		(let [
			pntData 	(retr arr xcoin ycoin)
			cpntP 		(:cpntPos pntData)
			cpntV 		(:cpntVec pntData)
			cpntA 		(:cpntAng pntData)

			]
			;(println pntData)
			
			(attach [cpntP cpntV cpntA] [[0 0 0] [0 0 1] 0] (difference (cube 16 16 0.5) (cube 14 14 1)))
			
		)
	))

(defn moveonXYplane [arr xmove ymove zmove columnorrow number]
	(vec(for [ycoin (range arrYLen)]
		(vec (for [xcoin (range arrXWid)]
			(let [
				pntData (retr arr xcoin ycoin)
				xval		(:xcoord pntData)
				yval  		(:ycoord pntData)
				cpntP 		(:cpntPos pntData)
				cpntV 		(:cpntVec pntData)
				cpntA 		(:cpntAng pntData)
				]
				

				{:xcoord xval, 
				 :ycoord yval,
				 :cpntPos (if (= xcoin number) 
				 				[ (+ (cpntP 0) xmove) (+ (cpntP 1) ymove) (+ (cpntP 2) zmove)]
				 				cpntP)
				 :cpntVec cpntV,
				 :cpntAng cpntA}
				

				)

			)
		))
	)
	)



;;;;;;;;;;;;;;;;
;; SA Keycaps ;;
;;;;;;;;;;;;;;;;
(def plate-thickness 4)
(def dsa-length 18.25)
(def dsa-double-length 37.5)
(def heightbaseofkeycap 6.1 )
(def dsa-cap {1 (let [bl2 (/ 18.5 2)
                     m (/ 18 2)
                     key-cap (hull (->> (polygon [[bl2 bl2] [bl2 (- bl2)] [(- bl2) (- bl2)] [(- bl2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[m m] [m (- m)] [(- m) (- m)] [(- m) m]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 1.9]))
                                   (->> (polygon [[6 6] [6 -6] [-6 -6] [-6 6]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 7.9])))]
                 (->> key-cap
                      (translate [0 0 heightbaseofkeycap])
                      (color [220/255 163/255 163/255 1])))
             2 (let [bl2 (/ dsa-double-length 2)
                     bw2 (/ 18.25 2)
                     key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 0.05]))
                                   (->> (polygon [[6 16] [6 -16] [-6 -16] [-6 16]])
                                        (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                        (translate [0 0 12])))]
                 (->> key-cap
                      (translate [0 0 (+ 5 plate-thickness)])
                      (color [127/255 159/255 127/255 1])))
             1.5 (let [bl2 (/ 18.25 2)
                       bw2 (/ 28 2)
                       key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 0.05]))
                                     (->> (polygon [[11 6] [-11 6] [-11 -6] [11 -6]])
                                          (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                          (translate [0 0 12])))]
                   (->> key-cap
                        (translate [0 0 (+ 5 plate-thickness)])
                        (color [240/255 223/255 175/255 1])))})

(defn showkeycaps [arr]
		(for [ycoin (range arrYLen) xcoin (range arrXWid)]
			(let [
			pntData 	(retr arr xcoin ycoin)
			cpntP 		(:cpntPos pntData)
			cpntV 		(:cpntVec pntData)
			cpntA 		(:cpntAng pntData)
				]
			(when (pntData :existence)
				(attach [cpntP cpntV cpntA]
						[[0 0 0] [0 0 1] 0]
						(dsa-cap 1)
					))

	)))

(defn showconnectors [arr]
		(for [ycoin (range arrYLen) xcoin (range arrXWid)]
			(let [
			pntData 	(retr arr xcoin ycoin)
			cpntP 		(:cpntPos pntData)
			cpntV 		(:cpntVec pntData)
			cpntA 		(:cpntAng pntData)
				]

			(connector [cpntP cpntV cpntA]
				)

	)))

;
;Making web
;

(def leftedgepadding 3)
(def rightedgepadding 3)
(def topedgepadding 3)
(def bottedgepadding 3)

(def mount-hole-width 14)
(def mount-hole-height 14)


(def web-thickness plate-thickness )
(def post-size 0.1)
(def web-post (->> (cube post-size post-size web-thickness)
                   (translate [0 0 (/ web-thickness -2)])))
(def edgepost (scale [1 1 3] web-post))


(def post-adj (/ post-size 2))
(def web-post-tr (translate [(- (/ mount-hole-width  2) post-adj) (- (/ mount-hole-height  2) post-adj) 0] web-post))
(def web-post-tl (translate [(+ (/ mount-hole-width -2) post-adj) (- (/ mount-hole-height  2) post-adj) 0] web-post))
(def web-post-bl (translate [(+ (/ mount-hole-width -2) post-adj) (+ (/ mount-hole-height -2) post-adj) 0] web-post))
(def web-post-br (translate [(- (/ mount-hole-width  2) post-adj) (+ (/ mount-hole-height -2) post-adj) 0] web-post))

(def web-post-edge-tr (translate [(- (/ mount-hole-width  2) post-adj) (- (/ mount-hole-height  2) post-adj) 0] edgepost))
(def web-post-edge-tl (translate [(+ (/ mount-hole-width -2) post-adj) (- (/ mount-hole-height  2) post-adj) 0] edgepost))
(def web-post-edge-bl (translate [(+ (/ mount-hole-width -2) post-adj) (+ (/ mount-hole-height -2) post-adj) 0] edgepost))
(def web-post-edge-br (translate [(- (/ mount-hole-width  2) post-adj) (+ (/ mount-hole-height -2) post-adj) 0] edgepost))

(defn triangle-hulls [& shapes]
	"TBH I didn't write this. Adereth did. Its just a nice hulling function that makes 
	multiple hulls instead of one big hull. I guess hulls in sets of three shapes as 
	three points will always form a plane. This way the hulls will always be planes (flat)"
  (apply union
         (map (partial apply hull)
              (partition 3 1 shapes))))


(defn getcolPntData [arr x y]
	"Similar to getrowPntData but this deals with x. See getrowPntData and smartretrPntData"
	(cond 
		(= x arrXWid)
			(retr arr (dec x) y)
		(= x -1)
			(retr arr (inc x) y)
		(< x -1)
			(retr arr 0 y)
		:else
			(retr arr x y)
		)
	)

(defn getrowPntData [arr x y]
	"Retrieves the pntData at x y in arr and makes sure that y is in the correct range. See smartretrPntData 
	for what it does when y is out of bounds. This only deals with y as it gets getcolPntData to deal with x."
	(cond 
		(= y arrYLen)
			(getcolPntData arr x (dec y))
		(= y -1)
			(getcolPntData arr x (inc y))
		(< y -1)
			(getcolPntData arr x 0)
		:else 
			(getcolPntData arr x y) 
			)

	)

(defn smartretrPntData [arr x y]
	"This is used to make the edges of the plate. Because the webbing loops through -1
	to arrXWid (or arrYLen), you need to be careful not to go outside of the array.
	this function catches when you are at -1 or arrYLen, and returns the closest good position
	+ edge padding. For instance if you call the array with arrXWid, this will find arrXWid - 1,
	then it will return arrXWid -1 with an updated x pos. This update will include edge padding.

	getrowPntData is used because inside of getrowPntData it calls getcolPntData. You can't also 
	call getrowPntData inside getcolPntData because then you will get an infinite loop."
	

	(getrowPntData arr x y)
	

	)

(defmacro make-fn [m] 
  `(fn [& args#] 
    (eval `(~'~m ~@args#))))

(defn putupapost [arr xcoin ycoin pos callingfrom makingwhat buildedgesornot]
	(let [
		pntData (smartretrPntData arr xcoin ycoin)
		cpntP 		(:cpntPos pntData)
		cpntV 		(:cpntVec pntData)
		cpntA 		(:cpntAng pntData)
		exists		(:existence pntData)

		xtrans (cond 
				(= xcoin -1) 
					(- 0 leftedgepadding mount-hole-width)
				(= xcoin arrXWid) 
					(+ rightedgepadding mount-hole-width)
				(= exists false)
					; (cond 
					; 	(= :callfromthisone callingfrom)
					; 		0
					; 	(= :callfromleftbelow callingfrom)
					; 		0
					; 	(= :callfromleft callingfrom)
					; 		0
					; 	(= :callfrombelow callingfrom)
					; 		0
						
					; 	)
					0
				:else
					0
				)
		ytrans (cond 
				(= ycoin -1) 
					(- 0 bottedgepadding mount-hole-height)
				(= ycoin arrYLen) 
					(+ topedgepadding mount-hole-height)
				(= exists false)
					; (cond 
					; 	(= :callfromthisone callingfrom)
					; 		2
					; 	(= :callfromleftbelow callingfrom)
					; 		-2
					; 	(= :callfromleft callingfrom)
					; 		(if (= :makingcolumns makingwhat)
					; 			2
					; 			-2)
					; 	(= :callfrombelow callingfrom)
					; 		-2
						
					; 	)

					0
				:else
					0
				)
		
		edge    ;this determines if the post should be an edge post or not.
				;This is easy if x or y is -1 or arrxwid or arrylen.
				;also easy if x or y is 0 and being called from the left or below. 
				;Remember that the webbing starts with one key and then awakens keys to the right or above it.
				;A littl trickier is if x or y is one less than their max. They have to be making columns or rows 
				;or diags and being called from either this one or from a suitable thing.
				;For instance, diagonals being called from below shouldn't be edged because this would create edge posts one key in from the actual edge.
				;Then, if any key in the currently forming web should be non existent, there needs to be an edge post.
				;For example, |n|c|, n is nonexistent, c is current key. That current key needs to look to the left and see that key exists. It doesn't, so put an edge post up.)
				;Also |c|n| would need to check to the right.
				;This is only two combinations for either row or column, but for diagonals, 4 different keys and each one needs to check 3 keys.
				(if buildedgesornot 
					(or 
					    (= xcoin -1 ) 
						(= ycoin -1 ) 
						(= xcoin arrXWid) 
						(= ycoin arrYLen) 
						(and (= xcoin 0) (= callingfrom :callfromleft))
						(and (= xcoin 0) (= callingfrom :callfromleftbelow ))
						(and (= ycoin 0) (= callingfrom :callfrombelow))
						(and (= ycoin 0) (= callingfrom :callfromleftbelow ))

						(and (= ycoin (dec arrYLen)) (or    (and (= makingwhat :makingcolumns) (= callingfrom :callfromthisone))
															(and (= makingwhat :makingdiag) ( or (= callingfrom :callfromthisone) (= callingfrom :callfromleft)))
															))
						(and (= xcoin (dec arrXWid)) (or    (and (= makingwhat :makingrows) (= callingfrom :callfromthisone))
															(and (= makingwhat :makingdiag) (= callingfrom :callfromthisone))
															(and (= makingwhat :makingdiag) (= callingfrom :callfrombelow))))

						(let [
						neighbours 	(cond
										(= makingwhat :makingcolumns)
											(cond
												(= callingfrom :callfromthisone)
													[(smartretrPntData arr xcoin ycoin) (smartretrPntData arr xcoin (inc ycoin))]
												(= callingfrom :callfrombelow)
													[(smartretrPntData arr xcoin (dec ycoin)) (smartretrPntData arr xcoin ycoin)]
												)
										(= makingwhat :makingrows)
											(cond
												(= callingfrom :callfromthisone)
													[(smartretrPntData arr xcoin ycoin) (smartretrPntData arr (inc xcoin) ycoin)]
												(= callingfrom :callfromleft)
													[(smartretrPntData arr (dec xcoin) ycoin) (smartretrPntData arr xcoin ycoin)]
												)
										(= makingwhat :makingdiag)
											(cond
												(= callingfrom :callfromthisone)
													[(smartretrPntData arr xcoin ycoin)
													 (smartretrPntData arr (inc xcoin) ycoin)
													 (smartretrPntData arr xcoin (inc ycoin))
													 (smartretrPntData arr (inc xcoin) (inc ycoin))]
												(= callingfrom :callfromleft)
													[(smartretrPntData arr (dec xcoin) ycoin)
													 (smartretrPntData arr xcoin ycoin)
													 (smartretrPntData arr (dec xcoin) (inc ycoin))
													 (smartretrPntData arr xcoin (inc ycoin))]
												(= callingfrom :callfrombelow)
													[(smartretrPntData arr xcoin (dec ycoin))
													 (smartretrPntData arr (inc xcoin) (dec ycoin))
													 (smartretrPntData arr xcoin ycoin)
													 (smartretrPntData arr (inc xcoin) ycoin)]
												(= callingfrom :callfromleftbelow)
													[(smartretrPntData arr (dec xcoin) (dec ycoin))
													 (smartretrPntData arr xcoin (dec ycoin))
													 (smartretrPntData arr (dec xcoin) ycoin)
													 (smartretrPntData arr xcoin ycoin)]
												)
									)
							]
							
							(->>(map #(get %1 :existence) neighbours) 
							(map (make-fn not))
							(apply (make-fn or))
								)))
				false)
						

		post (cond 
				(not edge)
					(cond
						(= pos :tl) (partial web-post-tl)
						(= pos :bl) (partial web-post-bl)
						(= pos :tr) (partial web-post-tr)
						(= pos :br) (partial web-post-br)
						)
				edge 
					(cond
						(= pos :tl) (partial web-post-edge-tl)
						(= pos :bl) (partial web-post-edge-bl)
						(= pos :tr) (partial web-post-edge-tr)
						(= pos :br) (partial web-post-edge-br)
						) )
		]

		(attach 
			[cpntP cpntV cpntA]
			[[0 0 0] [0 0 1] 0]
			(translate [xtrans ytrans 0] post)
			))
		)



(defn neigbhourtoexistence? [arr xcoin ycoin buildingwhat]

	(let [existenceofthisone 			( (smartretrPntData arr xcoin ycoin) :existence)
		  existenceofnextrow 			( (smartretrPntData arr xcoin (inc ycoin)) :existence)
		  existenceofnextcol 			( (smartretrPntData arr (inc xcoin) ycoin) :existence)
		  existenceofnextcolandrow		( (smartretrPntData arr (inc xcoin) (inc ycoin)) :existence)]
		  (cond 
			(= buildingwhat :buildingcolumnconnects)
				(or existenceofthisone existenceofnextrow)
			(= buildingwhat :buildingrowsconnects)
				(or existenceofthisone existenceofnextcol)
			(= buildingwhat :buildingdiagonalsconnects)
				(or existenceofthisone existenceofnextcolandrow existenceofnextrow existenceofnextcol)
			)
		))

	

(defn makeconnectors [arr plateorbase] 
	"Creates posts at the corner of each key switch and hulls them with the posts on other keycaps.
	The edges and corners are created by going one less than the array and one more than the array.
	When the variable is out of bounds, it is caught in the smartretrPntData."
	(let [
		function  			(cond
								(= plateorbase :plate)
								 	(partial triangle-hulls)
								(= plateorbase :base)
									(partial hull)
							)
		otherfunction  		(cond
								(= plateorbase :plate)
								 	(partial union)
								(= plateorbase :base)
									(partial triangle-hulls)
							)
		buildedges 			(cond
								(= plateorbase :plate)
								 	true
								(= plateorbase :base)
									false
							)

		]
	(apply union
		(concat
			;Row connectors

			(for [ycoin (range arrYLen)]
				(otherfunction
				(for  [xcoin (range -1  arrXWid)]
				(when (neigbhourtoexistence? arr xcoin ycoin :buildingrowsconnects)
					(color [1 (rand) 1 1] (function
						(putupapost arr xcoin       ycoin :tr :callfromthisone :makingrows :buildedges)
						(putupapost arr xcoin       ycoin :br :callfromthisone :makingrows :buildedges)
						(putupapost arr (inc xcoin) ycoin :tl :callfromleft    :makingrows :buildedges)
						(putupapost arr (inc xcoin) ycoin :bl :callfromleft    :makingrows :buildedges)
						)
					)
						

					))))

			;Columns connectors
			(for [ycoin (range -1 arrYLen)]
				(otherfunction
				(for [xcoin (range arrXWid)]
				(when (neigbhourtoexistence? arr xcoin ycoin :buildingcolumnconnects) 
					(color [(rand) 1 1 1] (function
						(putupapost arr xcoin       ycoin :tr :callfromthisone :makingcolumns :buildedges)
						(putupapost arr xcoin (inc ycoin) :br :callfrombelow   :makingcolumns :buildedges)
						(putupapost arr xcoin       ycoin :tl :callfromthisone :makingcolumns :buildedges)
						(putupapost arr xcoin (inc ycoin) :bl :callfrombelow   :makingcolumns :buildedges)
						)
					)
					))))

			;Diagonal connectors
			(for [ycoin (range -1 arrYLen)]
				(otherfunction
				(for [xcoin (range -1 arrXWid)]
				(when (neigbhourtoexistence? arr xcoin ycoin :buildingdiagonalsconnects)
					(color [1 1 (rand) 1] (function
						(putupapost arr xcoin       ycoin       :tr :callfromthisone :makingdiag :buildedges)
						(putupapost arr xcoin       (inc ycoin) :br :callfrombelow   :makingdiag :buildedges)
						(putupapost arr (inc xcoin) ycoin       :tl :callfromleft    :makingdiag :buildedges)
						(putupapost arr (inc xcoin) (inc ycoin) :bl :callfromleftbelow :makingdiag :buildedges)
						)
					)

					))))

			))))

(defn findnewvec [[x1 y1 z1] [x2 y2 z2]]
	"Simple function to find vector between two points."
	[(- x2 x1) (- y2 y1) (- z2 z1)]
	)

(defn curveitbaby [arr]
	(vec(for [ycoin (range arrYLen)]
		(vec (for [xcoin (range arrXWid)]
			(let [
				pntData (retr arr xcoin ycoin)
				xval		(:xcoord pntData)
				yval  		(:ycoord pntData)
				cpntP 		(:cpntPos pntData)
				cpntV 		(:cpntVec pntData)
				cpntA 		(:cpntAng pntData)
				

				newPos 		[ (cpntP 0) (cpntP 1) (* (sqrt (+ (expt (cpntP 0) 2) (expt (cpntP 1) 2))) 0.1) ]
				focuspnt	[ 30 0 150]

				newVec 		(findnewvec  [(newPos 0) (newPos 1) (newPos 2)] focuspnt )
				]
				

				{:xcoord xval, 
				 :ycoord yval,
				 :cpntPos newPos, 
				 :cpntVec newVec,
				 :cpntAng cpntA}

		)

	)))))


(defn centrearray [arr]
	(let [
		xcoords  (for [ycoin arr pntData ycoin] 
					((pntData :cpntPos) 0) 
					)
		ycoords  (for [ycoin arr pntData ycoin] 
					((pntData :cpntPos) 1) 
					)
		minx 	(apply min xcoords)
		miny 	(apply min ycoords)
		maxx 	(apply max xcoords)
		maxy 	(apply max ycoords)

		halfrangex (/ (- maxx minx) 2)
		halfrangey (/ (- maxy miny) 2)]

		
	(vec(for [ycoin (range arrYLen)]
		(vec (for [xcoin (range arrXWid)]
			(let [
				pntData (retr arr xcoin ycoin)
				xval		(:xcoord pntData) ; coordinate in array, not coord in 3d
				yval  		(:ycoord pntData)
				cpntP 		(:cpntPos pntData)
				cpntV 		(:cpntVec pntData)
				cpntA 		(:cpntAng pntData)
				]
				{:xcoord xval, 
				 :ycoord yval,
				 :cpntPos [(+ (- (cpntP 0) maxx) halfrangex)
				           (+ (- (cpntP 1) maxy) halfrangey) 
				           (cpntP 2)] , 
				 :cpntVec cpntV,  
				 :cpntAng cpntA}


				)

			))))

	

	)

	)

(defn apply3dequation [arr fxy fpartialx fpartialy xmulti ymulti zmulti]
	"this is a general function. It takes fxy and makes z into fxy. 
	fxy uses xmulti and ymulti to change the scale of the 3d plot. 
	It does not change the actual coordinates as this would interfere
	with switch placement. zmulti is applied to the z coordinates 
	after the function has been applied to get the scale correct.
	fpartialx is the partial derivative of fxy with respect to x.
	fpartialy is the partial derivative of fxy with respect to y.

	The point of including the fpartials is to orientate the switches
	along the normal vector at that point in the equation so it looks 
	like a gradual curve.

	It returns an array like the other array transformationFunctions."
	(vec(for [ycoin (range arrYLen)]
		(vec (for [xcoin (range arrXWid)]
			(let [
				pntData (retr arr xcoin ycoin)
				xval		(:xcoord pntData)
				yval  		(:ycoord pntData)
				cpntP 		(:cpntPos pntData)
				cpntV 		(:cpntVec pntData)
				cpntA 		(:cpntAng pntData)
				

				newPos 		[
							(cpntP 0) 
							(cpntP 1) 
							(+ (cpntP 2) (* (fxy (* (cpntP 0) xmulti) (* (cpntP 1) ymulti)) zmulti)) ]


				newVec 		[
							(- (* (fpartialx (* (cpntP 0) xmulti) (* (cpntP 1) ymulti)) zmulti) ) 
							(- (* (fpartialy (* (cpntP 1) ymulti) (* (cpntP 0) xmulti)) zmulti))
							1 ]
				]
				

				{:xcoord xval, 
				 :ycoord yval,
				 :cpntPos newPos, 
				 :cpntVec newVec,
				 :cpntAng cpntA}

		)

	))))
	)

(defn gradualcurve [arr rowangle columnangle]
		"This curves the array gradually."
	(vec (for [ycoin (range arrYLen)]
		(vec (for [xcoin (range arrXWid)]

			(let [
				pntData (retr arr xcoin ycoin)
				xval		(:xcoord pntData)
				yval  		(:ycoord pntData)
				cpntP 		(:cpntPos pntData)
				cpntV 		(:cpntVec pntData)
				cpntA 		(:cpntAng pntData)
				xmod		(int (/ arrXWid 2))
				ymod		(int (/ arrYLen 2))

				betterxval  (- xval xmod)
				betteryval  (inc (- yval xmod))

				currentcolangle (* betteryval columnangle)
				currentrowangle (* betterxval columnangle)
				 		

				rowrotated	[
							(cpntP 0) 
							(- (* (cpntP 1) (Math/cos currentcolangle))  (* (cpntP 2) (Math/sin currentcolangle)) ) 
							(* (+ (* (cpntP 1) (Math/sin currentcolangle))  (* (cpntP 2) (Math/cos currentcolangle)) ) 1)
							]

				fullyrotated [
							(- (* (rowrotated 0) (Math/cos currentrowangle))  (* (rowrotated 2) (Math/sin currentrowangle)) ) 
							(rowrotated 1)
							(+ (* (rowrotated 2) (Math/cos currentrowangle))  (* (rowrotated 0) (Math/sin currentrowangle)) ) 

							]

				newVec 		(findnewvec fullyrotated [0 0 1] ) 
				]
				
				(prn betteryval)
				{:xcoord xval, 
				 :ycoord yval,
				 :cpntPos fullyrotated, 
				 :cpntVec newVec,
				 :cpntAng cpntA}



			))))))




(defn newcurveitbaby [arr]
	(apply3dequation
		arr
		(partial  #(- (sqrt (- 1000 (expt %1 2) (expt %2 2)))))
		(partial  #(/ %1 (sqrt (- 1000 (expt %1 2) (expt %2 2)))))
		(partial  #(/ %1 (sqrt (- 1000 (expt %1 2) (expt %2 2)))))
		0.25
		0.2
		5

		)

	)

(defn curvexaxisy [arr]
	(let [
		arguments [arr (partial #(* (+ (* (expt %1 2) 0.25) ( expt %2 2) ) 0.008))

					
					(partial #(+ (* %1 2 0.008 0.25)  (* %2 0) ))
					(partial #(+ (* %1 2 0.008)     (* %2 0) ))	
					
					1
					1
					1 ]]

	(apply apply3dequation arguments)


		))


(defn showfunction [fxy xrange yrange]
	(for [x (range (- xrange) xrange) y (range (- yrange) yrange)] (
		
		(spit "things/post-demo.scad"
      		(point x y (fxy x y) )) :append true)
		

		)
	)

(defn shouldthesekeysexist?youbethejudge [arr]
	(let [existencearray
					 [
					[false true true true true true true true] 
					[false true true true true true true true] 
					[false true true true true true true true] 
					[true true true true true true true true] 
					[true true true true true true true true] ;as seem from origin looking in pos x, pos y

					] ]
		(vec (for [ycoin (range arrYLen)]
			(vec (for [xcoin (range arrXWid)]
				(assoc (retr arr xcoin ycoin) :existence (get-in existencearray [(- (dec arrYLen) ycoin) xcoin]))
		)))))
	)

(defn alignkeys [arr & more]
	(let [
		vecofkeys 		(more 0)
		anchorkey		(retr arr ((vecofkeys 0) 0) ((vecofkeys 0) 1))
		anchkeypos		(anchorkey :cpntPos)
		anchkeyvec		(anchorkey :cpntVec)
		anchkeyang		(anchorkey :cpntAng)

		u 				(unitv anchkeyvec)
		a 				(u 0)
		b 				(u 1)
		c 				(u 2)
		d 				(modofvec [0 b c])

		ConD 			(/ c d)
		BonD 			(/ b d)
		


		])

	)

(defn transformationFunctions [arr & more]
	"these are the functions that rewrite the array"
	(-> 
		(centrearray arr)
		(moveonXYplane -2 0 -6 :row 3)
		(moveonXYplane 0 0 -4 :row 4)
		(moveonXYplane 0 0 +0 :row 5)
		(moveonXYplane 0 0 +2 :row 6)
		
		(curvexaxisy)
		;(alignkeys [[0 0] [1 0] :ontheleft])
		;(curvexaxisx)
		(shouldthesekeysexist?youbethejudge)

		;(gradualcurve (/ (Math/PI) 6) (/ (Math/PI) 36))
		;(newcurveitbaby)
		)
	)

(defn doesnttoucharrayFunctions [arr & more]
	"These functions only read the array hence the arr parameter being 
	passed to each of them individually. Unlike the threading of the transformationFunctions"
	(union 
		;(putsquareinarr arr)

		(makeconnectors arr :plate)
		(translate [0 0 -30] (makeconnectors arr :base))
		;(showkeycaps arr)
		(showconnectors arr)
		)
	)

(defn buildarray []
		 (-> (createarray arrXWid arrYLen)
		 	 (transformationFunctions)
		 	 (doesnttoucharrayFunctions) ;the outcome of this should be code for scad-clj
		 	 )

		 
	)

(spit "things/post-demo.scad"
      (write-scad (rotate 0.3 [1 0 0] (buildarray ))) :append true)