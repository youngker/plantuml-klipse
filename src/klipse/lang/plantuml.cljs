(ns klipse.lang.plantuml
  (:require
   [ajax.core :refer [GET]]
   [goog.string :as gs]
   [goog.crypt.base64 :as gb64]
   [cljs.core.async :refer [chan put!]]
   [klipse.common.registry :refer [codemirror-mode-src register-mode]]))

(defn http-request [data handler]
  (let [format (str "@startuml\n" data "\n@enduml")
        base64 (gb64/encodeString (gs/urlEncode format))]
    (GET "/plantuml" {:params {:data base64}
                      :handler handler})))

(defn str-eval-async [exp _]
  (let [c (chan)
        handler (fn [resp] (put! c resp))]
    (http-request exp handler)
    c))

(def opts {:editor-in-mode "markdown"
           :editor-out-mode "htmlmixed"
           :external-scripts [(codemirror-mode-src "markdown") (codemirror-mode-src "htmlmixed")]
           :eval-fn str-eval-async
           :comment-str "'"})

(register-mode "eval-plantuml" "selector_eval_plantuml" opts)
