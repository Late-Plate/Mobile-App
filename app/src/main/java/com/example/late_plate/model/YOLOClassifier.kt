package com.example.late_plate.model
import java.nio.ByteBuffer

interface YOLOClassifier{
    fun classify(byteBuffer: ByteBuffer,score:String):List<Classification>
}