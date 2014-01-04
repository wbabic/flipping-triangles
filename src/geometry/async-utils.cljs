(ns geometry.async-utils
  (:require [cljs.core.async :as async :refer [chan <! >! put! alts! close!]])
  (:require-macros
   [cljs.core.async.macros :refer [go alt!]]))

(defn event-chan
  [type]
  (let [c (chan)]
    (.addEventListener js/window type #(put! c %))
    c))

(defn keycode->command []
  (fn [e]
    (let [key (.-keyCode e)
          command (case key
                    80 :rho
                    83 :sigma
                    77 :match
                    78 :new
                    :unknown)]
      command)))

(defn map-chan [f in]
  (let [c (chan)]
    (go (loop []
          (if-let [v (<! in)]
            (do (>! c (f v))
                (recur))
            (close! c))))
    c))

(defn key-input-channel []
  (map-chan (keycode->command) (event-chan "keyup")))

(defn filter-chan [pred channel]
  (let [rc (chan)]
    (go (loop []
          (let [val (<! channel)]
            (if (pred val) (put! rc val))
            (recur))
          ))
    rc))

(defn merge-chans [& chans]
  (let [out (chan)]
    (go
     (loop []
       (put! out (first (alts! chans)))
       (recur)))
    out))
