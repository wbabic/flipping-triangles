(ns geometry.draw
  (:require [geometry.complex :as complex]
            [geometry.color :as color]))

(defn log [s]
  (.log js/console (str s)))

(defn draw-vertex [surface vertex size color]
  (.beginPath surface)
  (.arc surface (complex/re vertex) (complex/im vertex) size 0 (* 2 Math/PI) true)
  (.stroke surface)
  (set! (. surface -fillStyle) (color/hsla->str color))
  (.fill surface)
  (. surface (closePath)))

(defn draw-vertices [surface polygon]
  (let [vertices (:vertices polygon)
        colors (:colors polygon)
        n (count vertices)]
    (doseq [i (range n)]
      (draw-vertex surface (vertices i) 1 (colors i)))))

(defn draw-halfturn [canvas halfturn]
  (let [[surface _ _] canvas
        center (:center halfturn)
        color (:color halfturn)]
    (draw-vertex surface center 6 color)))

(defn draw-sub-face [surface center v1 v2 color]
  (.beginPath surface)
  (.moveTo surface (complex/re center) (complex/im center))
  (.lineTo surface (complex/re v1) (complex/im v1))
  (.lineTo surface (complex/re v2) (complex/im v2))
  (set! (. surface -fillStyle) (color/hsla->str color))
  (.fill surface)    
  (. surface (closePath)))

(defn draw-sub-faces [surface poly]
  (let [vertices (:vertices poly)
        colors (:colors poly)
        center (:center poly)
        highlight (:highlight poly)
        n (count vertices)
        sub-faces (vec (take n (partition 2 1 (cycle vertices))))]
    (doseq [i (range n)]
      (let [[v1 v2] (sub-faces i)
            color (colors i)
            color (if highlight
                    (color/highlight-color color)
                    color)]
        (draw-sub-face surface
                       center
                       v1
                       v2
                       color)))))

(defn fill-rect [canvas]
  (let [[surface w h] canvas]
  (set! (. surface -fillStyle) (str "rgb(256,256,256)"))
  (.fillRect surface 0 0 w h)))

(defn draw-line [line canvas]
  (let [p1 (:p1 line)
        p2 (:p2 line)
        [surface w h] canvas]
    (set! (. surface -strokeStyle) "rgba(20,20,20,0.5)")
    (set! (. surface -lineWidth) 1)
    (.beginPath surface)
    (.moveTo surface (complex/re p1) (complex/im p1))
    (.lineTo surface (complex/re p2) (complex/im p2))
    (.stroke surface)    
    (. surface (closePath))))

(defn ^:export draw-poly
  "draw polygon in context"
  [polygon surface]
  (let [[canvas width height] surface]
    (set! (. surface -strokeStyle) "rgb(20,20,20)")
    (set! (. surface -lineWidth) 1)
    (draw-sub-faces canvas polygon)
    ;(draw-vertices canvas polygon)
    ))

;; used in eq-tri
(defn vertices [tri]
  (let [c (:center tri)]
    (mapv (complex/add-by c) (:base tri))))

(defn midpoints [vertices]
  (let [n (count vertices)]
    (mapv (partial apply complex/midpoint)
          (take n (partition 2 (cycle vertices))))))

(defn colors [color-map]
  (mapv color/hsla->str color-map))

(defn draw-sub-tri
  [tri color canvas]
  (let [[surface width height] canvas
        [v1 v2 v3] tri]
    (set! (. surface -strokeStyle) "rgb(20,20,20)")
    (set! (. surface -fillStyle) color)
    (set! (. surface -lineWidth) 1)
    (.beginPath surface)
    (.moveTo surface (complex/re v1) (complex/im v1))
    (.lineTo surface (complex/re v2) (complex/im v2))
    (.lineTo surface (complex/re v3) (complex/im v3))
    (.fill surface)    
    (. surface (closePath))))

(defn draw-eq-tri
  "draw equilateral triangle with color model in canvas"
  [tri color-map canvas]
  (let [[v1 v2 v3] (vertices tri)
        [m1 m2 m3] (midpoints [v1 v2 v3])
        [c1 c2 c3 c4] (colors color-map)]
    (draw-sub-tri [v1 m1 m2] c1 canvas)
    (draw-sub-tri [v2 m3 m1] c2 canvas)
    (draw-sub-tri [v3 m3 m2] c3 canvas)
    (draw-sub-tri [m1 m2 m3] c4 canvas)))

;; used in rt-scalene-1
(defn rt-scalene-vertices
  [tri]
  (let [[c b m] tri
        t (complex/add-by c) [b m]]
    [c (t b) (t m)]))

(defn draw-rt-scalene
  "draw right scalene triangle with comlor model in canvas
tri is in the form [center basis midpoint]"
  [tri color-map canvas]
  (let [[v1 v2 v3] (rt-scalene-vertices tri)
        [c1 c2 c3 c4] (colors color-map)
        [m1 m2 m3] (midpoints [v1 v2 v3])]
    (draw-sub-tri [v1 m1 m2] c1 canvas)
    (draw-sub-tri [v2 m3 m1] c2 canvas)
    (draw-sub-tri [v3 m3 m2] c3 canvas)
    (draw-sub-tri [m1 m2 m3] c4 canvas)))
