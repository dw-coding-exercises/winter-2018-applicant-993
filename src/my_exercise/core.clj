(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [my-exercise.home :as home]))

(defroutes app
  (GET "/" [] home/page)
  (POST "/search" {:params :params }
    (GET (api-call [(params)]) [])
    (GET "/results" [] "Hello")
  )
  (route/resources "/")
  (route/not-found "Not found"))

(def handler
  (-> app
      (wrap-defaults site-defaults)
      wrap-reload))

(defn ocd-id-us [] "ocd-division/country:us" )
(defn ocd-id-state [state] (str (ocd-id-us) "/state:" (clojure.string/lower-case state) ) )
(defn ocd-id-place [city, state] (str (ocd-id-us) (ocd-id-state [state]) "/place:" (clojure.string/lower-case (replace city #" " "_") ) ) )
(defn query-string [params]
  (cond
    (and (params :city) (params :state)) (ocd-id-place) [(params :city), (params :state)]
    (params :state) (ocd-id-state) [(params :state)]
    :else (ocd-id-us)
  )
)
(defn api-call [params]
  (GET (str "https://api.turbovote.org/elections/upcoming?district-divisions" (query-string [params])) [params])
)
