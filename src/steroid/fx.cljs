(ns steroid.fx
  (:require-macros steroid.fx)
  (:require re-frame.core)
  (:refer-clojure :exclude [merge reduce]))

(defn- update-db [cofx fx]
  (if-let [db (:db fx)]
    (assoc cofx :db db)
    cofx))

(def ^:private default-interceptors (atom []))

(defn set-default-interceptors [value]
  (reset! default-interceptors value))

(defn get-interceptors [interceptors]
  [@default-interceptors interceptors])

(def ^:private mergeable-keys (atom #{}))

(defn set-mergeable-keys [value]
  (reset! mergeable-keys value))

(defn- safe-merge [fx new-fx]
  (if (:merging-fx-with-common-keys fx)
    fx
    (clojure.core/reduce (fn [merged-fx [k v]]
                           (if (= :db k)
                             (assoc merged-fx :db v)
                             (if (get merged-fx k)
                               (if (get @mergeable-keys k)
                                 (update merged-fx k into v)
                                 (reduced {:merging-fx-with-common-keys k}))
                               (assoc merged-fx k v))))
                         fx
                         new-fx)))

(defn merge
  "Takes a map of co-effects and forms as argument.
  The first optional form can be map of effects
  The next forms are functions applying effects and returning a map of effects.
  The fn ensures that updates to db are passed from function to function within the cofx :db key and
  that only a :merging-fx-with-common-keys effect is returned if some functions are trying
  to produce the same effects (excepted :db, :data-source/tx effects).
  :data-source/tx and effects are handled specially and their results
  (list of transactions) are compacted to one transactions list (for each effect). "
  [{:keys [db] :as cofx} & args]
  (let [[first-arg & rest-args] args
        initial-fxs? (map? first-arg)
        fx-fns (if initial-fxs? rest-args args)]
    (clojure.core/reduce (fn [fxs fx-fn]
                           (let [updated-cofx (update-db cofx fxs)]
                             (if fx-fn
                               (safe-merge fxs (fx-fn updated-cofx))
                               fxs)))
                         (if initial-fxs? first-arg {:db db})
                         fx-fns)))