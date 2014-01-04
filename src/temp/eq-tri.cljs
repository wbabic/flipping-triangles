(ns geometry.eq-tri
  (:require [geometry.complex :as c]
            [geometry.color :as color]
            [geometry.draw :as draw]
            [geometry.async-utils :as a]
            [cljs.core.async :as async :refer [chan <! >! put! alts! close! timeout]])
  (:require-macros
   [cljs.core.async.macros :refer [go alt!]]))

(defn log
  "put this in a common place TODO"
  [s]
  (.log js/console s))

(def color-map (color/hsla-hue-range 4))

(def center (c/make-rect 200 200))
(def radius 100)
(def b1 (c/times c/one radius))
(def b2 (c/times (c/unit-polar (/ 1 3)) radius))
(def b3 (c/times (c/unit-polar (/ 2 3)) radius))

(defn make-eq-tri
  [center base]
  {:center center
   :base base})

(defn reflect-eq-tri
  "return triangle reflected in given side
where side is one of :s1 :s2 :s3"
  [tri side]
  (let [[b1 b2 b3] (:base tri)
        c (:center tri)]
    (condp = side
      :s1 (make-eq-tri (c/plus c (c/plus b1 b2)) [(c/minus b2) (c/minus b1) (c/minus b3)])
      :s2 (make-eq-tri (c/plus c (c/plus b2 b3)) [(c/minus b1) (c/minus b3) (c/minus b2)])
      :s3 (make-eq-tri (c/plus c (c/plus b3 b1)) [(c/minus b3) (c/minus b2) (c/minus b1)]))))

(defn surface
  "TODO put in a common place"
  [id]
  (let [surface (.getElementById js/document id)]
    [(.getContext surface "2d")
     (. surface -width)
     (. surface -height)]))

(defn apply-op [op tri]
  (let [[type arg] op]
    (condp = type
      :flip (reflect-eq-tri tri arg))))

(defn app-loop
  "listen for events on in and draw to canvas
maintain current triangle in local state and apply ops to it
replace current with result"
  [in first canvas]
  (go (loop [next first]
        (draw/draw-eq-tri next color-map canvas)
        (let [op (<! in)
              _ (log (str "in: " op))
              current (apply-op op next)]
          (recur current)))))

(defn fill
  "fill canvas with reflections of 
equilateral triangle"
  [id]
  
  (def repl-in (chan))
  (def canvas (surface "canvas"))
  (def tri (make-eq-tri center [b1 b2 b3]))
  (app-loop repl-in tri canvas)

  (go
   (<! (timeout 1500))
   (>! repl-in [:flip :s1])
   (<! (timeout 1000))
   (>! repl-in [:flip :s2])
   (<! (timeout 750))
   (>! repl-in [:flip :s1])
   (<! (timeout 500))
   (>! repl-in [:flip :s2])
   (<! (timeout 500))
   (>! repl-in [:flip :s1])
   (<! (timeout 250))
   (>! repl-in [:flip :s3])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])
   (<! (timeout 250))
   (>! repl-in [:flip :s3])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])
   (<! (timeout 250))
   (>! repl-in [:flip :s1])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])
   (<! (timeout 250))
   (>! repl-in [:flip :s3])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])
   (<! (timeout 250))
   (>! repl-in [:flip :s3])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])
   (<! (timeout 250))
   (>! repl-in [:flip :s1])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])
   (<! (timeout 250))
   (>! repl-in [:flip :s1])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])
   (<! (timeout 250))
   (>! repl-in [:flip :s3])
   (<! (timeout 250))
   (>! repl-in [:flip :s2])))

(comment
  (fill "canvas")
  )
