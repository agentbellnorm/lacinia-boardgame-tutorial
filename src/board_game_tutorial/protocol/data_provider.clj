(ns board-game-tutorial.protocol.data-provider)

(defprotocol DataProviderProtocol
  (find-game-by-id [data id])
  (find-member-by-id [data id])
  (upsert-game-rating [data game-id member-id rating])
  (list-designers-for-game [data game-id])
  (list-games-for-designer [data designer-id])
  (list-ratings-for-game [data game-id])
  (list-ratings-for-member [data member-id]))