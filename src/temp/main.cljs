(ns geometry.main
  (:require [geometry.geometry :as g]
            [geometry.complex :as c]
            [geometry.color :as color]
            [geometry.draw :as d]
            [geometry.async-utils :as a]
            [cljs.core.async :as async :refer [chan <! >! put! alts! close! timeout]])
  (:require-macros
   [cljs.core.async.macros :refer [go alt!]]))

(defn surface [id]
  (let [surface (.getElementById js/document id)]
    [(.getContext surface "2d")
     (. surface -width)
     (. surface -height)]))

(def s1
  {:name :s1
   :type :poly
   :base g/square
   :colors (color/hsla-saturation-range 240 4)
   :situation {:position [150 200]
               :size 10
               :orientation (/ c/tau 8)}})

(def s2
  {:name :s2
   :type :poly
   :base g/square
   :colors (color/hsla-saturation-range 240 4)
   :situation {:position [200 150]
               :size 10
               :orientation (/ c/tau 4)}})

(def ts1 (assoc s1
           :situation {:position [250 200]
                       :size 10
                       :orientation (* c/tau  (/ 7 8))}))

(def ts2 (assoc s1
           :situation {:position [250 100]
                       :size 10
                       :orientation (* c/tau  (/ 5 8))}))

(def ts3 (assoc s1
           :situation {:position [150 100]
                       :size 10
                       :orientation (* c/tau  (/ 3 8))}))

(defn make-halfturn [position name]
  (let [[x y] position]
    {:name name
     :type :halfturn
     :color (vector 360 0.50 0.65 0.8)
     :center (c/make-rect x y)}))

(def h1 (make-halfturn [200 200] :h1))

(def h2 (make-halfturn [250 150] :h2))

(def h3 (make-halfturn [200 100] :h3))

(def h4 (make-halfturn [150 150] :h4))

(defn connecting-line
  "return line from center of poly1 to center of poly2"
  [name poly1 poly2]
  (let [c1 (:center poly1)
        c2 (:center poly2)]
    {:name name
     :type :line
     :p1 c1
     :p2 c2}))

(defn update-state [old-state message]
  (conj old-state message))

(defn log [s]
  (.log js/console s))

(defn main-loop [start-state canvas in]
  (go
   (loop [state start-state]
     ;; draw current state
     ;; state is a vector of objects to be drawn
     ;; objects are geometric objects
     ;; in form to be rendered to the canvas
     (d/fill-rect canvas)
     (doseq [poly state]
       (let [type (:type poly)]
         (condp = type
           :line (d/draw-line poly canvas)
           :halfturn (d/draw-halfturn canvas poly)
           (d/draw-poly poly canvas))))
     ;; listen for messages
     (let [d (<! in)
           new-state (update-state state d)]
       ;; (log d)
       (recur new-state))
     )))

(defn app-loop [start-state canvas input]
  (main-loop start-state canvas input)
  :ok)

(def canvas (surface "canvas"))
(def repl-in (chan))
(def rs1 (g/render s1))
(def rs2 (g/render s2))
(def rts1 (g/render ts1))
(def rts2 (g/render ts2))
(def rts3 (g/render ts3))

(comment
  
  (app-loop [] canvas repl-in)

  (go
   (<! (timeout 1000))
   (>! repl-in rs2)
   (<! (timeout 1000))
   (>! repl-in rs1)
   (>! repl-in (connecting-line :l1 rs2 rs1))
   (<! (timeout 1000))
   (>! repl-in rts1)
   (>! repl-in (connecting-line :l2 rs2 rts1))

   (<! (timeout 1000))
   (>! repl-in rts2)
   (>! repl-in (connecting-line :l3 rs2 rts2))
   (<! (timeout 1000))
   (>! repl-in rts3)
   (>! repl-in (connecting-line :l4 rs2 rts3))
   
   (<! (timeout 1000))
   (>! repl-in (connecting-line :l5 rs1 rts1))
   (>! repl-in (connecting-line :l6 rts1 rts2))
   (>! repl-in (connecting-line :l7 rts2 rts3))
   (>! repl-in (connecting-line :l8 rts3 rs1))

   (<! (timeout 1000))
   (>! repl-in h1)
   (>! repl-in h2)
   (>! repl-in h3)
   (>! repl-in h4)

   (<! (timeout 1000))
   (>! repl-in (connecting-line :l9 h1 h3))
   (>! repl-in (connecting-line :l10 h2 h4))

   ))
