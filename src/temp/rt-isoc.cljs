(ns geomery.rt-isoc
  (:require [geometry.complex :as c]
            [geometry.color :as color]))

(defn log
  "put this in a common place TODO"
  [s]
  (.log js/console s))

(def color-map (color/hsla-hue-range 4))

(def center (c/make-rect 200 200))
(def radius 100)
(def b1 (c/one))
(def b2 (c/i))

(defn flip-right-isoceles
  "flip right tri in given side"
  [base side]
  (let [[v0 v1 v2] base]
    (condp = side
      :s1 [v0 v1 (c/minus v2)]
      :s2 [v0 (c/minus v1) v2]
      :s3 [(c/plus v1 v2) (c/minus v2) (c/minus v1)])))

(comment
  (def base [c/zero c/one c/i])
  (flip-right-isoceles base :s1)
  (flip-right-isoceles base :s2)
  (mapv str (flip-right-isoceles base :s3))
  )
