package bruhcollective.itaysonlab.jetispot.core.collection.db.model2

interface CollectionEntry {
  fun ceId(): String
  fun ceUri(): String
  fun ceTimestamp(): Long
  fun ceModifyPredef(type: PredefCeType, dyn: String) {}
}

enum class PredefCeType {
  COLLECTION, EPISODES
}