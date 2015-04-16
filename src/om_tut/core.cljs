(ns ^:figwheel-always om-tut.core
  (:require[om.core :as om :include-macros true]
           [sablono.core :refer-macros [html]]
           [alandipert.storage-atom :refer [local-storage]]
           ))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

;;(defonce app-state (atom {:list ["Red" "Yellow" "Green" "Blue" "Orange"]}))

(defonce app-state
  (local-storage
   (atom
    {:todos
     [{:text "A must to do" :done true}
      {:text "Either do or not" :done false}
      {:text "Dont do it" :done false}
      ]})
   :todos-app-state))


(defn todo-item
  [todo owner]
  (om/component
   (let [cls (if (:done todo) "done" "")
         toggle (fn [todo] (update todo :done not))]
     (html
      [:li {:class "todo"}
       [:input {:type "checkbox" :class "toggle" :checked (:done todo)
                :on-click #(om/transact! todo toggle)}]
       [:span {:class cls} (:text todo)]
       ]
      )
     )
   )
  )

(defn add-todo [text todos]
  (conj todos {:text text :done false})
  )

(defn todo-adder [todos owner]
  (om/component
   (html
    [:input
     {:type "text"
      :class "do-btn"
      :placeholder "What needs doing?"
      :on-key-up (fn [event]
                   (let [input (.-target event)]
                     (when (= 13 (.-keyCode event)) ;; ENTER
                       (om/transact! todos
                                     (partial add-todo (.-value input)))
                       (set! (.-value input) ""))))}])))

(defn count-todo [todos owner]
  (om/component
   (html
    [:div {:class "items-wrap"}
     [:div {:class "items-done"} "items done:" (count (filter :done todos))]
     [:div {:class "items-left"} "items left:" (count (remove :done todos))]
     ]
    )
   )
  )

(defn clear-todo-button [todos]
  (vec(remove :done todos))
  )

(defn clear-todo [todos owner]
  (om/component
   (html
    [:input {:type "button"
             :class (if (zero? (count (filter :done todos))) "hidden" "clear-btn")
             :value "Clear Completed"
             :on-click #(om/transact! todos clear-todo-button)
             }]
    )
   )
  )

(defn add-filters [todos owner]
  (om/component
   (html
    [:div {:class "filter-wrapper"}
     [:ul
      [:li
       [:a {:href "#"} "All"]]
      [:li
       [:a {:href "#"} "Active"]]
      [:li
       [:a {:href "#"} "Completed"]
       ]
      ]
     ]
    )
   )
  )

(defn todo-list
  [data owner]
  (om/component
   (html
    [:div {:class "wrapper"}
     [:h1 "Things to be done"]
     [:ul
      (om/build-all todo-item (:todos data))
      ]
     (om/build todo-adder (:todos data))
     (om/build count-todo (:todos data))
     (om/build clear-todo (:todos data))
     (om/build add-filters (:todos data))
     ]
    )
   ))

;;(defn colors-view
;;  [data owner]
;; (om/component
;; (html
;; [:div {:class "wrapper"}
;; [:h1 "List of Colors"]
;;[:ul {:class "colors-list"}
;;(map (fn[colors] [:li [:a {:href "#"} colors]]) (:list data))
;;]])))

;; (om/root colors-view
;;       app-state
;;         {:target (. js/document (getElementById "app0"))})


(om/root todo-list
         app-state
         {:target (. js/document (getElementById "todos"))})




