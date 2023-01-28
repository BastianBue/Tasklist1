data class Event(val id: Int, val x: Int, val y: Int, val isLongClick: Boolean)

fun isEventLongClicked(events: MutableList<Event>, eventId: Int): Boolean? {
    for ((id, _, _, isLongClick) in events) {
        if ( id == eventId) return isLongClick
    }
    return null
}