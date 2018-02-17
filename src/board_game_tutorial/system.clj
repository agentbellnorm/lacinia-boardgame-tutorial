(ns board-game-tutorial.system
  (:require [com.stuartsierra.component :as component]
            [board-game-tutorial.schema :as schema]
            [board-game-tutorial.server :as server]))

(defn new-system
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)))
