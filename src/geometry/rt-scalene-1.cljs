(ns geomery.rt-scalene
  (:require [geometry.complex :as c]
            [geometry.color :as color]
            [geometry.draw :as draw]
            [goog.dom :as dom]
            [goog.events :as events]
            [cljs.core.async :as async :refer [chan <! >! put! alts! close! timeout]])
  (:require-macros
   [cljs.core.async.macros :refer [go alt!]]))

(defn surface
  "TODO put in a common place"
  [id]
  (let [surface (.getElementById js/document id)]
    [(.getContext surface "2d")
     (. surface -width)
     (. surface -height)]))

(defn log
  "TODO put this in a common place"
  [s]
  (.log js/console s))

(def color-map (color/hsla-hue-range 4))

(def center (c/make-rect 200 200))
(def radius 200)
(def b1 (c/times c/one radius))
(def b2 (c/times (c/unit-polar (/ 1 3)) radius))
(def b3 (c/times (c/unit-polar (/ 2 3)) radius))

(def m12 (c/midpoint b1 b2))
(def m23 (c/midpoint b2 b3))
(def m31 (c/midpoint b3 b1))

(def rt1 [center b1 m12])
(def rt2 [center b2 m12])
(def rt3 [center b2 m23])
(def rt4 [center b3 m23])
(def center-1 (c/make-rect 300 200))
(def rt4-1 [center-1 b3 m23])
(def center-2 (c/make-rect 400 370))
(def rt4-2 [center-2 b3 m23])
(def rt5 [center b3 m31])
(def rt6 [center b1 m31])

(defn new-m [b m]
  (c/plus (c/times b (/ 1 2)) (c/minus m)))

(defn reflect-rt-scalene-1
  "return reflected rt-scalene-1 in given side
where side is one of :s1 :s2 :s3"
  [tri side]
  (let [[c b m] tri]
    (condp = side
      :s1 [c b (new-m b m)]
      :s3 [(c/plus c (c/times m 2)) (c/plus b (c/times m -2)) (c/minus m)]
      :s2 [c (c/plus (c/times m 2) (c/minus b)) m])))

(def canvas (surface "canvas"))

(defn flip [tri side]
  (reflect-rt-scalene-1 tri side))

(defn app-loop
  "listen for events on given chanel 'in' and draw to canvas
maintain current triangle in local state and apply ops to it
replace current with result"
  [in canvas]
  (go (loop [next nil]
        (when next (draw/draw-rt-scalene next color-map canvas))
        (let [op (<! in)
              _ (log (str "in: " op))
              [type arg] op]
          (condp = type
            :flip (recur (flip next arg))
            :reset (do
                     (draw/fill-rect canvas)
                     (recur nil))
            :set (do
                   (draw/fill-rect canvas)
                   (recur arg)))))))

(defn flip-seq [chan seq t tri]
  (go
   (>! chan [:set tri])
   (doseq [side seq]
     (<! (timeout t))
     (>! chan [:flip side]))))

(def repl-in (chan))
(def canvas (surface "canvas"))
(def fundamental (surface "fundamental"))

(draw/draw-rt-scalene rt4 color-map fundamental)

(defn listen [el type name]
  (let [out (chan)]
    (events/listen el
                   type
                   (fn [e] (put! out name)))
    out))

(defn set-class [el name]
  (set! (.-className el) name))

(defn clear-class [el name]
  (set! (.-className el) ""))

(defn html-in []
  (let [c (chan)
        d-el (dom/getElement "diamond")
        t-el (dom/getElement "triangle")
        h-el (dom/getElement "hexagon")
        diamond (listen d-el "click" :diamond)
        triangle (listen t-el "click" :triangle)
        hexagon (listen h-el "click" :hexagon)]
    (go (while true
          (let [[v ch] (alts! [diamond triangle hexagon])]
            (condp = ch
              diamond (do
                        (set-class d-el "active list-group-item")
                        (set-class t-el "list-group-item")
                        (set-class h-el "list-group-item"))
              triangle (do
                         (set-class d-el "list-group-item")
                         (set-class t-el "active list-group-item")
                         (set-class h-el "list-group-item"))
              hexagon (do
                        (set-class d-el "list-group-item")
                        (set-class t-el "list-group-item")
                        (set-class h-el "active list-group-item")))
            (>! c v))))
    c))

(app-loop repl-in canvas)
(let [in (html-in)]
  (go (while true
        (let [c (<! in)]
          (.log js/console c)
          (condp = c
            :diamond (flip-seq repl-in [:s3 :s2 :s3] 750 rt4-1)
            :triangle (flip-seq repl-in [:s2 :s1 :s2 :s1 :s2] 500 rt4)
            :hexagon (flip-seq repl-in [:s3 :s1 :s3 :s1 :s3 :s1 :s3 :s1 :s3 :s1 :s3] 250 rt4-2))))))

(comment
  (app-loop repl-in canvas)
  (flip-seq repl-in [:s3 :s2 :s3] 1000 rt4)
  (go (>! repl-in [:reset]))

  (flip-seq repl-in [:s2 :s1 :s2 :s1 :s2] 1000 rt4)
  (go (>! repl-in [:reset]))


  )
