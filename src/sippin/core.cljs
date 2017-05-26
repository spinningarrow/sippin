(ns sippin.core
    (:require [reagent.core :as reagent]))

;; -------------------------
;; State
(def app-db (reagent/atom {:rooms {:#first-room []
                                   :#second-room []
                                   :#third-room []}
                           :current-input ""
                           :active-room :#first-room}))

;; -------------------------
;; Constructors
(defn message
  [content]
  {:content content
   :time (.toISOString (js/Date.))})


;; -------------------------
;; Views

(defn input
  []
  [:form {:on-submit (fn [event]
                       (.preventDefault event)
                       (.log js/console (str @app-db))
                       (swap! app-db
                              update-in
                              [:rooms (get @app-db :active-room)]
                              #(conj %1 (message (get @app-db :current-input))))
                       (swap! app-db assoc :current-input ""))}
   [:input#message {:auto-focus "autofocus"
                    :placeholder "Enter some stuff here if you can!"
                    :value (get @app-db :current-input)
                    :on-change #(swap! app-db
                                       assoc :current-input (-> %
                                                                .-target
                                                                .-value))}]
   [:button "Send"]])

(defn messages
  []
  [:ul
   (for [message (get-in @app-db [:rooms (get @app-db :active-room)])]
     ^{:key (get message :time)} [:li (get message :content)])])

(defn rooms
  []
  [:ul
   (for [room (keys (get @app-db :rooms))]
     ^{:key room} [:li [:a {:href "#"
                            :on-click (fn [event]
                                        (.preventDefault event)
                                        (swap! app-db assoc :active-room room))}
                        room]])])

(defn home-page []
  [:div
   [input]
   [messages]
   [rooms]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
