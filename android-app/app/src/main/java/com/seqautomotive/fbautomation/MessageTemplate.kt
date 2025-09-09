package com.seqautomotive.fbautomation

object MessageTemplate {
    
    fun generateMessage(sellerName: String?, carModel: String?): String {
        val name = sellerName ?: "there"
        val car = carModel ?: "vehicle"
        
        return """Hi $name,
I'm Jordan Lansbury from SEQ Automotive. We're interested in purchasing your $car and would like to offer $15,000. If this works for you, we can arrange a quick inspection (We come to you) and handle all transfers without a roadworthy and the payment processed before we pick up the vehicle.

Feel free to contact me at 0408187145 with any questions.

Best regards,
Jordan Lansbury
SEQ Automotive
www.seqautomotive.com.au"""
    }
}