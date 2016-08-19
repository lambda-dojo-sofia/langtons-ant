(ns langtons-ant-cljs.core
  (:require [compojure.core           :refer :all]
            [compojure.route          :as route]
            [hiccup.core              :refer [html]]
            [ring.adapter.jetty       :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload   :refer [wrap-reload]])
  (:gen-class))

(def layout
  (html [:html {:lang "en"}
         [:head
          [:title "Langton's ant - CLJS"]
          [:link {:rel  "stylesheet"
                  :href "/css/ant.css"}]]
         [:body
          [:div#ant]
          [:script {:src  "/js/ant.js"
                    :type "text/javascript"}]]]))

(defroutes app-routes
  (GET "/" [] layout)
  (route/resources "/")
  (route/not-found "Nope"))

(def app
  (-> #'app-routes
      wrap-reload
      (wrap-defaults site-defaults)))

(defn -main []
  (run-jetty #'app {:port 3000}))
