(ns optcase.core
  (:gen-class :main true)
  (:refer-clojure :exclude [use import])
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]
            [unicode-math.core :refer :all]
            [clojure.math.numeric-tower :refer :all]))

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

(def keywidthForSpacing 	14.00)
(def keySpacing 			5.05)
(def arrXWid				2 )
(def arrYLen				2 )

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


;(prn keyPlateArr)

(defn retr [arr x y]

	((arr y) x)

	)

;(prn (retr 0 0))


(defn putsquareinarr [arr & more]
	(for [ycoin (range arrYLen) xcoin (range arrXWid)]

		(let [
			pntData 	(retr arr xcoin ycoin)
			cpntP 		(:cpntPos pntData)
			cpntV 		(:cpntVec pntData)
			cpntA 		(:cpntAng pntData)

			]
			;(println pntData)
			
			(attach [cpntP cpntV cpntA] [[0 0 0] [0 0 1] 0] (cube 14 14 1))
			
		)
	))

(defn moveonXYplane [arr & more]
	(vec(for [ycoin (range arrYLen)]
		(vec (for [xcoin (range arrXWid)]
			(let [pntData (retr arr xcoin ycoin)]
				(update-in pntData [:cpntPos 0] #(+ %1 50))
				)

			)
		))
	)
	)

;(prn (moveonXYplane (createarray arrXWid arrYLen)))


; (defn fOnEachPnt [arr & more] ;takes the array and a list of functions called more. applies each of those functions to each point
; 	(let [
; 		xmax 		(count (first arr))
; 		ymax 		(count arr)
; 		]
; 		(for [ycoin (range ymax) xcoin (range xmax)]
; 			(for [f more]
; 				(f (retr xcoin ycoin) xcoin ycoin)
; 				)
; 		))
; 	)

(defn makepostatpoint [pntData relpos] ;point data and relpas (topleft bottomleft topright bottomright)
	(let [ ;attach it
			cpntP 		(:cpntPos pntData)
			cpntV 		(:cpntVec pntData)
			cpntA 		(:cpntAng pntData)
			
			]
	(->>

		(cube 2 2 6) ;make post

		((case relpos ;find which corner and apply transformation
						"topleft"    (partial translate [-7  7 0])
						"topright"   (partial translate [ 7  7 0])
						"bottleft"   (partial translate [-7 -7 0])
						"bottright"  (partial translate [ 7 -7 0])
						))

		(attach [cpntP cpntV cpntA] [[0 0 0] [0 0 1] 0]) 
		
		)))

(defn lastoneorinc? [v xory]


	(if (< v (- xory 1)) (inc v) (* 1 v) )

	)



(defn makeandhullposts [arr & more]
	(for [ycoin (range arrYLen) xcoin (range arrXWid)]

		(let [
			pntData 	(retr arr xcoin ycoin)
			pntDataRight(retr arr (lastoneorinc? xcoin arrXWid) ycoin)
			pntDataDown(retr arr xcoin (lastoneorinc? ycoin arrYLen))
			]

			(hull
			(makepostatpoint pntData "topleft")
			(makepostatpoint pntDataRight "topleft")
			(makepostatpoint pntDataDown "topleft")
			)
		)
	))

(defn transformationFunctions [arr & more]
	(-> (moveonXYplane arr) ;thread this one into others
		
		)
	)

(defn doesnttoucharrayFunctions [arr & more]
	(union 
		(putsquareinarr arr) ;thread this one into others.
		(makeandhullposts arr)
		)
	)

(defn messingaround []
		 (-> (createarray arrXWid arrYLen)
		 	 (transformationFunctions)
		 	 (doesnttoucharrayFunctions) ;the outcome of this should be code for scad-clj
		 	 )

		 
	)

(spit "things/post-demo.scad"
      (write-scad (messingaround )))