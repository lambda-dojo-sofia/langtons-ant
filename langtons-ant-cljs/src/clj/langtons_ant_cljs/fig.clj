(ns langtons-ant-cljs.fig
  (:require [ring.middleware.resource :refer (wrap-resource)]))

(defn handler [request]
  {:status  404
   :headers {"Content-Type" "text/html"}
   :body    (str "Cannot find:" (:uri request))})

(def figwheel-resources
  (-> handler
      (wrap-resource "public/js/out")))
