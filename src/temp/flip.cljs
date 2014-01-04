(ns geometry.flip
  (:require [geometry.complex :as c]))

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
