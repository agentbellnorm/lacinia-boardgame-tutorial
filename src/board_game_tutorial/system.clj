(ns board-game-tutorial.system
  (:require [com.stuartsierra.component :as component]
            [board-game-tutorial.schema :as schema]
            [board-game-tutorial.server :as server]
            [file-db.db :as db]))

(defn new-system
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)
         (db/new-db)))
