package isel.tds.go.storage

interface Serializer <Data>{
    fun serialize(data: Data): String
    fun deserialize(text: String): Data

}