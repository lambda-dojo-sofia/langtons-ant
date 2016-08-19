(ns langtons-ant-cljs.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop alt!]])
  (:require [clojure.set :refer [map-invert]]
            [reagent.core :as reagent]
            [cljs.core.async :refer [put! chan <! timeout]]
            [goog.string :refer [format]]
            [goog.string.format]
            [figwheel.client :as fw :include-macros true]))

;;This is noise
(defn ^:export init! []
  (enable-console-print!)
  (fw/watch-and-reload
   :websocket-url "ws://localhost:3449/figwheel-ws"
   :jsload-callback #(prn "JS Reloaded")))

(init!)
;;end the noise...

(def clockwise-directions {:down  :left
                           :right :down
                           :top   :right
                           :left  :top})

(defonce control-ch (atom (chan)))

(defonce game-parameters (reagent/atom {:dimension 20}))
(defonce game-board (reagent/atom []))
(defonce ant (reagent/atom {:x 9 :y 9 :direction :left}))

(defn draw-cell [x cell-state y]
  (let [cell      (if cell-state "black" "")
        ant?      (and (= x (:x @ant))
                       (= y (:y @ant)))
        direction (if ant? (-> @ant :direction name) "")
        ant       (if ant? "ant" "")]
    ^{:key x}
    [:td.langtons-cell {:class (format "%s %s %s" cell direction ant)}]))

(defn draw-board-row [y board-row]
  ^{:key y}
  [:tr
   (doall
    (map-indexed #(draw-cell %1 %2 y) board-row))])

(defn rotate-ant! []
  (let [status (get-in @game-board [(:y @ant) (:x @ant)])
        current-direction (:direction @ant)
        directions (if status
                     (map-invert clockwise-directions)
                     clockwise-directions)]
    (swap! ant assoc :direction (-> directions current-direction))))

(defn cell-status! []
  (swap! game-board update-in [(:y @ant) (:x @ant)] not))

(defn move-forward! []
  (case (:direction @ant)
    :left  (swap! ant update-in [:x] dec)
    :top   (swap! ant update-in [:y] dec)
    :down  (swap! ant update-in [:y] inc)
    :right (swap! ant update-in [:x] inc))
  true)

(defn move-ant! []
  (rotate-ant!)
  (cell-status!)
  (move-forward!))

(defn start-ant! []
  (reset! control-ch (chan))
  (go-loop []
    (let [result (alt!
                   (timeout 500) ([_] (when-not (move-ant!)
                                        (put! @control-ch true)))
                   @control-ch :done)]
      (when (not= result :done)
        (recur)))))

(defn stop-ant! []
  (put! @control-ch true))

(defn reset-board! []
  (let [board-size (-> @game-parameters
                       :dimension
                       js/parseInt)]
    (stop-ant!)
    (reset! game-board (vec (repeat board-size
                                    (vec (repeat board-size false)))))
    (reset! ant {:y (/ board-size 2) :x (/ board-size 2) :direction :left})))

(def langtons-ant
  (with-meta
    (fn []
      [:div.langtons-game
       [:form
        [:input {:type      "text"
                 :value (:dimension @game-parameters)
                 :on-change #(swap! game-parameters assoc :dimension (.-value (.-target %)))}]
        [:button {:on-click #(do
                               (.preventDefault %)
                               (reset-board!))} "Reset"]
        [:button {:on-click #(do
                               (.preventDefault %)
                               (start-ant!))} "Start"]
        [:button {:on-click #(do
                               (.preventDefault %)
                               (.stopPropagation %)
                               (stop-ant!))} "Stop"]]
       [:table#langtons-ant
        (doall
         (map-indexed draw-board-row @game-board))]])
    {:component-did-mount reset-board!}))

(reagent/render-component [langtons-ant]
                          (. js/document (getElementById "ant")))
