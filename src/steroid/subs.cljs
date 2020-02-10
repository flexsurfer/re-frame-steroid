(ns steroid.subs
  (:require re-frame.core))

(defn reg-root-sub
  ([sub-name]
   (reg-root-sub sub-name sub-name))
  ([sub-name sub-key]
   (re-frame.core/reg-sub sub-name (fn [db] (get db sub-key)))))

(defn reg-root-subs [sub-keys]
  (doseq [sub-name sub-keys]
    (reg-root-sub sub-name)))