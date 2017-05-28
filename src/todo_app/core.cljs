(ns todo-app.core
    (:require [reagent.core :as reagent]))


;; -------------------------
;; State

; lets make each task a vector with two elements [id, task-string]
(def tasks (reagent/atom []))
(def input-task (reagent/atom ""))
(def id-counter (reagent/atom 0))
(def current-task (reagent/atom 0))

;; -------------------------
;; Views
(defn remove-task [index]
  (let [new-tasks (filter #(not= index (first %)) @tasks)]
    (reset! tasks new-tasks)))

(defn task-item [current index task]
  (let [real-id (first task)
        is-active (= real-id current)
        color (if is-active "red" "blue")
        dotted? (if is-active "solid" "dotted")]
    [:div {:style {:border-color color :border-style dotted? :border-width "2px" :margin-bottom "1px"}}
      index ": " (last task)
      [:div
        [:input {:type "button" :value "Set to active!" :on-click #(reset! current-task real-id)}]
        [:input {:type "button" :value "Remove" :on-click #(remove-task real-id)}]]]))

(defn render-tasks [current]
  [:div
    (for [task (zipmap (map inc (range)) @tasks)]
      ^{:key (first task)}(task-item current (first task) (last task)))])

(defn add-task []
  (let [new-id (swap! id-counter inc)]
    (swap! tasks conj [new-id @input-task])
    (reset! input-task "")))

(defn task-adder []
  [:div
    [:input
      {:type "text" :value @input-task :on-change #(reset! input-task (-> % .-target .-value)) :on-key-down #(case (-> % .-which) 13 (add-task) "default")}]
    [:input {:type "button" :value "Submit" :on-click add-task}]])



(defn home-page []
  [:div
    [:h2 "List of things to do!"]
    [task-adder]
    [render-tasks @current-task]])


;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
