# re-frame-steroid

A Clojure library with a few functions-steroids for re-frame app

[![Clojars](https://img.shields.io/clojars/v/re-frame-steroid.svg)](https://clojars.org/re-frame-steroid)

## Usage

Views

```clojure
(:require [steroid.views :as views]))

(views/defview test-view []
  (views/letsubs [subs1 [:subs1]
                  subs2 [:subs2]]
    [text (str subs1 " wut " subs2)]))
```

```clojure
(views/defview test-view []
  (views/letsubs [subs1 [:subs1]]
    {:component-did-mount #(dosmth)}
    [text subs1]))
```

Events

```clojure
(:require [steroid.fx :as fx])

(fx/defn update-count
  {:events [:update-count]
   :interceptors [(re-frame/inject-cofx :now)]}
  [{:keys [db now]}]
  {:db (-> db
           (update :count inc)
           (assoc :now now))}) 

(fx/defn update-label
  {:events [:update-label]}
  [{:keys [db]} value]
  {:db (assoc db :label value)})
```

Merge events

```clojure
(fx/defn update-count-and-label
  {:events [:update-count-and-label]
   :interceptors [(re-frame/inject-cofx :now)]}
  [cofx label]
  (fx/merge cofx
            (update-count)
            (update-label label)))
```

```clojure
(re-frame/dispatch [:update-count-and-label "label"])
```


Global interceptors
```clojure
(def debug-handlers-names
  "Interceptor which logs debug information to js/console for each event."
  (->interceptor
   :id     :debug-handlers-names
   :before (fn debug-handlers-names-before [context]
             (log/debug "Handling re-frame event: " (pretty-print-event context))
             context)))

(fx/set-default-interceptors [debug-handlers-names])
```

Set mergeable keys
```clojure
(fx/set-mergeable-keys #{:key1 :key2})
```

Subs
```clojure
(:require [steroid.subs :as subs])

(subs/reg-root-subs #{:sub1 :sub2})

(subs/reg-root-sub :sub3)

(subs/reg-root-sub :sub4-name :sub4)
```


ENJOY!