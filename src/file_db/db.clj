(ns file-db.db
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.stuartsierra.component :as component]
    [board-game-tutorial.protocol.data-provider :as data-provider]))

(defn internal-find-game-by-id
  [db game-id]
  (->> db
       :data
       deref
       :games
       (filter #(= game-id (:id %)))
       first))

(defn internal-find-member-by-id
  [db member-id]
  (->> db
       :data
       deref
       :members
       (filter #(= member-id (:id %)))
       first))

(defn internal-list-designers-for-game
  [db game-id]
  (let [designers (:designers (internal-find-game-by-id db game-id))]
    (->> db
         :data
         deref
         :designers
         (filter #(contains? designers (:id %))))))

(defn internal-list-games-for-designer
  [db designer-id]
  (->> db
       :data
       deref
       :games
       (filter #(-> % :designers (contains? designer-id)))))

(defn internal-list-ratings-for-game
  [db game-id]
  (->> db
       :data
       deref
       :ratings
       (filter #(= game-id (:game_id %)))))

(defn internal-list-ratings-for-member
  [db member-id]
  (->> db
       :data
       deref
       :ratings
       (filter #(= member-id (:member_id %)))))

(defn ^:private apply-game-rating
  [game-ratings game-id member-id rating]
  (->> game-ratings
       (remove #(and (= game-id (:game_id %))
                     (= member-id (:member_id %))))
       (cons {:game_id   game-id
              :member_id member-id
              :rating    rating})))

(defn internal-upsert-game-rating
  "Adds a new game rating or changes the old value of an existing game rating"
  [db game-id member-id rating]
  (-> db
      :data
      (swap! update :ratings apply-game-rating game-id member-id rating)))


(defrecord BoardGameTutorialDb [data]

  component/Lifecycle

  (start [this] (assoc this :data (-> (io/resource "cgg-data.edn")
                          slurp
                          edn/read-string
                          atom)))

  (stop [this] (assoc this :data nil))

  data-provider/DataProviderProtocol

  (find-game-by-id [data id] (internal-find-game-by-id data id))
  (find-member-by-id [data id] (file-db.db/internal-find-member-by-id data id))
  (upsert-game-rating [data game-id member-id rating] (file-db.db/internal-upsert-game-rating data game-id member-id rating))
  (list-designers-for-game [data game-id] (file-db.db/internal-list-designers-for-game data game-id))
  (list-games-for-designer [data game-id] (file-db.db/internal-list-games-for-designer data game-id))
  (list-ratings-for-game [data game-id] (file-db.db/internal-list-ratings-for-game data game-id))
  (list-ratings-for-member [data member-id] (file-db.db/internal-list-ratings-for-member data member-id)))

(defn new-db
  []
  {:db (map->BoardGameTutorialDb {})})